/*
 * Copyright (C) 2015 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General default License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General default License for more details.
 *
 * You should have received a copy of the GNU General default License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.idea.running.config;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.CpuArch;
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;

public interface DevicePickerConfig extends BasePrimitiveConfig {
    String AUTO_SIGNING_IDENTITY_LEGACY = "Auto (matches 'iPhone Developer|iOS Development')";
    String AUTO_SIGNING_IDENTITY = "Auto (matches 'iPhone Developer|iOS Development|Apple Development')";

    class Bucket {
        CpuArch deviceArch;
        EntryType signingIdentityType;
        String signingIdentity;
        EntryType provisioningProfileType;
        String provisioningProfile;
    }

    Bucket devicePickerBucket();

    default CpuArch getDeviceArch() {
        return devicePickerBucket().deviceArch;
    }

    default void setDeviceArch(CpuArch deviceArch) {
        devicePickerBucket().deviceArch = deviceArch;
    }

    default EntryType getSigningIdentityType() {
        return devicePickerBucket().signingIdentityType;
    }

    default void setSigningIdentityType(EntryType signingIdentityType) {
        devicePickerBucket().signingIdentityType = signingIdentityType;
    }

    default String getSigningIdentity() {
        return devicePickerBucket().signingIdentity;
    }

    default void setSigningIdentity(String signingIdentity) {
        devicePickerBucket().signingIdentity = signingIdentity;
    }

    default EntryType getProvisioningProfileType() {
        return devicePickerBucket().provisioningProfileType;
    }

    default void setProvisioningProfileType(EntryType provisioningProfileType) {
        devicePickerBucket().provisioningProfileType = provisioningProfileType;
    }

    default String getProvisioningProfile() {
        return devicePickerBucket().provisioningProfile;
    }

    default void setProvisioningProfile(String provisioningProfile) {
        devicePickerBucket().provisioningProfile = provisioningProfile;
    }

    default void setDefaultDevicePickerValues() {
        setDeviceArch(CpuArch.arm64);
        setSigningIdentityType(EntryType.AUTO);
        setProvisioningProfileType(EntryType.AUTO);
    }

    default void validateAndFixDevicePicker() {
        // migrate simulator to new code if legacy found
        if (getSigningIdentityType() == null && AUTO_SIGNING_IDENTITY_LEGACY.equals(devicePickerBucket().signingIdentity)) {
            setSigningIdentityType(EntryType.AUTO);
            setSigningIdentity(AUTO_SIGNING_IDENTITY);
        }
    }

    default void readDevicePickerExternal(@NotNull Element element) throws InvalidDataException {
        devicePickerBucket().deviceArch = valueOf(CpuArch.class, JDOMExternalizerUtil.readField(element, "deviceArch"));
        devicePickerBucket().signingIdentityType = valueOf(EntryType.class, JDOMExternalizerUtil.readField(element, "signingIdentityType"));
        devicePickerBucket().signingIdentity = JDOMExternalizerUtil.readField(element, "signingIdentity");
        devicePickerBucket().provisioningProfileType = valueOf(EntryType.class, JDOMExternalizerUtil.readField(element, "provisioningProfileType"));
        devicePickerBucket().provisioningProfile = JDOMExternalizerUtil.readField(element, "provisioningProfile");

        validateAndFixDevicePicker();
    }

    default void writeDevicePickerExternal(@NotNull Element element) throws WriteExternalException {
        JDOMExternalizerUtil.writeField(element, "deviceArch", toStringOrNull(devicePickerBucket().deviceArch));
        JDOMExternalizerUtil.writeField(element, "signingIdentityType", toStringOrNull(devicePickerBucket().signingIdentityType));
        JDOMExternalizerUtil.writeField(element, "signingIdentity", devicePickerBucket().signingIdentity);
        JDOMExternalizerUtil.writeField(element, "provisioningProfileType", toStringOrNull(devicePickerBucket().provisioningProfileType));
        JDOMExternalizerUtil.writeField(element, "provisioningProfile", devicePickerBucket().provisioningProfile);
    }
}
