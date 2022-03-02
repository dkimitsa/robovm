/*
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.target;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.robovm.compiler.log.ErrorOutputStream;
import org.robovm.compiler.log.Logger;
import org.robovm.compiler.util.io.ObservableOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

import static org.apache.commons.exec.Executor.INVALID_EXITVALUE;

/**
 * Container class for all primitive related to launchers
 */
public abstract class Launchers {
    protected Launchers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Synchronous launcher, uses standard IO input/output streams if not overridden
     */
    public interface SyncLauncher {
        int exec() throws IOException, InterruptedException;
    }

    /**
     * Asynchronous launcher produces process that launch asynchronously.
     * uses piped in/out streams that are piped to corresponding sinks of process
     */
    public interface AsyncLauncher {
        Process execAsync() throws IOException;
    }

    /**
     * Sync launcher builder that allows output/input streams to be redirected
     * (not all implementations support every stream)
     */
    public interface SyncLauncherBuilder extends SyncLauncher {
        SyncLauncherBuilder setOut(OutputStream out);

        SyncLauncherBuilder setErr(OutputStream err);

        SyncLauncherBuilder setIn(InputStream in);
    }

    /**
     * Async launcher builder that allows output/input streams to be redirected
     * (not all implementations support every stream)
     */
    public interface AsyncLauncherBuilder extends AsyncLauncher {
        AsyncLauncherBuilder setOut(OutputStream out, InputStream outSink);

        AsyncLauncherBuilder setErr(OutputStream err, InputStream errSink);

        AsyncLauncherBuilder setIn(InputStream in, OutputStream inSink);
    }

    /**
     * Launcher that supports async and sync launch methods
     */
    public interface Launcher extends SyncLauncher, AsyncLauncher {
    }

    /**
     * Sync/Async laycher that also provide option for streams customization
     */
    public interface CustomizableLauncher extends Launcher {
        SyncLauncherBuilder customizeExec();
        AsyncLauncherBuilder customizeExecAsync();
    }

    /**
     * interface that allows to terminate running process (e.g. from debugger)
     */
    public interface Killable {
        void terminate();
    }

    /**
     * Launcher progress listener
     */
    public interface Listener {
        void beforeLaunch();
        void justLaunched(Killable monitor);
        void launchFinished();
    }

    /**
     * Base thread for launchers
     * Provides common api and functionality like:
     * - calling launcher call-backs
     * - provides locking and wait logic
     * @param <T> type of launch parameter
     */
    protected abstract static class LauncherThread<T extends LaunchParameters> extends Thread implements Killable {
        protected final Logger log;
        protected final Listener listener;
        protected final T launchParameters;

        protected final CountDownLatch countDownLatch = new CountDownLatch(1);
        private volatile boolean cleanedUp = false;
        private volatile int exitCode = -1;

        // input/output redirection. if not set -- launcher specific will be used
        protected final OutputStream out;
        protected final OutputStream err;
        protected final InputStream in;

        public final InputStream waitInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new InterruptedIOException();
                }
                return -1;
            }
        };

        protected LauncherThread(Logger log, Listener listener, T launchParameters,
                                 OutputStream out, OutputStream err, InputStream in) {
            // notify right in the bottom of constructors. this allows all launchers to
            // use finished launchParameters at constructor level
            listener.beforeLaunch();

            this.log = log;
            this.listener = listener;
            this.launchParameters = launchParameters;
            if (launchParameters.getStdOutObserver() != null)
                out = new ObservableOutputStream(out, launchParameters.getStdOutObserver());
            this.out = out;
            this.err = err;
            this.in = in;

            setName(getThreadName());
        }

        protected LauncherThread(Builder<T> builder) {
            this(builder.log, builder.listener, builder.launchParameters,
                    builder.out.getLeft(), builder.err.getLeft(), builder.in.getLeft());
        }

        /**
         * implementations expected to do all launcher work in this method
         * @return exit code of launch
         * @throws IOException
         */
        protected abstract int performLaunch() throws IOException;

        @Override
        public void run() {
            listener.justLaunched(this);

            try {
                exitCode = performLaunch();
            } catch (ExecuteException e) {
                exitCode = e.getExitValue();
                // if process is interrupted Apache Executor will use this constant, replace with 0 otherwise
                // -559038737 looks odd in console output
                if (exitCode == INVALID_EXITVALUE)
                    exitCode = 0;
            } catch (Throwable t) {
                log.error(getLauncherName() + " failed with an exception:", t.getMessage());
                t.printStackTrace(new PrintStream(new ErrorOutputStream(log), true));
            } finally {
                countDownLatch.countDown();
                cleanUp();
            }
        }

        protected String getLauncherName() {
            return this.getClass().getSimpleName().replace("Process", "");
        }

        protected String getThreadName() {
            return getLauncherName() + "Thread";
        }

        public final int waitFor() throws InterruptedException {
            countDownLatch.await();
            return exitCode;
        }

        private void cleanUp() {
            synchronized (this) {
                if (!cleanedUp) {
                    cleanedUp = true;
                    listener.launchFinished();
                }
            }
        }

        @Override
        public void terminate() {
            if (isAlive()) {
                try {
                    interrupt();
                    join();
                } catch (InterruptedException ignored) {
                }
            }
            cleanUp();
        }
    }

    /**
     * builder for synchronous launcher
     */
    protected static abstract class Builder<T extends LaunchParameters> implements CustomizableLauncher, SyncLauncherBuilder, AsyncLauncherBuilder {
        protected final Logger log;
        protected final Listener listener;
        protected final T launchParameters;

        // input/output stream and their related sinks (for process)
        protected Pair<OutputStream, InputStream> out;
        protected Pair<OutputStream, InputStream> err;
        protected Pair<InputStream, OutputStream> in;

        public Builder(Logger log, Listener listener, T launchParameters) {
            this.log = log;
            this.listener = listener;
            this.launchParameters = launchParameters;
        }

        /// subclasses should return a duplicate of object to be returned as customization ones
        protected abstract Builder<T> duplicate();

        /// subclass expected to create launcher thread and finish initialization there
        protected abstract LauncherThread<T> createAndSetupThread(boolean async) throws IOException;

        @Override
        public SyncLauncherBuilder setOut(OutputStream out) {
            this.out = new ImmutablePair<>(out, null);
            return this;
        }

        @Override
        public SyncLauncherBuilder setErr(OutputStream err) {
            this.err = new ImmutablePair<>(err, null);
            return this;
        }

        @Override
        public SyncLauncherBuilder setIn(InputStream in) {
            this.in = new ImmutablePair<>(in, null);
            return this;
        }

        @Override
        public AsyncLauncherBuilder setOut(OutputStream out, InputStream outSink) {
            this.out = new ImmutablePair<>(out, outSink);
            return this;
        }

        @Override
        public AsyncLauncherBuilder setErr(OutputStream err, InputStream errSink) {
            this.err = new ImmutablePair<>(err, errSink);
            return this;
        }

        @Override
        public AsyncLauncherBuilder setIn(InputStream in, OutputStream inSink) {
            this.in = new ImmutablePair<>(in, inSink);
            return this;
        }

        @Override
        public int exec() throws IOException, InterruptedException {
            LauncherThread<T> thread = createAndSetupThread(false);
            thread.start();
            return thread.waitFor();
        }

        @Override
        public Process execAsync() throws IOException {
            LauncherThread<T> thread = createAndSetupThread(true);
            Process process = new LauncherProcess(thread, in.getRight(), out.getRight(), err.getRight());
            thread.start();
            return process;
        }

        @Override
        public SyncLauncherBuilder customizeExec() {
            return duplicate();
        }

        @Override
        public AsyncLauncherBuilder customizeExecAsync() {
            return duplicate();
        }
    }

    /**
     * Process wrapper around launcher thread
     */
    protected static class LauncherProcess extends Process {
        private final LauncherThread<?> thread;
        // streams will be initialized once before starting the thread
        private final OutputStream inSink;
        private final InputStream outSink;
        private final InputStream errSink;

        public LauncherProcess(LauncherThread<?> thread, OutputStream inSink, InputStream outSink, InputStream errSink) {
            this.thread = thread;
            this.inSink = inSink;
            this.outSink = outSink;
            this.errSink = errSink;
        }

        @Override
        public final OutputStream getOutputStream() {
            return inSink;
        }

        @Override
        public final InputStream getInputStream() {
            return outSink;
        }

        @Override
        public final InputStream getErrorStream() {
            return errSink;
        }

        @Override
        public final int waitFor() throws InterruptedException {
            return thread.waitFor();
        }

        @Override
        public final int exitValue() {
            if (thread.isAlive()) {
                throw new IllegalThreadStateException("Not terminated");
            }
            return thread.exitCode;
        }

        @Override
        public final void destroy() {
            thread.terminate();
        }
    }
}
