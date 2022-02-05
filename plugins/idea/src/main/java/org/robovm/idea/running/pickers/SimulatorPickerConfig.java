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

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.config.CpuArch;
import org.robovm.compiler.target.ios.DeviceType;
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;

/**
 * Interface that will inject code related to saving/restoring Simulator data to Run configuration.
 * Target class have to implement devicePickerBucket to return value object where all
 * parameters are stored.
 * Also, it's the interface IOSDevicePicker is working with
 */
public interface SimulatorPickerConfig extends BasePrimitiveConfig {
    class Bucket {
        private CpuArch simulatorArch;
        private EntryType simulatorType;
        private String simulator;
        private int simulatorSdk;
        private boolean simulatorLaunchWatch;
    }

    /**
     * target class is responsible to return data bucket to allow default implementation to work
     */
    Bucket simulatorPickerBucket();

    default CpuArch getSimulatorArch() {
        return simulatorPickerBucket().simulatorArch;
    }

    default void setSimulatorArch(CpuArch simulatorArch) {
        simulatorPickerBucket().simulatorArch = simulatorArch;
    }

    default EntryType getSimulatorType() {
        return simulatorPickerBucket().simulatorType;
    }

    default void setSimulatorType(EntryType simulatorType) {
        simulatorPickerBucket().simulatorType = simulatorType;
    }

    default String getSimulator() {
        return simulatorPickerBucket().simulator;
    }

    default void setSimulator(String simulator) {
        simulatorPickerBucket().simulator = simulator;
    }

    default int getSimulatorSdk() {
        return simulatorPickerBucket().simulatorSdk;
    }

    default void setSimulatorSdk(int simulatorSdk) {
        simulatorPickerBucket().simulatorSdk = simulatorSdk;
    }

    default boolean isSimulatorLaunchWatch() {
        return simulatorPickerBucket().simulatorLaunchWatch;
    }

    default void setSimulatorLaunchWatch(boolean simulatorLaunchWatch) {
        simulatorPickerBucket().simulatorLaunchWatch = simulatorLaunchWatch;
    }

    default void setDefaultSimulatorPickerValues() {
        simulatorPickerBucket().simulatorType = EntryType.AUTO;
        simulatorPickerBucket().simulatorArch = DeviceType.DEFAULT_HOST_ARCH;
        simulatorPickerBucket().simulatorLaunchWatch = false;
    }

    default void validateAndFixSimulatorPicker() {
        if (simulatorPickerBucket().simulatorType == EntryType.AUTO ||
                simulatorPickerBucket().simulatorType == EntryType.AUTO2) {
            simulatorPickerBucket().simulatorArch = DeviceType.DEFAULT_HOST_ARCH;
        }
    }

    default void readSimulatorPickerExternal(@NotNull Element element) throws InvalidDataException {
        simulatorPickerBucket().simulatorType = valueOf(EntryType.class, JDOMExternalizerUtil.readField(element, "simulatorType"));
        simulatorPickerBucket().simulator = JDOMExternalizerUtil.readField(element, "simulatorName");
        simulatorPickerBucket().simulatorArch = valueOf(CpuArch.class, JDOMExternalizerUtil.readField(element, "simArch"));
        simulatorPickerBucket().simulatorSdk = parseInt(JDOMExternalizerUtil.readField(element, "simulatorSdk"), -1);
        simulatorPickerBucket().simulatorLaunchWatch = parseInt(JDOMExternalizerUtil.readField(element, "simulatorLaunchPair"), -1)  > 0;

        validateAndFixSimulatorPicker();
    }

    default void writeSimulatorPickerExternal(@NotNull Element element) throws WriteExternalException {
        JDOMExternalizerUtil.writeField(element, "simArch", toStringOrNull(simulatorPickerBucket().simulatorArch));
        JDOMExternalizerUtil.writeField(element, "simulatorType", toStringOrNull(simulatorPickerBucket().simulatorType));
        JDOMExternalizerUtil.writeField(element, "simulatorName", simulatorPickerBucket().simulator);
        JDOMExternalizerUtil.writeField(element, "simulatorSdk", Integer.toString(simulatorPickerBucket().simulatorSdk));
        JDOMExternalizerUtil.writeField(element, "simulatorLaunchWatch", simulatorPickerBucket().simulatorLaunchWatch ? "1" : "0");
    }
}
