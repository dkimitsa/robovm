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
package org.robovm.idea.running.pickers

import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.JDOMExternalizerUtil
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element
import org.robovm.compiler.config.CpuArch
import org.robovm.compiler.target.ios.DeviceType

/**
 * Interface that will inject code related to saving/restoring Simulator data to Run configuration.
 * Target class have to implement devicePickerBucket to return value object where all
 * parameters are stored.
 * Also, it's the interface IOSDevicePicker is working with
 */
interface SimulatorPickerConfig : BasePrimitiveConfig {
    enum class EntryType {
        ID,    // corresponding *name field contains identifier
        AUTO_IPHONE,
        AUTO_IPAD,
    }

    var simulatorArch: CpuArch?
    var simulatorType: EntryType?
    var simulator: String?
    var simulatorLaunchWatch: Boolean


    fun setDefaultSimulatorPickerValues() {
        simulatorType = EntryType.AUTO_IPHONE
        simulatorArch = DeviceType.DEFAULT_HOST_ARCH
        simulatorLaunchWatch = false
    }

    fun validateAndFixSimulatorPicker() {
        simulatorType = simulatorType ?: EntryType.AUTO_IPHONE
        simulatorArch = simulatorArch ?: DeviceType.DEFAULT_HOST_ARCH
        simulatorLaunchWatch = simulatorLaunchWatch ?: false
        if (simulatorType === EntryType.AUTO_IPHONE || simulatorType === EntryType.AUTO_IPAD) {
            simulatorArch = DeviceType.DEFAULT_HOST_ARCH
        }
    }

    @Throws(InvalidDataException::class)
    fun readSimulatorPickerExternal(element: Element) {
        simulatorType = valueOf(EntryType::class.java, JDOMExternalizerUtil.readField(element, "simulatorType"))
        simulator = JDOMExternalizerUtil.readField(element, "simulatorName")
        simulatorArch = valueOf<CpuArch>(CpuArch::class.java, JDOMExternalizerUtil.readField(element, "simArch"))
        simulatorLaunchWatch = parseInt(JDOMExternalizerUtil.readField(element, "simulatorLaunchPair"), -1) > 0
        validateAndFixSimulatorPicker()
    }

    @Throws(WriteExternalException::class)
    fun writeSimulatorPickerExternal(element: Element) {
        JDOMExternalizerUtil.writeField(element, "simArch", simulatorArch?.toString())
        JDOMExternalizerUtil.writeField(element, "simulatorType", simulatorType?.toString())
        JDOMExternalizerUtil.writeField(element, "simulatorName", simulator)
        JDOMExternalizerUtil.writeField(element, "simulatorLaunchWatch", if (simulatorLaunchWatch == true) "1" else "0")
    }
}