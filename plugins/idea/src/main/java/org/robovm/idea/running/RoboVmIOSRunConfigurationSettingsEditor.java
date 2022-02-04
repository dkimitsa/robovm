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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.config.CpuArch;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.compiler.target.ios.IOSTarget;
import org.robovm.compiler.target.ios.ProvisioningProfile;
import org.robovm.compiler.target.ios.SigningIdentity;
import org.robovm.idea.RoboVmPlugin;
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;

import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.robovm.idea.running.RoboVmRunConfiguration.AUTO_PROVISIONING_PROFILE;
import static org.robovm.idea.running.RoboVmRunConfiguration.AUTO_SIGNING_IDENTITY;

public class RoboVmIOSRunConfigurationSettingsEditor extends SettingsEditor<RoboVmRunConfiguration>
        implements BaseDecoratorAware
{
    private JPanel panel;
    private JTabbedPane tabbedPane1;
    private JComboBox<ModuleNameDecorator> module;
    private JTextArea args;
    private RoboVmIOSTargetSelectionPanel targetSelectionPanel;

    // copy of data that is time consuming to fetch (fetched only once when dialog is created)
    private List<ModuleNameDecorator> roboVmModules;
    // true if editor internally updating data and listeners should ignore the events
    private boolean updatingData;

    @NotNull
    @Override
    protected JComponent createEditor() {
        // populate controls with stable data
        targetSelectionPanel.getDeviceSelector().populate();
        targetSelectionPanel.getSimulatorSelector().populate();
        roboVmModules = null; // will be populated once modules list is known

//        module.addActionListener(e -> {
//            if (!updatingData)
//                updatePairedWatch(true, null);
//        });

        return panel;
    }

    @Override
    protected void resetEditorFrom(@NotNull RoboVmRunConfiguration config) {
        try {
            updatingData = true;
            module.setSelectedItem(getModuleFromConfig(config));
            targetSelectionPanel.getDeviceSelector().applyDataFrom(config);
            targetSelectionPanel.getSimulatorSelector().applyDataFrom(config);

            targetSelectionPanel.getAttachedDeviceRadioButton().setSelected(config.getTargetType() == RoboVmRunConfiguration.TargetType.Device ||
                    targetSelectionPanel.getSimulatorSelector(). .getItemCount() == 0);
            args.setText(config.getArguments());
        } finally {
            updatingData = false;
        }
    }

    @Override
    protected void applyEditorTo(@NotNull RoboVmRunConfiguration config) throws ConfigurationException {
        // validate all data
        if (module.getSelectedItem() == null)
            throw new ConfigurationException("RoboVM module is not specified!");
        if (simType.getSelectedItem() == null)
            throw buildConfigurationException("Simulator is not specified!", () -> simType.setSelectedItem(simulatorAutoIPhone));
        if (simArch.getSelectedItem() == null)
            throw buildConfigurationException("Simulator architecture is not specified!", () -> simArch.setSelectedIndex(0));
        if (deviceArch.getSelectedItem() == null)
            throw buildConfigurationException("Device architecture is not specified!", () -> deviceArch.setSelectedItem(Arch.arm64));
        if (signingIdentity.getSelectedItem() == null)
            throw buildConfigurationException("Signing identity is not specified!", () -> signingIdentity.setSelectedItem(signingIdentityAuto));
        if (provisioningProfile.getSelectedItem() == null)
            throw buildConfigurationException("Provisioning profile is not specified!", () -> provisioningProfile.setSelectedItem(provisioningProfileAuto));

        // save all data
        config.setModuleName(Decorator.from(module).name);
        config.setTargetType(attachedDeviceRadioButton.isSelected() ? RoboVmRunConfiguration.TargetType.Device : RoboVmRunConfiguration.TargetType.Simulator);
        // device related
        config.setDeviceArch((CpuArch) deviceArch.getSelectedItem());
        config.setSigningIdentityType(Decorator.from(signingIdentity).entryType);
        config.setSigningIdentity(Decorator.from(signingIdentity).id);
        config.setProvisioningProfileType(Decorator.from(provisioningProfile).entryType);
        config.setProvisioningProfile(Decorator.from(provisioningProfile).id);
        // simulator related
        config.setSimulatorArch((CpuArch) simArch.getSelectedItem());
        config.setSimulatorType(Decorator.from(simType).entryType);
        config.setSimulator(Decorator.from(simType).id);
        config.setSimulatorSdk(-1); // legacy, will not be used
        config.setArguments(args.getText());
        config.setSimulatorLaunchWatch(pairedWatch.isSelected());
    }

    private void populateModules(@NotNull RoboVmRunConfiguration config) {
        if (roboVmModules != null)
            return;

        this.roboVmModules = RoboVmPlugin.getRoboVmModules(config.getProject(), IOSTarget.TYPE).stream()
                .map(ModuleNameDecorator::new)
                .collect(Collectors.toList());

        // populate with RoboVM Sdk modules
        this.module.removeAllItems();
        this.roboVmModules.forEach(m -> module.addItem(m));
    }

    private ModuleNameDecorator getModuleFromConfig(RoboVmRunConfiguration config) {
        populateModules(config);

        // validate if module is known
        String name = config.getModuleName();
        return getMatchingDecorator(EntryType.ID, name, (ModuleNameDecorator) module.getSelectedItem(),
                null, null, roboVmModules);
    }

    /**
     * decorator for module
     */
    private static class ModuleNameDecorator extends Decorator<Module> {
        ModuleNameDecorator(Module module) {
            super(module, module.getName(), module.getName(), EntryType.ID);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
