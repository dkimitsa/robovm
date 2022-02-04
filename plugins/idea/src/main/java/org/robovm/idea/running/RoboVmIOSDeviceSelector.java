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
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.Arch;
import org.robovm.compiler.config.CpuArch;
import org.robovm.compiler.target.ios.ProvisioningProfile;
import org.robovm.compiler.target.ios.SigningIdentity;
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;
import org.robovm.idea.running.config.RoboVmRunDevicePickerConfig;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.robovm.idea.running.RoboVmRunConfiguration.AUTO_PROVISIONING_PROFILE;
import static org.robovm.idea.running.RoboVmRunConfiguration.AUTO_SIGNING_IDENTITY;

public class RoboVmIOSDeviceSelector implements BaseDecoratorAware {
    private static final CpuArch[] DEVICE_ARCHS = {CpuArch.arm64, CpuArch.thumbv7};

    private JPanel panel;
    private JComboBox<SigningIdentityDecorator> signingIdentity;
    private JComboBox<ProvisioningProfileDecorator> provisioningProfile;
    private JComboBox<CpuArch> deviceArch;

    // copy of data that is time-consuming to fetch (fetched only once when dialog is created)
    private List<ProvisioningProfileDecorator> provisioningProfiles;
    private final ProvisioningProfileDecorator provisioningProfileAuto = new ProvisioningProfileDecorator(AUTO_PROVISIONING_PROFILE, EntryType.AUTO);
    private List<SigningIdentityDecorator> signingIdentities;
    private final SigningIdentityDecorator signingIdentityAuto = new SigningIdentityDecorator(AUTO_SIGNING_IDENTITY, EntryType.AUTO);

    // true if editor internally updating data and listeners should ignore the events
    private boolean updatingData;

    public void populate() {
        // populate controls with stable data
        populateDeviceArch();
        populateSigningIdentities();
        populateProvisioningProfiles();
    }


    public void applyDataFrom(@NotNull RoboVmRunDevicePickerConfig config) {
        try {
            updatingData = true;
            deviceArch.setSelectedItem(config.getDeviceArch());
            signingIdentity.setSelectedItem(getSigningIdentityFromConfig(config));
            provisioningProfile.setSelectedItem(getProvisioningProfileFromConfig(config));
        } finally {
            updatingData = false;
        }
    }

    protected void saveDataTo(@NotNull RoboVmRunDevicePickerConfig config) throws ConfigurationException {
        // validate all data
        if (deviceArch.getSelectedItem() == null)
            throw buildConfigurationException("Device architecture is not specified!", () -> deviceArch.setSelectedItem(Arch.arm64));
        if (signingIdentity.getSelectedItem() == null)
            throw buildConfigurationException("Signing identity is not specified!", () -> signingIdentity.setSelectedItem(signingIdentityAuto));
        if (provisioningProfile.getSelectedItem() == null)
            throw buildConfigurationException("Provisioning profile is not specified!", () -> provisioningProfile.setSelectedItem(provisioningProfileAuto));

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
        signingIdentity.addItem(signingIdentityAuto);
        this.signingIdentities.forEach(t -> signingIdentity.addItem(t));
    }

    private void populateProvisioningProfiles() {
        this.provisioningProfiles = ProvisioningProfile.list().stream()
                .map(ProvisioningProfileDecorator::new)
                .collect(Collectors.toList());

        provisioningProfile.removeAllItems();
        provisioningProfile.addItem(provisioningProfileAuto);
        this.provisioningProfiles.forEach(t -> provisioningProfile.addItem(t));
    }

    private SigningIdentityDecorator getSigningIdentityFromConfig(RoboVmRunDevicePickerConfig config) {
        String name = config.getSigningIdentity();
        return getMatchingDecorator(config.getSigningIdentityType(), name,
                (SigningIdentityDecorator) signingIdentity.getSelectedItem(),
                AUTO_SIGNING_IDENTITY, signingIdentityAuto, null,
                signingIdentities, t -> Decorator.matchesName(t, name));
    }

    private ProvisioningProfileDecorator getProvisioningProfileFromConfig(RoboVmRunDevicePickerConfig config) {
        String name = config.getProvisioningProfile();
        return getMatchingDecorator(config.getProvisioningProfileType(), name,
                (ProvisioningProfileDecorator) provisioningProfile.getSelectedItem(),
                AUTO_PROVISIONING_PROFILE, provisioningProfileAuto, null,
                provisioningProfiles, t -> Decorator.matchesName(t, name));
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

 }
