/*
 * Copyright 2016 Justin Shapcott.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.compiler.llvm.debug.dwarf;

import org.robovm.compiler.ModuleBuilder;
import org.robovm.compiler.llvm.NamedMetadata;

/**
 * Compile unit metadata
 */
public class DICompileUnit extends NamedMetadata<DISpecializedMetadata> {
    public enum DebugEmissionKind implements DwarfConst.DwarfConstEnum {
        NoDebug,
        FullDebug,
        LineTablesOnly
    }

    public DICompileUnit(ModuleBuilder mb, DIFile file, boolean optimized, String producer, DebugEmissionKind emissionKind) {
        super(mb, new DISpecializedMetadata("DICompileUnit", true)
                .put("language", DwarfConst.SourceLanguage.DW_LANG_Java)
                .put("file", file)
                .put("producer", producer)
                .put("isOptimized", optimized)
                .put("emissionKind", emissionKind));
    }
}
