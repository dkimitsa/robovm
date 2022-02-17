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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class DirectoryPicker {
    private JPanel panel;
    private JTextField directory;
    private JButton browseButton;
    private JLabel title;
    private Project project;
    private boolean optional;

    public void populate(String title, boolean optional) {
        this.optional = optional;
        this.title.setText(title);
        this.browseButton.addActionListener(e -> {
            FileChooserDialog fileChooser = FileChooserFactory.getInstance()
                    .createFileChooser(new FileChooserDescriptor(true, false, false, false, false, false) {
                        @Override
                        public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                            return file.isDirectory();
                        }
                        @Override
                        public boolean isFileSelectable(VirtualFile file) {
                            return file.isDirectory();
                        }
                    }, null, panel);
            VirtualFile[] dir1 = fileChooser.choose(project);
            if(dir1.length > 0) {
                directory.setText(dir1[0].getCanonicalPath());
            }
        });
    }

    public void setDirectory(String value) {
        directory.setText(value);
    }

    public void validate() throws ConfigurationException {
        String d = directory.getText();
        if (!optional && (d == null || d.isEmpty()))
            throw new ConfigurationException(title.getText() + " is not specified!");

        File dirFile = new File(d);
        if (!dirFile.exists() || !dirFile.isDirectory())
            throw new ConfigurationException(title.getText() + " is valid directory!");
    }

    public String getSelectedDirectory() {
        return directory.getText();
    }
}
