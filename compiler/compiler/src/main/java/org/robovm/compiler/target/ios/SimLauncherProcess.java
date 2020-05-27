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
package org.robovm.compiler.target.ios;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.robovm.compiler.log.Logger;
import org.robovm.compiler.target.Launchers;
import org.robovm.compiler.target.Launchers.CustomizableLauncher;
import org.robovm.compiler.target.Launchers.Listener;
import org.robovm.compiler.util.Executor;
import org.robovm.compiler.util.io.NeverCloseOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Process} implementation which runs an app on a simulator using an
 * simctl
 */
public class SimLauncherProcess extends AbstractLauncherProcess<IOSSimulatorLaunchParameters> {
    private final File appDir;
    private final String bundleId;

    private SimLauncherProcess(Builder builder, File appDir, String bundleId) {
        super(builder);

        this.appDir = appDir;
        this.bundleId = bundleId;
    }

    public static CustomizableLauncher createLauncher(Logger log, Listener listener, IOSSimulatorLaunchParameters launchParameters,
                                                                File appDir, String bundleId) {
        return new Builder(log, listener, launchParameters, appDir, bundleId);
    }

    @Override
    protected int performLaunch() throws IOException {
        DeviceType deviceType = launchParameters.getDeviceType();
        File wd = launchParameters.getWorkingDirectory();
        ArrayList<String> arguments = new ArrayList<>(launchParameters.getArguments());
        Map<String, String> env = null;
        if (launchParameters.getEnvironment() != null) {
            env = new HashMap<>();
            for (Map.Entry<String, String> entry : launchParameters.getEnvironment().entrySet()) {
                env.put("SIMCTL_CHILD_" + entry.getKey(), entry.getValue());
            }
        }

        Executor executor;
        DeviceType freshState = deviceType.refresh();
        if (freshState != null && "shutdown".equals(freshState.getState().toLowerCase())) {
            log.info("Booting simulator %s", deviceType.getUdid());
            executor = new Executor(log, "xcrun");
            executor.args("simctl", "boot", deviceType.getUdid());
            executor.exec();
        }

        // bringing simulator to front (and showing it if it was just booted)
        log.info("Showing simulator %s", deviceType.getUdid());
        executor = new Executor(log, "open");
        executor.args("-a", "Simulator", "--args", "-CurrentDeviceUDID", deviceType.getUdid());
        executor.exec();

        log.info("Deploying app %s to simulator %s", appDir.getAbsolutePath(),
                deviceType.getUdid());
        executor = new Executor(log, "xcrun");
        executor.args("simctl", "install", deviceType.getUdid(), appDir.getAbsolutePath());
        executor.exec();

        // launch and deploy to paired watch simulator
        if (launchParameters.getPairedWatchAppName() != null && freshState != null  && freshState.getPair() != null) {
            DeviceType watchDeviceType = freshState.getPair();
            if ("shutdown".equals(watchDeviceType.getState().toLowerCase())) {
                log.info("Booting watch simulator %s", watchDeviceType.getUdid());
                executor = new Executor(log, "xcrun");
                executor.args("simctl", "boot", watchDeviceType.getUdid());
                executor.exec();
            }

            // bringing simulator to front (and showing it if it was just booted)
            log.info("Showing watch simulator %s", watchDeviceType.getUdid());
            executor = new Executor(log, "open");
            executor.args("-a", "Simulator", "--args", "-CurrentDeviceUDID", watchDeviceType.getUdid());
            executor.exec();

            File watchAppDir = new File(appDir, "Watch/" + launchParameters.getPairedWatchAppName());
            log.info("Deploying app %s to watch simulator %s", watchAppDir.getAbsolutePath(),
                    watchDeviceType.getUdid());
            executor = new Executor(log, "xcrun");
            executor.args("simctl", "install", watchDeviceType.getUdid(), watchAppDir.getAbsolutePath());
            executor.exec();
        }

        log.info("Launching app %s on simulator %s", appDir.getAbsolutePath(),
                deviceType.getUdid());
        executor = new Executor(log, "xcrun");
        List<Object> args = new ArrayList<>();
        args.add("simctl");
        args.add("launch");
        args.add("--console");
        args.add(deviceType.getUdid());
        args.add(bundleId);
        args.addAll(arguments);
        executor.args(args);

        if (env != null) {
            executor.env(env);
        }

        executor.wd(wd).out(out).err(err).closeOutputStreams(true).inheritEnv(false);
        executor.exec();
        return 0;
    }

    public static class Builder extends AbstractLauncherProcess.Builder<IOSSimulatorLaunchParameters> {
        private final File appDir;
        private final String bundleId;

        public Builder(Logger log, Listener listener, IOSSimulatorLaunchParameters launchParameters,
                       File appDir, String bundleId) {
            super(log, listener, launchParameters);
            this.appDir = appDir;
            this.bundleId = bundleId;
        }

        @Override
        protected AbstractLauncherProcess.Builder<IOSSimulatorLaunchParameters> duplicate() {
            return new Builder(log, listener, launchParameters, appDir, bundleId);
        }

        @Override
        protected AbstractLauncherProcess<IOSSimulatorLaunchParameters> createAndSetupThread(boolean async) throws IOException {
            // apply default streams if not configured
            if (async) {
                if (out == null) {
                    PipedInputStream sink = new PipedInputStream();
                    out = new ImmutablePair<>(new PipedOutputStream(sink), sink);
                }
                if (err == null) {
                    PipedInputStream sink = new PipedInputStream();
                    err = new ImmutablePair<>(new PipedOutputStream(sink), sink);
                }
                // input stream is not used
                in = new ImmutablePair<>(null, new NullOutputStream());
            } else {
                if (out == null)
                    out = new ImmutablePair<>(new NeverCloseOutputStream(System.out), null);
                if (err == null)
                    err = new ImmutablePair<>(new NeverCloseOutputStream(System.err), null);
                // input stream is not used
                in = new ImmutablePair<>(null, null);
            }
            return new SimLauncherProcess(this, appDir, bundleId);
        }

        @Override
        public Launchers.AsyncLauncherBuilder setIn(InputStream in, OutputStream inSink) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Launchers.SyncLauncherBuilder setIn(InputStream in) {
            throw new UnsupportedOperationException();
        }
    }
}
