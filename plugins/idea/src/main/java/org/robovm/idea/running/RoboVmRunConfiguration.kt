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
package org.robovm.idea.running

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element
import org.robovm.compiler.AppCompiler
import org.robovm.compiler.config.Config
import org.robovm.compiler.config.CpuArch
import org.robovm.idea.RoboVmPlugin
import org.robovm.idea.running.pickers.BasePrimitiveConfig
import org.robovm.idea.running.pickers.DevicePickerConfig
import org.robovm.idea.running.pickers.SimulatorPickerConfig

class RoboVmRunConfiguration(private val configurationType: ConfigurationType, name: String,
                             configurationModule: RunConfigurationModule,
                             factory: ConfigurationFactory)
    : ModuleBasedConfiguration<RunConfigurationModule, Element>(name, configurationModule, factory),
        RunConfigurationWithSuppressedDefaultDebugAction,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunProfileWithCompileBeforeLaunchOption,
        BasePrimitiveConfig {

    // TODO: all these are deprecated
    enum class TargetType { Simulator, Device, Console }

    // promote visibility to public
    fun getModuleName(): String = options.module ?: ""
    override fun getType(): ConfigurationType = configurationType
    var targetType: TargetType? = null

    // TODO: rework these are just temporal proxies to options
    val signingIdentity: String?
        get() = options.signingIdentity
    val simulatorLaunchWatch: Boolean
        get() = options.simulatorLaunchWatch
    val workingDir: String?
        get() = options.workingDirectory
    val simulatorType: SimulatorPickerConfig.EntryType?
        get() = options.simulatorType
    val simulator: String?
        get() = options.simulator
    val provisioningProfileType: DevicePickerConfig.EntryType?
        get() = options.provisioningProfileType
    val provisioningProfile: String?
        get() = options.provisioningProfile
    val signingIdentityType: DevicePickerConfig.EntryType?
        get() = options.signingIdentityType
    val arguments: String?
        get() = options.arguments
    val simulatorArch: CpuArch?
        get() = options.simulatorArch
    val deviceArch: CpuArch?
        get() = options.deviceArch
    // TODO: end of proxies

    // these are used to pass information between
    // the compiler, the run configuration and the
    // runner. They are not persisted.
    var isDebug = false
    var config: Config? = null
    var debugPort = 0
    var compiler: AppCompiler? = null
    var programArguments: List<String>? = null

    init {
        setDefaultValues()
    }

    override fun getOptionsClass(): Class<out RunConfigurationOptions> {
        return RoboVmRunConfigurationOptions::class.java
    }

    public override fun getOptions(): RoboVmRunConfigurationOptions {
        return super.getOptions() as RoboVmRunConfigurationOptions
    }

    private fun setDefaultValues() {
        if (type is RoboVmIOSConfigurationType) {
            targetType = TargetType.Device

            options.setDefaultDevicePickerValues();
            options.setDefaultSimulatorPickerValues();
        } else if (type is RoboVmConsoleConfigurationType) {
            targetType = TargetType.Console
        }
    }


    override fun getValidModules(): Collection<Module> {
        return RoboVmPlugin.getRoboVmModules(configurationModule!!.project)
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return if (type is RoboVmIOSConfigurationType) {
            RoboVmIOSRunConfigurationSettingsEditor()
        } else {
            RoboVmConsoleRunConfigurationSettingsEditor()
        }
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return RoboVmRunProfileState(environment)
    }

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        super.readExternal(element)
        targetType = valueOf(TargetType::class.java, JDOMExternalizerUtil.readField(element, "targetType"))
        validateAndFix()
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, "targetType", toStringOrNull(targetType))
    }

    override fun setModuleName(moduleName: String?) {
        options.module = moduleName
    }

    /**
     * validates possibly wrong values and tries to fix
     */
    private fun validateAndFix() {
        if (type is RoboVmIOSConfigurationType) {
            if (targetType != TargetType.Device && targetType != TargetType.Simulator) targetType = TargetType.Device

            options.validateAndFixDevicePicker();
            options.validateAndFixSimulatorPicker();
        } else if (type is RoboVmConsoleConfigurationType) {
            // MacOsX console target
// FIXME: !
//            if (deviceArch != CpuArch.x86_64 && deviceArch != DeviceType.DEFAULT_HOST_ARCH)
//                deviceArch = DeviceType.DEFAULT_HOST_ARCH;
            targetType = TargetType.Console
        }
    }
}