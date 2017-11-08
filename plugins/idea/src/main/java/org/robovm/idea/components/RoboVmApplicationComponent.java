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
package org.robovm.idea.components;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import org.robovm.compiler.util.platforms.ToolchainUtil;
import org.robovm.idea.RoboVmPlugin;
import org.robovm.idea.components.setupwizard.JdkSetupDialog;
import org.robovm.idea.components.setupwizard.NoXcodeSetupDialog;
import org.robovm.idea.components.setupwizard.XcodeSetupDialog;
import org.robovm.idea.sdk.RoboVmSdkType;

/**
 * Call on app startup, responsible for extracting/updating the RoboVM SDK and
 * setting up the SDK so its available in IDEA.
 */
public class RoboVmApplicationComponent implements ApplicationComponent {
    public static final String ROBOVM_HAS_SHOWN_NO_XCODE_WIZARD = "robovm.hasShownNoXcodeWizard";

    @Override
    public void initComponent() {
        displaySetupWizard();
        RoboVmPlugin.extractSdk();
    }

    private void displaySetupWizard() {
        // uncomment for debugging
        // PropertiesComponent.getInstance().unsetValue(ROBOVM_HAS_SHOWN_ANDROID_WIZARD);
        // PropertiesComponent.getInstance().unsetValue(ROBOVM_HAS_SHOWN_LICENSE_WIZARD);

        // make sure a JDK is configured
        Sdk jdk = RoboVmSdkType.findBestJdk();
        if (jdk == null) {
            new JdkSetupDialog().show();
        }

        // make sure Xcode is installed
        // If we are on a Mac, otherwise
        // inform the user that they
        // won't be able to compile for
        // iOS
        // dkimitsa: skip debug case when considering macos as linux
        if (System.getProperty("os.name").contains("Mac") && System.getenv("ROBOVM_FORCE_MACOSXLINUX") == null &&
                System.getProperty("ROBOVM_FORCE_MACOSXLINUX") == null) {
            try {
                ToolchainUtil.findXcodePath();
            } catch (Throwable e) {
                new XcodeSetupDialog().show();
            }
        } else {
            if(!PropertiesComponent.getInstance().getBoolean(ROBOVM_HAS_SHOWN_NO_XCODE_WIZARD, false)) {
                new NoXcodeSetupDialog().show();
                PropertiesComponent.getInstance().setValue(ROBOVM_HAS_SHOWN_NO_XCODE_WIZARD, "true");
            }
        }
    }

    @Override
    public void disposeComponent() {}

    @NotNull
    @Override
    public String getComponentName() {
        return "org.robovm.idea.components.RoboVmApplicationComponent";
    }
}
