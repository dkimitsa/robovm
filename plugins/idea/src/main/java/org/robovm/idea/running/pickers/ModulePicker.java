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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.robovm.idea.RoboVmPlugin;
import org.robovm.idea.running.RoboVmRunConfiguration.EntryType;

import javax.swing.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * widget that provides a combobox with RoboVM module selection.
 * predicate is used to filter modules by criteria (e.g. iOS or console project)
 */
public class ModulePicker implements BaseDecoratorAware {

    @SuppressWarnings("unused") // root panel required otherwise produces No binding on root component of nested form
    private JPanel panel;

    private JComboBox<ModuleNameDecorator> module;

    // copy of data that is time-consuming to fetch (fetched only once when dialog is created)
    private List<ModuleNameDecorator> roboVmModules;

    // true if editor internally updating data and listeners should ignore the events
    private boolean updatingData;

    // listener for module changed callbacks
    public interface Listener {
        void moduleChanged(Module m);
    }
    private Listener listener;
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void populate() {
        roboVmModules = null; // will be populated once modules list is known
        module.addActionListener(e -> {
            if (!updatingData) {
                if (listener != null) {
                    ModuleNameDecorator decorator = (ModuleNameDecorator) module.getSelectedItem();
                    listener.moduleChanged(decorator != null ? decorator.data : null);
                }
            }
        });
    }

    public void applyDataFrom(Project project, @NotNull Predicate<String> moduleTypePredicate,
                              ModulePickerConfig config) {
        try {
            updatingData = true;
            populateModulesIfNeeded(project, moduleTypePredicate);
            module.setSelectedItem(getModuleDecoratorByName(config.getModuleName()));
        } finally {
            updatingData = false;
        }
    }

    public void validate() throws ConfigurationException {
        if (module.getSelectedItem() == null)
            throw new ConfigurationException("RoboVM module is not specified!");
    }

    public void saveDataTo(@NotNull ModulePickerConfig config) {
        // save all data
        config.setModuleName(Decorator.from(module).name);
    }

    private void populateModulesIfNeeded(@NotNull Project project, @NotNull Predicate<String> targetTypePredicate) {
        if (roboVmModules != null)
            return;

        this.roboVmModules = RoboVmPlugin.getRoboVmModules(project, targetTypePredicate).stream()
                .map(ModuleNameDecorator::new)
                .collect(Collectors.toList());

        // populate with RoboVM Sdk modules
        this.module.removeAllItems();
        this.roboVmModules.forEach(m -> module.addItem(m));
    }

    private ModuleNameDecorator getModuleDecoratorByName(String name) {
        // validate if module is known
        return getMatchingDecorator(EntryType.ID, name, (ModuleNameDecorator) module.getSelectedItem(),
                null, null, roboVmModules);
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
