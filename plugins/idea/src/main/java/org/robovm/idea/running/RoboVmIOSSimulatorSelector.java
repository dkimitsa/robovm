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
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.CpuArch;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.compiler.target.ios.ProvisioningProfile;
import org.robovm.compiler.target.ios.SigningIdentity;
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;
import org.robovm.idea.running.config.RoboVmRunSimulatorPickerConfig;

import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoboVmIOSSimulatorSelector implements BaseDecoratorAware {
    private static final CpuArch[] DEVICE_ARCHS = {CpuArch.arm64, CpuArch.thumbv7};
    private static final CpuArch[] SIMULATOR_ARCHS = {CpuArch.x86_64, CpuArch.x86, CpuArch.arm64};

    public static final String AUTO_SIMULATOR_IPHONE_TITLE = "Auto (prefers '" + DeviceType.PREFERRED_IPHONE_SIM_NAME + "')";
    public static final String AUTO_SIMULATOR_IPAD_TITLE = "Auto (prefers '" + DeviceType.PREFERRED_IPAD_SIM_NAME + "')";


    private JPanel panel;
    private JComboBox<SimTypeDecorator> simType;
    private JComboBox<CpuArch> simArch;
    private JCheckBox pairedWatch;

    // copy of data that is time consuming to fetch (fetched only once when dialog is created)
    private List<SimTypeDecorator> simDeviceTypes;
    private final SimTypeDecorator simulatorAutoIPhone = new SimTypeDecorator(AUTO_SIMULATOR_IPHONE_TITLE, EntryType.AUTO);
    private final SimTypeDecorator simulatorAutoIPad = new SimTypeDecorator(AUTO_SIMULATOR_IPAD_TITLE, EntryType.AUTO2);
    private boolean moduleHasWatchApp;
    // true if editor internally updating data and listeners should ignore the events
    private boolean updatingData;

    public void populate() {
        // populate controls with stable data
        populateSimulators();

        simType.addActionListener(e -> {
            if (!updatingData) {
                updateSimArchs((SimTypeDecorator) simType.getSelectedItem());
                updatePairedWatch(false, null);
            }
        });
//        module.addActionListener(e -> {
//            if (!updatingData)
//                updatePairedWatch(true, null);
//        } );
    }

    public void applyDataFrom(@NotNull RoboVmRunSimulatorPickerConfig config) {
        try {
            updatingData = true;
            simType.setSelectedItem(getSimulatorFromConfig(config));
            simArch.setSelectedItem(populateSimulatorArch((SimTypeDecorator) simType.getSelectedItem(), config.getSimulatorArch()));
            updatePairedWatch(true, config.isSimulatorLaunchWatch());
        } finally {
            updatingData = false;
        }
    }

    protected void saveDataTo(@NotNull RoboVmRunSimulatorPickerConfig config) throws ConfigurationException {
        // validate all data
        if (simType.getSelectedItem() == null)
            throw buildConfigurationException("Simulator is not specified!", () -> simType.setSelectedItem(simulatorAutoIPhone));
        if (simArch.getSelectedItem() == null)
            throw buildConfigurationException("Simulator architecture is not specified!", () -> simArch.setSelectedIndex(0));

        // simulator related
        config.setSimulatorArch((CpuArch) simArch.getSelectedItem());
        config.setSimulatorType(Decorator.from(simType).entryType);
        config.setSimulator(Decorator.from(simType).id);
        config.setSimulatorSdk(-1); // legacy, will not be used
        config.setSimulatorLaunchWatch(pairedWatch.isSelected());
    }

    private CpuArch populateSimulatorArch(SimTypeDecorator simulator, CpuArch arch) {
        CpuArch result = null;
        simArch.removeAllItems();
        if (simulator != null) {
            if (simulator == simulatorAutoIPad || simulator == simulatorAutoIPhone){
                // auto simulator, use default OS arch (x86_64 or arm64 on m1)is allowed, if arch doesn't match -- override
                simArch.addItem(DeviceType.DEFAULT_HOST_ARCH);
                result = DeviceType.DEFAULT_HOST_ARCH;
            } else {
                Set<Arch> simArches = simulator.data.getArchs();
                for (CpuArch a : SIMULATOR_ARCHS) {
                    if (simArches.contains(a)) {
                        simArch.addItem(a);
                        if (a == arch)
                            result = a;
                    }
                }
            }
        }

        return result;
    }

    private void populateSimulators() {
        this.simDeviceTypes = DeviceType.listDeviceTypes().stream()
                .map(SimTypeDecorator::new)
                .collect(Collectors.toList());

        simType.removeAllItems();
        simType.addItem(simulatorAutoIPhone);
        simType.addItem(simulatorAutoIPad);
        this.simDeviceTypes.forEach(t -> simType.addItem(t));
    }


    private SimTypeDecorator getSimulatorFromConfig(RoboVmRunSimulatorPickerConfig config) {
        String name = config.getSimulator();
        return getMatchingDecorator(config.getSimulatorType(), name,
                (SimTypeDecorator) simType.getSelectedItem(),
                null, simulatorAutoIPhone, simulatorAutoIPad,
                simDeviceTypes,  t -> SimTypeDecorator.matchesName(t, config.getSimulator(), config.getSimulatorSdk()));
    }

    private void updateSimArchs(SimTypeDecorator simulator) {
        CpuArch arch = populateSimulatorArch(simulator, (CpuArch) simArch.getSelectedItem());
        if (arch == null && simArch.getItemCount() > 0)
            arch = simArch.getItemAt(0);
        simArch.setSelectedItem(arch);
    }

    private void updatePairedWatch(boolean moduleChanged, Boolean valueToSet) {
        boolean visible;
        boolean enabled;
        boolean selected = valueToSet != null ? valueToSet : pairedWatch.isSelected();
        String text;

        if (moduleChanged) {
// FIXME!
//            // module changed
//            Decorator<Module> moduleSelected = Decorator.from(module);
//            Config config = moduleSelected != null ? RoboVmPlugin.loadRawModuleConfig(moduleSelected.data) : null;
//            moduleHasWatchApp = config != null && config.getWatchKitApp() != null;
        }

        if (!moduleHasWatchApp) {
            selected = false;
            enabled = false;
            visible = false;
            text = "";
        } else {
            visible = true;
            Decorator<DeviceType> simSelected = Decorator.from(simType);
            if (simSelected != null && simSelected.entryType == EntryType.ID && simSelected.data.getPair() != null) {
                // has pair
                text = "Launch paired: " + simSelected.data.getPair().getDeviceName();
                enabled = true;
            } else {
                // no pair
                text = "Not paired with watch";
                enabled = false;
                selected = false;
            }
        }

        pairedWatch.setVisible(visible);
        pairedWatch.setSelected(selected);
        pairedWatch.setEnabled(enabled);
        pairedWatch.setText(text);
    }

   /**
     * decorator for simulator type
     */
    private static class SimTypeDecorator extends Decorator<DeviceType> {
        SimTypeDecorator(String title, EntryType entryType) {
            super(null, null, title, entryType);
        }

        SimTypeDecorator(DeviceType data) {
            super(data, data.getUdid(), data.getDeviceName(), EntryType.ID);
        }

        public static boolean matchesName(SimTypeDecorator d, String name, int version) {
            return d != null && d.data != null &&
                    d.data.getVersion().versionCode == version &&
                    d.name != null && d.name.equals(name);
        }

        @Override
        public String toString() {
            return data == null ? name : data.getSimpleDeviceName() + " - " + data.getVersion();
        }
    }

    /**
     * decorator for singing identity
     */
    private static class SigningIdentityDecorator extends Decorator<SigningIdentity> {
        SigningIdentityDecorator(String title, EntryType entryType) {
            super(null, null, title, entryType);
        }

        SigningIdentityDecorator(SigningIdentity identity) {
            super(identity, identity.getFingerprint(), identity.getName(), EntryType.ID);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * decorator for provisioning profile
     */
    private static class ProvisioningProfileDecorator extends Decorator<ProvisioningProfile> {
        ProvisioningProfileDecorator(String title, EntryType entryType) {
            super(null, null, title, entryType);
        }

        ProvisioningProfileDecorator(ProvisioningProfile profile) {
            super(profile, profile.getUuid(), profile.getName(), EntryType.ID);
        }

        @Override
        public String toString() {
            return name;
        }
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
