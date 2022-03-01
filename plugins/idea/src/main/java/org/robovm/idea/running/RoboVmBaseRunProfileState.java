/*
 * Copyright (C) 2015 RoboVM AB
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
package org.robovm.idea.running;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.AppCompiler;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.target.LaunchParameters;
import org.robovm.idea.RoboVmPlugin;

import java.io.IOException;

public abstract class RoboVmBaseRunProfileState<T extends RoboVmBaseRunConfiguration> extends CommandLineState {

    private int debugPort = -1;

    protected RoboVmBaseRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    protected abstract LaunchParameters createLaunchParameters(T runConfig, Config config) throws IOException, ExecutionException;

    protected ProcessHandler executeRun() throws Throwable {
        RunnerAndConfigurationSettings runnerAndConfigurationSettings = getEnvironment().getRunnerAndConfigurationSettings();
        if (runnerAndConfigurationSettings == null)
            throw new ExecutionException("RoboVmRunConfiguration is missing");
        //noinspection unchecked
        T runConfig = (T) runnerAndConfigurationSettings.getConfiguration();
        Config config = runConfig.getConfig();
        AppCompiler compiler = runConfig.getCompiler();
        runConfig.setConfig(null);
        runConfig.setCompiler(null);
        RoboVmPlugin.logInfo(getEnvironment().getProject(), "Launching executable");

        LaunchParameters launchParameters = createLaunchParameters(runConfig, config);
        Process process = config.getTarget().launch(launchParameters);
        RoboVmPlugin.logInfo(getEnvironment().getProject(), "Launch done");

        final OSProcessHandler processHandler = new ColoredProcessHandler(process, null);
        ProcessTerminatedListener.attach(processHandler);
        return processHandler;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        try {
            if (getEnvironment().getExecutor().getId().equals(RoboVmRunner.RUN_EXECUTOR)) {
                return executeRun();
            } else if (getEnvironment().getExecutor().getId().equals(RoboVmRunner.DEBUG_EXECUTOR)) {
                return executeRun();
            } else {
                throw new ExecutionException("Unsupported executor " + getEnvironment().getExecutor().getId());
            }
        } catch (Throwable t) {
            RoboVmPlugin.logErrorThrowable(getEnvironment().getProject(), "Couldn't start application", t, true);
            throw new ExecutionException(t);
        }
    }


    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }
}
