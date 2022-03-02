/*
 * Copyright (C) 2012 RoboVM AB
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

import org.robovm.compiler.util.io.ObservableOutputStream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class LaunchParameters {
    private List<String> arguments = new ArrayList<>();
    private Map<String, String> environment = null;
    private File workingDirectory = new File(".");
    private File appDirectory = null;
    private ObservableOutputStream.Observer stdOutObserver = null;

    public List<String> getArguments() {
        return arguments;
    }

    public List<String> getArguments(boolean rvmReorder) {
        if (rvmReorder) {
            // filter arguments to have all -rvm: to be present in front of any other user specified
            // otherwise robovm will just stop parsing JVM parameters at first non `-rvm:` one
            List<String> rvmArgs = new ArrayList<>();
            List<String> userArgs = new ArrayList<>();
            for (String arg : arguments) {
                if (arg.startsWith("-rvm:"))
                    rvmArgs.add(arg);
                else
                    userArgs.add(arg);
            }
            rvmArgs.addAll(userArgs);
            return rvmArgs;
        } else {
            return arguments;
        }
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
    
    public Map<String, String> getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }
    
    public File getWorkingDirectory() {
        return workingDirectory;
    }
    
    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public File getAppDirectory() {
        return appDirectory;
    }

    public void setAppDirectory(File appDirectory) {
        this.appDirectory = appDirectory;
    }

    public ObservableOutputStream.Observer getStdOutObserver() {
        return stdOutObserver;
    }

    public void setStdOutObserver(ObservableOutputStream.Observer stdOutObserver) {
        this.stdOutObserver = stdOutObserver;
    }
}
