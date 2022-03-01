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

import java.io.File;
import java.io.IOException;

public class RoboVmConsoleRunProfileState extends RoboVmBaseRunProfileState<RoboVmConsoleRunConfiguration> {
    public RoboVmConsoleRunProfileState(ExecutionEnvironment environment) {
        super(environment);
    }

    @Override
    protected LaunchParameters createLaunchParameters(RoboVmConsoleRunConfiguration runConfig, Config config) throws IOException, ExecutionException {
        // TODO: FIXME: this method to be refactored and these parameters to be provided

        LaunchParameters launchParameters = config.getTarget().createLaunchParameters();
        if (runConfig.getWorkingDir() != null && !runConfig.getWorkingDir().isEmpty()) {
            launchParameters.setWorkingDirectory(new File(runConfig.getWorkingDir()));
        }

        return launchParameters;
    }
}
