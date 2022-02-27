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
import org.robovm.compiler.target.ios.IOSTarget;
import org.robovm.idea.running.pickers.IOSTargetTypePicker;
import org.robovm.idea.running.pickers.ModulePicker;

import javax.swing.*;

public class RoboVmIOSRunConfigurationSettingsEditor extends SettingsEditor<RoboVmIOSRunConfiguration> {
    private JPanel panel;
    private JTextArea args;
    private IOSTargetTypePicker targetSelectionPanel;
    private ModulePicker modulePicker;

    // true if editor internally updating data and listeners should ignore the events
    private boolean updatingData;

    @NotNull
    @Override
    protected JComponent createEditor() {
        // populate controls with stable data
        targetSelectionPanel.getDeviceSelector().populate();
        targetSelectionPanel.getSimulatorSelector().populate();
        modulePicker.populate();
        modulePicker.setListener(m -> {
            if (!updatingData) {
                targetSelectionPanel.moduleChanged(m);
            }
        });

        return panel;
    }

    @Override
    protected void resetEditorFrom(@NotNull RoboVmIOSRunConfiguration config) {
        try {
            updatingData = true;
            RoboVmIOSRunConfigurationOptions options = config.getOptions();
            modulePicker.applyDataFrom(config.getProject(), IOSTarget.TYPE::equals, options);
            targetSelectionPanel.getDeviceSelector().applyDataFrom(options);
            targetSelectionPanel.getSimulatorSelector().applyDataFrom(options);
            targetSelectionPanel.setTargetType(options.getTargetType());
            args.setText(options.getArguments());
        } finally {
            updatingData = false;
        }
    }

    @Override
    protected void applyEditorTo(@NotNull RoboVmIOSRunConfiguration config) throws ConfigurationException {
        RoboVmIOSRunConfigurationOptions options = config.getOptions();

        // validate all data
        targetSelectionPanel.validate();
        modulePicker.validate();

        // save all data
        options.setTargetType(targetSelectionPanel.getTargetType());
        // module
        options.setModule(modulePicker.getSelectedModuleName());
        // device related
        targetSelectionPanel.getDeviceSelector().saveDataTo(options);
        // simulator related
        targetSelectionPanel.getSimulatorSelector().saveDataTo(options);
    }
}
