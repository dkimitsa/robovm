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
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;

import javax.swing.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Contains base code related to picking values from the list
 */
public class BaseDecoratorAware {

    /**
     * Universal entry point for getting decorator by name or id
     * @param entryType     type of entry if it was configured, null if legacy
     * @param value         name/id (depending on entry)
     * @param selectedItem  currently selected item
     * @param autoKey       in case of legacy, identifier of auto field
     * @param auto1         decorator matching auto
     * @param auto2         same for auto 2 (e.g. autoIpad)
     * @param values           all known values (on form of decorator)
     * @param byNamePredicate  predicate to find item by name
     * @return matching decorator
     */
    protected <T extends Decorator<?>> T getMatchingDecorator(EntryType entryType, String value, T selectedItem,
                                                              String autoKey, T auto1, T auto2,
                                                              List<T> values, Predicate<T> byNamePredicate) {
        if (entryType != null) {
            // by entry type and ID
            return getMatchingDecorator(entryType, value, selectedItem, auto1, auto2, values);
        } else {
            // legacy by name
            return getMatchingDecorator(value, selectedItem, autoKey, auto1, values, byNamePredicate);
        }
    }

    /**
     * version for case when entry type is known
     */
    private <T extends Decorator<?>> T getMatchingDecorator(EntryType entryType, String id, T selectedItem, T auto1, T auto2, List<T> values) {
        T result = null;
        switch (entryType) {
            case ID:
                // looking for exact match, quick check against selected item
                if (Decorator.matchesId(selectedItem, id)) {
                    result = selectedItem;
                } else {
                    result = values.stream().filter(t -> Decorator.matchesId(t, id)).findAny().orElse(null);
                }
                break;
            case AUTO:
                result = auto1;
                break;
            case AUTO2:
                result = auto2;
                break;
        }

        return result;
    }

    /**
     * version for legacy case, matching by name only
     */
    private <T extends Decorator<?>> T getMatchingDecorator(String name, T selectedItem, String autoKey, T auto, List<T> values, Predicate<T> byNamePredicate) {
        // backward compatibility, should not happen once saved with ID
        T result = null;
        if (name != null) {
            // lookup Auto values first
            if (name.equals(autoKey))
                result = auto;
            else if (selectedItem != null && byNamePredicate.test(selectedItem)) {
                result = selectedItem;
            } else {
                result = values.stream().filter(byNamePredicate).findAny().orElse(null);
            }
        }
        return result;
    }

    /**
     * base decorator for items with name and identifier
     *
     * @param <T> type of data
     */
    protected static abstract class Decorator<T> {
        final T data;
        final String id;
        final String name;
        final EntryType entryType;

        Decorator(T data, String id, String name, EntryType entryType) {
            this.data = data;
            this.id = id;
            this.name = name;
            this.entryType = entryType;
        }

        static boolean matchesId(Decorator<?> d, String id) {
            return d != null && d.id != null && d.id.equals(id);
        }

        static boolean matchesName(Decorator<?> d, String name) {
            return d != null && d.name != null && d.name.equals(name);
        }

        static <T> Decorator<T> from(JComboBox<? extends Decorator<T>> cb) {
            //noinspection unchecked
            return (Decorator<T>) cb.getSelectedItem();
        }
    }

    /**
     * helper to build exception with quick fix action
     */
    ConfigurationException buildConfigurationException(String message, Runnable quickFix) {
        ConfigurationException exc = new ConfigurationException(message);
        exc.setQuickFix(quickFix);
        return exc;
    }
}
