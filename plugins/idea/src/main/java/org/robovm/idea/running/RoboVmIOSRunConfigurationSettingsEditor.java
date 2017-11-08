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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.compiler.target.ios.ProvisioningProfile;
import org.robovm.compiler.target.ios.SigningIdentity;
import org.robovm.idea.RoboVmPlugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;

public class RoboVmIOSRunConfigurationSettingsEditor extends SettingsEditor<RoboVmRunConfiguration> {
    private static final Arch[] DEVICE_ARCHS = { Arch.thumbv7, Arch.arm64 };
    private static final Arch[] SIMULATOR_ARCHS = { Arch.x86, Arch.x86_64 };

    public static final String SKIP_SIGNING = "Don't sign";
    public static final String AUTO_SIGNING_IDENTITY = "Auto (matches 'iPhone Developer|iOS Development')";
    public static final String AUTO_PROVISIONING_PROFILE = "Auto";

    private JPanel panel;
    private JTabbedPane tabbedPane1;
    private JComboBox module;
    private JRadioButton attachedDeviceRadioButton;
    private JRadioButton simulatorRadioButton;
    private JComboBox simType;
    private JComboBox signingIdentity;
    private JComboBox provisioningProfile;
    private JComboBox simArch;
    private JComboBox deviceArch;
    private JTextArea args;

    @Override
    protected void resetEditorFrom(RoboVmRunConfiguration config) {
        populateControls(config);
    }

    @Override
    protected void applyEditorTo(RoboVmRunConfiguration config) throws ConfigurationException {
        config.setModuleName(module.getSelectedItem() != null ? module.getSelectedItem().toString() : "");
        config.setTargetType(attachedDeviceRadioButton.isSelected() ? RoboVmRunConfiguration.TargetType.Device
                : RoboVmRunConfiguration.TargetType.Simulator);
        config.setDeviceArch((Arch) deviceArch.getSelectedItem());
        config.setSigningIdentity(signingIdentity.getSelectedItem().toString());
        config.setProvisioningProfile(provisioningProfile.getSelectedItem().toString());
        config.setSimArch((Arch) simArch.getSelectedItem());
        // there is no simulator on linux/windows
        if (simType.getSelectedItem() != null)
            config.setSimulatorName(((SimTypeWrapper) simType.getSelectedItem()).getType().getDeviceName());
        config.setArguments(args.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return panel;
    }

    private void populateControls(RoboVmRunConfiguration config) {

        updateModuleConfig(config);
        updateDeviceConfig(config);
        updateSimulatorConfig(config);

        attachedDeviceRadioButton.setSelected(config.getTargetType() == RoboVmRunConfiguration.TargetType.Device ||
                !simulatorRadioButton.isEnabled());
        args.setText(config.getArguments());
    }

    private void updateModuleConfig(RoboVmRunConfiguration config) {
        // populate with RoboVM Sdk modules
        this.module.removeAllItems();
        List<Module> roboVmModules = RoboVmPlugin.getRoboVmModules(config.getProject());
        Collections.sort(roboVmModules, new Comparator<Module>() {
            @Override
            public int compare(Module o1, Module o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Module module : roboVmModules) {
            this.module.addItem(module.getName());
            if (module.getName().equals(config.getModuleName())) {
                this.module.setSelectedIndex(this.module.getItemCount() - 1);
                config.setModule(module);
            }
        }
    }

    private void updateSimulatorConfig(RoboVmRunConfiguration config) {
        simType.removeAllItems();
        simArch.removeAllItems();

        List<DeviceType> deviceTypes = DeviceType.listDeviceTypes();
        simType.setEnabled(!deviceTypes.isEmpty());
        simArch.setEnabled(!deviceTypes.isEmpty());
        simulatorRadioButton.setEnabled(!deviceTypes.isEmpty());
        if (deviceTypes.isEmpty())
            return;

        // set simulator types
        for (DeviceType type : deviceTypes) {
            simType.addItem(new SimTypeWrapper(type));
            if (type.getDeviceName().equals(config.getSimulatorName())) {
                simType.setSelectedIndex(simType.getItemCount() - 1);
            } else if (config.getSimulatorName().isEmpty() && type.getDeviceName().contains("iPhone-6") && !type.getDeviceName().contains("Plus")) {
                simType.setSelectedIndex(simType.getItemCount() - 1);
            }
        }

        // set default arch for selected simulator
        SimTypeWrapper wrapper = (SimTypeWrapper) simType.getSelectedItem();
        if (wrapper != null) {
            for (Arch arch : SIMULATOR_ARCHS) {
                for (Arch otherArch : wrapper.getType().getArchs()) {
                    if (arch == otherArch) {
                        simArch.addItem(otherArch);
                        if (otherArch == config.getSimArch()) {
                            simArch.setSelectedItem(otherArch);
                        } else if (config.getSimArch() == null && otherArch == Arch.x86_64) {
                            simArch.setSelectedItem(otherArch);
                        }
                    }
                }
            }
        }

        // set a listener that populates the arch of a selected
        // sim properly.
        simType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateSimArchs((SimTypeWrapper) e.getItem());
            }
        });
    }

    private void updateSimArchs(SimTypeWrapper wrapper) {
        simArch.removeAllItems();
        for (Arch arch : SIMULATOR_ARCHS) {
            for (Arch otherArch : wrapper.getType().getArchs()) {
                if (arch == otherArch) {
                    simArch.addItem(otherArch);
                    if (otherArch == Arch.x86_64) {
                        simArch.setSelectedItem(otherArch);
                    }
                    break;
                }
            }
        }
    }

    private void updateDeviceConfig(RoboVmRunConfiguration config) {
        deviceArch.removeAllItems();
        signingIdentity.removeAllItems();
        provisioningProfile.removeAllItems();

        // populate signing identities
        signingIdentity.addItem(AUTO_SIGNING_IDENTITY);
        signingIdentity.addItem(SKIP_SIGNING);
        for (SigningIdentity identity : SigningIdentity.list()) {
            signingIdentity.addItem(identity.getName());
            if (identity.getName().equals(config.getSigningIdentity())) {
                signingIdentity.setSelectedIndex(signingIdentity.getItemCount() - 1);
            }
        }

        // populate provisioning profiles
        provisioningProfile.addItem(AUTO_PROVISIONING_PROFILE);
        for (ProvisioningProfile profile : ProvisioningProfile.list()) {
            provisioningProfile.addItem(profile.getName());
            if (profile.getName().equals(config.getProvisioningProfile())) {
                provisioningProfile.setSelectedIndex(provisioningProfile.getItemCount() - 1);
            }
        }

        // populate device archs
        for (Arch arch : DEVICE_ARCHS) {
            deviceArch.addItem(arch);
            if (arch == config.getDeviceArch()) {
                deviceArch.setSelectedItem(arch);
            }
        }
    }

    private class SimTypeWrapper {
        private final DeviceType type;

        public SimTypeWrapper(DeviceType type) {
            this.type = type;
        }

        public DeviceType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type.getSimpleDeviceName() + " - " + type.getSdk().getVersion();
        }
    }
}
