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

import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.CpuArch;
import org.robovm.compiler.target.ios.ProvisioningProfile;
import org.robovm.compiler.target.ios.SigningIdentity;
import org.robovm.idea.running.pickers.DevicePickerConfig.EntryType;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * iOS device selection widget, provides two combo boxes:
 * - Signing identity
 * - Device architecture
 * Picks signing identities and fill the combo-box
 */
public class IOSDevicePicker implements BaseDecoratorAware {
    private static final String TITLE_AUTO = "Auto";
    private static final CpuArch[] DEVICE_ARCHS = {CpuArch.arm64, CpuArch.thumbv7};

    private JComboBox<SigningIdentityDecorator> signingIdentity;
    private JComboBox<ProvisioningProfileDecorator> provisioningProfile;
    private JComboBox<CpuArch> deviceArch;
    @SuppressWarnings("unused") // root panel required otherwise produces No binding on root component of nested form
    private JPanel panel;

    // copy of data that is time-consuming to fetch (fetched only once when dialog is created)
    private List<ProvisioningProfileDecorator> provisioningProfiles;
    private List<SigningIdentityDecorator> signingIdentities;

    // true if editor internally updating data and listeners should ignore the events
    private boolean updatingData;

    public void populate() {
        // populate controls with stable data
        populateDeviceArch();
        populateSigningIdentities();
        populateProvisioningProfiles();
    }


    public void applyDataFrom(@NotNull DevicePickerConfig config) {
        try {
            updatingData = true;
            deviceArch.setSelectedItem(config.getDeviceArch());
            signingIdentity.setSelectedItem(getSigningIdentityFromConfig(config));
            provisioningProfile.setSelectedItem(getProvisioningProfileFromConfig(config));
        } finally {
            updatingData = false;
        }
    }

    public void validate() throws ConfigurationException {
        // validate all data
        if (deviceArch.getSelectedItem() == null)
            throw buildConfigurationException("Device architecture is not specified!",
                    () -> deviceArch.setSelectedItem(Arch.arm64));
        if (signingIdentity.getSelectedItem() == null)
            throw buildConfigurationException("Signing identity is not specified!",
                    () -> signingIdentity.setSelectedItem(SigningIdentityDecorator.Auto));
        if (provisioningProfile.getSelectedItem() == null)
            throw buildConfigurationException("Provisioning profile is not specified!",
                    () -> provisioningProfile.setSelectedItem(ProvisioningProfileDecorator.Auto));
    }

    public void saveDataTo(@NotNull DevicePickerConfig config) {
        // device related
        config.setDeviceArch((CpuArch) deviceArch.getSelectedItem());
        config.setSigningIdentityType(Decorator.from(signingIdentity).entryType);
        config.setSigningIdentity(Decorator.from(signingIdentity).id);
        config.setProvisioningProfileType(Decorator.from(provisioningProfile).entryType);
        config.setProvisioningProfile(Decorator.from(provisioningProfile).id);
    }

    private void populateDeviceArch() {
        deviceArch.removeAllItems();
        for (CpuArch arch : DEVICE_ARCHS)
            deviceArch.addItem(arch);
    }

    private void populateSigningIdentities() {
        this.signingIdentities = SigningIdentity.list().stream()
                .map(SigningIdentityDecorator::new)
                .collect(Collectors.toList());

        signingIdentity.removeAllItems();
        signingIdentity.addItem(SigningIdentityDecorator.Auto);
        this.signingIdentities.forEach(t -> signingIdentity.addItem(t));
    }

    private void populateProvisioningProfiles() {
        this.provisioningProfiles = ProvisioningProfile.list().stream()
                .map(ProvisioningProfileDecorator::new)
                .collect(Collectors.toList());

        provisioningProfile.removeAllItems();
        provisioningProfile.addItem(ProvisioningProfileDecorator.Auto);
        this.provisioningProfiles.forEach(t -> provisioningProfile.addItem(t));
    }

    private SigningIdentityDecorator getSigningIdentityFromConfig(DevicePickerConfig config) {
        if (config.getSigningIdentityType() == EntryType.AUTO) {
            return SigningIdentityDecorator.Auto;
        } else {
            String id = config.getSigningIdentity();
            return getMatchingDecorator(id,
                    (SigningIdentityDecorator) signingIdentity.getSelectedItem(),
                    signingIdentities);
        }
    }

    private ProvisioningProfileDecorator getProvisioningProfileFromConfig(DevicePickerConfig config) {
        if (config.getProvisioningProfileType() == EntryType.AUTO) {
            return ProvisioningProfileDecorator.Auto;
        } else {
            String id = config.getProvisioningProfile();
            return getMatchingDecorator(id,
                    (ProvisioningProfileDecorator) provisioningProfile.getSelectedItem(),
                    provisioningProfiles);
        }
    }

    /**
     * decorator for singing identity
     */
    private static class SigningIdentityDecorator extends Decorator<SigningIdentity, EntryType> {
        static final SigningIdentityDecorator Auto = new SigningIdentityDecorator(TITLE_AUTO, EntryType.AUTO);

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
    private static class ProvisioningProfileDecorator extends Decorator<ProvisioningProfile, EntryType> {
        static final ProvisioningProfileDecorator Auto = new ProvisioningProfileDecorator(TITLE_AUTO, EntryType.AUTO);
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

}
