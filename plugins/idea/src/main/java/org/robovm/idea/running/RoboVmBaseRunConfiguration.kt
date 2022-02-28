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

import com.intellij.execution.configurations.*
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.robovm.compiler.AppCompiler
import org.robovm.compiler.config.Config
import org.robovm.idea.running.pickers.BasePrimitiveConfig

public abstract class RoboVmBaseRunConfiguration(name: String, project: Project, factory: ConfigurationFactory)
    : ModuleBasedConfiguration<RunConfigurationModule, Element>(name, RunConfigurationModule(project), factory),
        RunConfigurationWithSuppressedDefaultDebugAction,
        RunConfigurationWithSuppressedDefaultRunAction,
        RunProfileWithCompileBeforeLaunchOption,
        BasePrimitiveConfig {

    // promote visibility to public
    fun getModuleName(): String = options.module ?: ""
    override fun getType(): ConfigurationType = super.getType()

    // TODO: FIXME: CONSIDER REMOVING or moving to RunState
    // these are used to pass information between
    // the compiler, the run configuration and the
    // runner. They are not persisted.
    var isDebug = false
    var config: Config? = null
    var debugPort = 0
    var compiler: AppCompiler? = null
    var programArguments: List<String>? = null
    // TODO: FIXME: end of
}