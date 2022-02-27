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

import javax.swing.*;
import java.util.List;

/**
 * Contains base code related to picking values from the list, base decorator classes and
 */
public interface BaseDecoratorAware {

    /**
     * Universal entry point for getting decorator by name or id
     * @param selectedItem  currently selected item
     * @param values           all known values (on form of decorator)
     * @return matching decorator
     */
    default <T extends Decorator<?, ?>> T getMatchingDecorator(String id, T selectedItem, List<T> values) {
        T result = null;
        // looking for exact match, quick check against selected item
        if (Decorator.matchesId(selectedItem, id)) {
            result = selectedItem;
        } else {
            result = values.stream().filter(t -> Decorator.matchesId(t, id)).findAny().orElse(null);
        }
        return result;
    }

    /**
     * base decorator for items with name and identifier
     *
     * @param <T> type of data
     */
    abstract class Decorator<T, E> {
        final T data;
        final String id;
        final String name;
        final E entryType;

        Decorator(T data, String id, String name, E entryType) {
            this.data = data;
            this.id = id;
            this.name = name;
            this.entryType = entryType;
        }

        static boolean matchesId(Decorator<?, ?> d, String id) {
            return d != null && d.id != null && d.id.equals(id);
        }

        static <T, E> Decorator<T, E> from(JComboBox<? extends Decorator<T, E>> cb) {
            //noinspection unchecked
            return (Decorator<T, E>) cb.getSelectedItem();
        }
    }

    /**
     * helper to build exception with quick fix action
     */
    default ConfigurationException buildConfigurationException(String message, Runnable quickFix) {
        ConfigurationException exc = new ConfigurationException(message);
        exc.setQuickFix(quickFix);
        return exc;
    }
}
