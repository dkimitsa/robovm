/*
 * Copyright (C) 2015 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.idea.running.pickers

import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element
import org.robovm.compiler.config.CpuArch

/**
 * Interface that will inject code related to saving/restoring iOS device data to Run configuration.
 * Target class have to implement devicePickerBucket to return value object where all
 * parameters are stored.
 * Also, it's the interface IOSDevicePicker is working with
 */
interface DevicePickerConfig : BasePrimitiveConfig {
    enum class EntryType {
        ID,    // corresponding *name field contains identifier
        AUTO,  // auto (signing identity, provisioning profile)
    }
    var deviceArch: CpuArch?
    var signingIdentityType: EntryType?
    var signingIdentity: String?
    var provisioningProfileType: EntryType?
    var provisioningProfile: String?


    fun setDefaultDevicePickerValues() {
        deviceArch = CpuArch.arm64
        signingIdentityType = EntryType.AUTO
        provisioningProfileType = EntryType.AUTO
    }

    fun validateAndFixDevicePicker() {
        // migrate simulator to new code if legacy found
        deviceArch = deviceArch ?: CpuArch.arm64
        if (signingIdentityType == null)
            signingIdentityType = EntryType.AUTO
        if (provisioningProfileType == null)
            provisioningProfileType = EntryType.AUTO
    }

    @Throws(InvalidDataException::class)
    fun readDevicePickerExternal(element: Element) {
        deviceArch = valueOf(CpuArch::class.java, JDOMExternalizerUtil.readField(element, "deviceArch"))
        signingIdentityType = valueOf(EntryType::class.java, JDOMExternalizerUtil.readField(element, "signingIdentityType"))
        signingIdentity = JDOMExternalizerUtil.readField(element, "signingIdentity")
        provisioningProfileType = valueOf(EntryType::class.java, JDOMExternalizerUtil.readField(element, "provisioningProfileType"))
        provisioningProfile = JDOMExternalizerUtil.readField(element, "provisioningProfile")
        validateAndFixDevicePicker()
    }

    @Throws(WriteExternalException::class)
    fun writeDevicePickerExternal(element: Element) {
        JDOMExternalizerUtil.writeField(element, "deviceArch", toStringOrNull(deviceArch))
        JDOMExternalizerUtil.writeField(element, "signingIdentityType", toStringOrNull(signingIdentityType))
        JDOMExternalizerUtil.writeField(element, "signingIdentity", signingIdentity)
        JDOMExternalizerUtil.writeField(element, "provisioningProfileType", toStringOrNull(provisioningProfileType))
        JDOMExternalizerUtil.writeField(element, "provisioningProfile", provisioningProfile)
    }
}