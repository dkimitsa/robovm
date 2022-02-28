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
package org.robovm.idea.running

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.robovm.compiler.AppCompiler
import org.robovm.compiler.config.Config
import org.robovm.compiler.target.ConsoleTarget
import org.robovm.idea.RoboVmPlugin
import org.robovm.idea.running.pickers.BasePrimitiveConfig

class RoboVmConsoleRunConfiguration(name: String, project: Project, factory: ConfigurationFactory)
    : RoboVmBaseRunConfiguration(name, project, factory) {
    // TODO: rework these are just temporal proxies to options
    val workingDir: String?
        get() = options.workingDirectory
    val arguments: String?
        get() = options.arguments
    // TODO: end of proxies

    override fun getOptionsClass(): Class<out RunConfigurationOptions> {
        return RoboVmConsoleRunConfigurationOptions::class.java
    }

    public override fun getOptions(): RoboVmConsoleRunConfigurationOptions {
        return super.getOptions() as RoboVmConsoleRunConfigurationOptions
    }

    override fun getValidModules(): Collection<Module> {
        return RoboVmPlugin.getRoboVmModules(configurationModule!!.project, ConsoleTarget.TYPE::equals)
    }

    override fun getConfigurationEditor(): RoboVmConsoleRunConfigurationSettingsEditor {
        return RoboVmConsoleRunConfigurationSettingsEditor()
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RoboVmConsoleRunProfileState {
        return RoboVmConsoleRunProfileState(environment)
    }

    override fun setModuleName(moduleName: String?) {
        options.module = moduleName
    }
}