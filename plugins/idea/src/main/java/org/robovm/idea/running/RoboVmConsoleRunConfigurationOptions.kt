package org.robovm.idea.running

import com.intellij.execution.configurations.ModuleBasedConfigurationOptions
import org.robovm.compiler.config.CpuArch
import org.robovm.idea.running.pickers.WorkingDirectoryPickerConfig

class RoboVmConsoleRunConfigurationOptions : ModuleBasedConfigurationOptions(),
        WorkingDirectoryPickerConfig{
    // working directory
    override var workingDirectory: String? = null

    // arguments
    var arguments: String? = null

    // target CPU type
    var targetArch: CpuArch? = null
}