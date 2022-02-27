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
import org.robovm.idea.running.pickers.WorkingDirectoryPickerConfig

class RoboVmRunConfiguration(private val configurationType: ConfigurationType, name: String,
                             configurationModule: RunConfigurationModule,
                             factory: ConfigurationFactory)
    : ModuleBasedConfiguration<RunConfigurationModule, Element>(name, configurationModule, factory),
        RunConfigurationWithSuppressedDefaultDebugAction,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunProfileWithCompileBeforeLaunchOption,
        BasePrimitiveConfig,
        SimulatorPickerConfig,
        DevicePickerConfig,
        WorkingDirectoryPickerConfig
{

    enum class TargetType {
        Simulator, Device, Console
    }

    // device target picker
    override var deviceArch: CpuArch? = null
    override var signingIdentityType: DevicePickerConfig.EntryType? = null
    override var signingIdentity: String? = null
    override var provisioningProfileType: DevicePickerConfig.EntryType? = null
    override var provisioningProfile: String? = null

    // simulator target picker
    override var simulatorArch: CpuArch? = null
    override var simulatorType: SimulatorPickerConfig.EntryType? = null
    override var simulator: String? = null
    override var simulatorLaunchWatch: Boolean = false

    // working directory config
    override var workingDirectory: String? = null

    // promote visibility to public
    fun getModuleName(): String = options.module ?: ""

    override fun getType(): ConfigurationType = configurationType

    var targetType: TargetType? = null
    var arguments: String? = null
    var workingDir: String? = null

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

    private fun setDefaultValues() {
        if (type is RoboVmIOSConfigurationType) {
            targetType = TargetType.Device

            setDefaultDevicePickerValues();
            setDefaultSimulatorPickerValues();
        } else if (type is RoboVmConsoleConfigurationType) {
            targetType = TargetType.Console
        }
    }

    public override fun getOptions(): ModuleBasedConfigurationOptions {
        return super.getOptions()
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
        arguments = JDOMExternalizerUtil.readField(element, "arguments", "")
        workingDir = JDOMExternalizerUtil.readField(element, "workingDir", "")

        readDevicePickerExternal(element);
        readSimulatorPickerExternal(element);
        validateAndFix()
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        JDOMExternalizerUtil.writeField(element, "targetType", toStringOrNull(targetType))
        JDOMExternalizerUtil.writeField(element, "arguments", arguments)
        JDOMExternalizerUtil.writeField(element, "workingDir", workingDir)

        writeDevicePickerExternal(element);
        writeSimulatorPickerExternal(element);
    }

    override fun setModuleName(moduleName: String?) {
        configurationModule!!.setModuleName(moduleName)
    }

    /**
     * validates possibly wrong values and tries to fix
     */
    private fun validateAndFix() {
        if (type is RoboVmIOSConfigurationType) {
            if (targetType != TargetType.Device && targetType != TargetType.Simulator) targetType = TargetType.Device

            validateAndFixDevicePicker();
            validateAndFixSimulatorPicker();
        } else if (type is RoboVmConsoleConfigurationType) {
            // MacOsX console target
// FIXME: !
//            if (deviceArch != CpuArch.x86_64 && deviceArch != DeviceType.DEFAULT_HOST_ARCH)
//                deviceArch = DeviceType.DEFAULT_HOST_ARCH;
            targetType = TargetType.Console
        }
    }
}