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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.CpuArch;
import org.robovm.compiler.target.framework.FrameworkTarget;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.idea.running.pickers.BaseDecoratorAware;
import org.robovm.idea.running.pickers.DirectoryPicker;
import org.robovm.idea.running.pickers.ModulePicker;

import javax.swing.*;

public class RoboVmConsoleRunConfigurationSettingsEditor extends SettingsEditor<RoboVmRunConfiguration>
        implements BaseDecoratorAware {
    private JPanel panel;
    private JTextArea args;
    private JComboBox<CpuArch> deviceArch;
    private DirectoryPicker directoryPicker;
    private ModulePicker modulePicker;

    @NotNull
    @Override
    protected JComponent createEditor() {
        modulePicker.populate();
        directoryPicker.populate("Working directory", false);

        return panel;
    }

    @Override
    protected void resetEditorFrom(@NotNull RoboVmRunConfiguration config) {
        modulePicker.applyDataFrom(config.getProject(), FrameworkTarget::matches, config);

        // populate arch
        deviceArch.removeAllItems();
        if (DeviceType.DEFAULT_HOST_ARCH == CpuArch.arm64)
            deviceArch.addItem(CpuArch.arm64);
        deviceArch.addItem(CpuArch.x86_64);
        if (config.getDeviceArch() == CpuArch.x86_64 || config.getDeviceArch() == DeviceType.DEFAULT_HOST_ARCH)
            deviceArch.setSelectedItem(config.getDeviceArch());

        this.args.setText(config.getArguments());
        String dir = config.getWorkingDir();
        if (dir == null || dir.trim().isEmpty()) {
            dir = config.getProject().getBasePath();
        }
        directoryPicker.setDirectory(dir);
    }

    @Override
    protected void applyEditorTo(@NotNull RoboVmRunConfiguration config) throws ConfigurationException {
        // validate
        modulePicker.validate();
        directoryPicker.validate();
        if (deviceArch.getSelectedItem() == null)
            throw buildConfigurationException("Device architecture is not specified!", () -> deviceArch.setSelectedItem(DeviceType.DEFAULT_HOST_ARCH));

        config.setDeviceArch((CpuArch) deviceArch.getSelectedItem());
        config.setTargetType(RoboVmRunConfiguration.TargetType.Console);
        config.setArguments(args.getText());
        config.setWorkingDir(directoryPicker.getSelectedDirectory());
    }
}
