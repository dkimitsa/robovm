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
package org.robovm.idea.running.pickers;

public interface BasePrimitiveConfig {

    //
    // Helpers
    //
    default <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        try {
            return name != null ? Enum.valueOf(enumType, name) : null;
        } catch (IllegalArgumentException ignored){
            return null;
        }
    }

    default int parseInt(String value, int defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    default String toStringOrNull(Object v) {
        return v != null ? v.toString() : null;
    }
}
