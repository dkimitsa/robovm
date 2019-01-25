/*
 * Copyright (C) 2013 RoboVM AB
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
package org.robovm.llvm;

import org.junit.Test;
import org.robovm.llvm.binding.CodeGenFileType;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link TargetMachine}.
 */
public class TargetMachineTest {

    @Test
    public void testEmitToFile() throws Exception {
        try (Context context = new Context()) {
            try (TargetMachine tm = Target.getTarget("thumb").createTargetMachine("thumbv7-unknown-ios")) {
                Module module = Module.parseIR(context, "define external i32 @foo() {\n ret i32 5\n }\n", "foo.c");
                byte[] data = tm.emit(module, CodeGenFileType.AssemblyFile);
                String asm = new String(data, "utf-8");
                assertTrue(asm.contains("_foo"));
            }
        }
    }

    @Test
    public void testAssemble() throws Exception {
        try (Context context = new Context()) {
            try (TargetMachine tm = Target.getTarget("thumb").createTargetMachine("thumbv7-unknown-ios")) {
                Module module = Module.parseIR(context, "define private i32 @foo() {\n ret i32 5\n }\n", "foo.c");
                byte[] data;
                data = tm.emit(module, CodeGenFileType.AssemblyFile);
                String asm = new String(data, "utf-8");
                data = tm.assemble(asm.getBytes(), "foo.s");
                assertTrue(data.length > 0);
            }
        }
    }
    
}
