package org.robovm.idea.running

import com.intellij.execution.configurations.ModuleBasedConfigurationOptions
import org.robovm.compiler.config.CpuArch
import org.robovm.idea.running.pickers.DevicePickerConfig
import org.robovm.idea.running.pickers.IOSTargetTypePickerConfig
import org.robovm.idea.running.pickers.SimulatorPickerConfig
import org.robovm.idea.running.pickers.WorkingDirectoryPickerConfig

class RoboVmIOSRunConfigurationOptions : ModuleBasedConfigurationOptions(),
        SimulatorPickerConfig,
        DevicePickerConfig,
        IOSTargetTypePickerConfig,
        WorkingDirectoryPickerConfig{
    // device picker
    override var deviceArch: CpuArch? = null
    override var signingIdentityType: DevicePickerConfig.EntryType? = null
    override var signingIdentity: String? = null
    override var provisioningProfileType: DevicePickerConfig.EntryType? = null
    override var provisioningProfile: String? = null

    // simulator picker
    override var simulatorArch: CpuArch? = null
    override var simulatorType: SimulatorPickerConfig.EntryType? = null
    override var simulator: String? = null
    override var simulatorLaunchWatch: Boolean = false

    // target type selector
    override var targetType: IOSTargetTypePickerConfig.Target? = null

    // working directory
    override var workingDirectory: String? = null

    // arguments
    var arguments: String? = null
}