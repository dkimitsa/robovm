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

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.robovm.compiler.AppCompiler;
import org.robovm.compiler.config.Config;
import org.robovm.idea.RoboVmPlugin;
import org.robovm.idea.running.RoboVmRunConfiguration.TargetType;
import org.robovm.idea.running.pickers.DevicePickerConfig;
import org.robovm.idea.running.pickers.SimulatorPickerConfig;

import java.util.Collection;
import java.util.List;

public class RoboVmIOSFrameworkRunConfiguration extends ModuleBasedConfiguration<RunConfigurationModule, Element> implements
        RunConfigurationWithSuppressedDefaultDebugAction, RunConfigurationWithSuppressedDefaultRunAction,
        RunProfileWithCompileBeforeLaunchOption,
        SimulatorPickerConfig,
        DevicePickerConfig
{

    private TargetType targetType;
    private final DevicePickerConfig.Bucket devicePickerConfigBucket = new DevicePickerConfig.Bucket();
    private final SimulatorPickerConfig.Bucket simulatorPickerConfigBucket = new SimulatorPickerConfig.Bucket();

    @Override
    public DevicePickerConfig.Bucket devicePickerBucket() {
        return devicePickerConfigBucket;
    }

    @Override
    public SimulatorPickerConfig.Bucket simulatorPickerBucket() {
        return simulatorPickerConfigBucket;
    }


    private String arguments;
    private String workingDir;

    // these are used to pass information between
    // the compiler, the run configuration and the
    // runner. They are not persisted.
    private boolean isDebug;
    private Config config;
    private int debugPort;
    private AppCompiler compiler;
    private ConfigurationType type;
    private List<String> programArguments;

    public RoboVmIOSFrameworkRunConfiguration(ConfigurationType type, String name, RunConfigurationModule configurationModule, ConfigurationFactory factory) {
        super(name, configurationModule, factory);
        this.type = type;
        this.setDefaultValues();
    }

    private void setDefaultValues() {
        if (type instanceof RoboVmIOSConfigurationType) {
            targetType = TargetType.Device;

            setDefaultDevicePickerValues();
            setDefaultSimulatorPickerValues();
        } else if (type instanceof RoboVmConsoleConfigurationType) {
            targetType = TargetType.Console;
        }
    }

    @Override
    public Collection<Module> getValidModules() {
        return RoboVmPlugin.getRoboVmModules(getConfigurationModule().getProject());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RoboVmIOSFrameworkRunConfigurationSettingsEditor();
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new RoboVmRunProfileState(environment);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);

        targetType = valueOf(TargetType.class, JDOMExternalizerUtil.readField(element, "targetType"));
        arguments = JDOMExternalizerUtil.readField(element, "arguments", "");
        workingDir = JDOMExternalizerUtil.readField(element, "workingDir", "");

        readDevicePickerExternal(element);
        readSimulatorPickerExternal(element);
        validateAndFix();
    }

    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);

        JDOMExternalizerUtil.writeField(element, "targetType", toStringOrNull(targetType));
        JDOMExternalizerUtil.writeField(element, "arguments", arguments);
        JDOMExternalizerUtil.writeField(element, "workingDir", workingDir);
        writeDevicePickerExternal(element);
        writeSimulatorPickerExternal(element);
    }

    public String getModuleName() {
        return getConfigurationModule().getModuleName();
    }

    @Override
    public void setModuleName(String moduleName) {
        getConfigurationModule().setModuleName(moduleName);
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setCompiler(AppCompiler compiler) {
        this.compiler = compiler;
    }

    public AppCompiler getCompiler() {
        return compiler;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setProgramArguments(List<String> programArguments) {
        this.programArguments = programArguments;
    }

    public List<String> getProgramArguments() {
        return programArguments;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    /**
     * validates possibly wrong values and tries to fix
     */
    private void validateAndFix() {
        if (type instanceof RoboVmIOSConfigurationType) {
            if (targetType != TargetType.Device && targetType != TargetType.Simulator)
                targetType = TargetType.Device;

            validateAndFixDevicePicker();
            validateAndFixSimulatorPicker();
        } else if (type instanceof RoboVmConsoleConfigurationType) {
            // MacOsX console target
// FIXME: !
//            if (deviceArch != CpuArch.x86_64 && deviceArch != DeviceType.DEFAULT_HOST_ARCH)
//                deviceArch = DeviceType.DEFAULT_HOST_ARCH;
            targetType = TargetType.Console;
        }
    }
}
