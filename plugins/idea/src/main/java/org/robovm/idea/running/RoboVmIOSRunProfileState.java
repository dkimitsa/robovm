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
import com.intellij.execution.runners.ExecutionEnvironment;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.target.LaunchParameters;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.compiler.target.ios.IOSSimulatorLaunchParameters;
import org.robovm.compiler.util.io.Fifos;

import java.io.IOException;

public class RoboVmIOSRunProfileState extends RoboVmBaseRunProfileState<RoboVmIOSRunConfiguration> {
    public RoboVmIOSRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected LaunchParameters createLaunchParameters(RoboVmIOSRunConfiguration runConfig, Config config) throws IOException, ExecutionException {

        // TODO: FIXME: this method to be refactored and these parameters to be provided

        LaunchParameters launchParameters = config.getTarget().createLaunchParameters();

        launchParameters.setStdoutFifo(Fifos.mkfifo("stdout"));
        launchParameters.setStderrFifo(Fifos.mkfifo("stderr"));
        if (launchParameters instanceof IOSSimulatorLaunchParameters) {
            IOSSimulatorLaunchParameters simParams = (IOSSimulatorLaunchParameters) launchParameters;
            // finding exact simulator to run at
            DeviceType exactType = RoboVmRunConfigurationUtils.getSimulator(runConfig.getOptions());
            if (exactType == null)
                throw new ExecutionException("Simulator type is not set or is not available anymore!");
            simParams.setDeviceType(exactType);
            simParams.setPairedWatchAppName(config.getWatchKitApp() != null && runConfig.getSimulatorLaunchWatch()
                    ? config.getWatchKitApp().getWatchAppName() : null);
        }

        launchParameters.setArguments(runConfig.getProgramArguments());
        return null;
    }
}
