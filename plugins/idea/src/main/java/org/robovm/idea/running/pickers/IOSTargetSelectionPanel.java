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
package org.robovm.idea.running.pickers;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import org.robovm.idea.running.RoboVmRunConfiguration.TargetType;

import javax.swing.*;

/**
 * Panel that combines simulator and device selection widgets, adds radio buttons to select target type
 */
public class IOSTargetSelectionPanel {
    private IOSSimulatorPicker simulatorSelector;
    private IOSDevicePicker deviceSelector;
    private JRadioButton attachedDeviceRadioButton;
    private JRadioButton simulatorRadioButton;
    @SuppressWarnings("unused") // root panel required otherwise produces No binding on root component of nested form
    private JPanel panel;

    public IOSSimulatorPicker getSimulatorSelector() {
        return simulatorSelector;
    }

    public IOSDevicePicker getDeviceSelector() {
        return deviceSelector;
    }

    public JRadioButton getAttachedDeviceRadioButton() {
        return attachedDeviceRadioButton;
    }

    public JRadioButton getSimulatorRadioButton() {
        return simulatorRadioButton;
    }

    public TargetType getTargetType() {
        return (attachedDeviceRadioButton.isSelected()) ? TargetType.Device : TargetType.Simulator;
    }

    public void setTargetType(TargetType type) {
        attachedDeviceRadioButton.setSelected(type == TargetType.Device);
    }

    public void moduleChanged(Module module) {
        // module has been changed, show/hide watch checkbox
        simulatorSelector.setModuleHasWatchApp(IOSSimulatorPicker.isWatchConfigured(module));
    }

    public void validate() throws ConfigurationException {
        simulatorSelector.validate();
        deviceSelector.validate();
    }
}
