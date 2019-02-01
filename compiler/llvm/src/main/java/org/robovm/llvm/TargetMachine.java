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

import java.io.File;
import java.io.OutputStream;

import org.robovm.llvm.binding.CodeGenFileType;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.MemoryBufferRef;
import org.robovm.llvm.binding.StringOut;
import org.robovm.llvm.binding.TargetMachineRef;

/**
 * 
 */
public class TargetMachine implements AutoCloseable {
    protected TargetMachineRef ref;
    private DataLayout dataLayout;

    TargetMachine(TargetMachineRef ref) {
        this.ref = ref;
    }

    protected final void checkDisposed() {
        if (ref == null) {
            throw new LlvmException("Already disposed");
        }
    }
    
    public synchronized void dispose() {
        checkDisposed();
        LLVM.DisposeTargetMachine(ref);
        ref = null;
    }

    @Override
    public void close() {
        dispose();
    }

    public Target getTarget() {
        checkDisposed();
        return new Target(LLVM.GetTargetMachineTarget(ref));
    }
    
    public synchronized DataLayout getDataLayout() {
        checkDisposed();
        if (dataLayout == null) {
            dataLayout = new DataLayout(LLVM.CreateTargetDataLayout(ref));
        }
        return dataLayout;
    }
    
    public TargetOptions getOptions() {
        return new TargetOptions(LLVM.GetTargetMachineTargetOptions(ref));
    }
    
    public String getTriple() {
        checkDisposed();
        return LLVM.GetTargetMachineTriple(ref);
    }

    public String getCPU() {
        checkDisposed();
        return LLVM.GetTargetMachineCPU(ref);
    }
    
    public String getFeatureString() {
        checkDisposed();
        return LLVM.GetTargetMachineFeatureString(ref);
    }
    
    public boolean getAsmVerbosityDefault() {
        return LLVM.TargetMachineGetAsmVerbosityDefault(ref);
    }

    public void setAsmVerbosityDefault(boolean value) {
        LLVM.TargetMachineSetAsmVerbosityDefault(ref, value);
    }

    public boolean getDataSections() {
        return LLVM.TargetMachineGetDataSections(ref);
    }

    public boolean getFunctionSections() {
        return LLVM.TargetMachineGetFunctionSections(ref);
    }

    public void setDataSections(boolean value) {
        LLVM.TargetMachineSetDataSections(ref, value);
    }

    public void setFunctionSections(boolean value) {
        LLVM.TargetMachineSetFunctionSections(ref, value);
    }

    public byte[] emit(Module module, CodeGenFileType fileType) {
        checkDisposed();
        module.checkDisposed();
        StringOut ErrorMessage = new StringOut();
        byte[] res = LLVM.TargetMachineEmit(ref, module.getRef(), fileType, ErrorMessage);
        if (res == null) {
            throw new LlvmException(ErrorMessage.getValue().trim());
        }

        return res;
    }

    public void emit(Module module, File outFile, CodeGenFileType fileType) {
        checkDisposed();
        module.checkDisposed();
        StringOut ErrorMessage = new StringOut();
        if (LLVM.TargetMachineEmitToFile(ref, module.getRef(), outFile.getAbsolutePath(), fileType, ErrorMessage)) {
            // Returns true on failure!
            throw new LlvmException(ErrorMessage.getValue().trim());
        }
    }

    public byte[] assemble(byte[] asm, String filename) {
        MemoryBufferRef memoryBufferRef = LLVM.CreateMemoryBufferWithMemoryRangeCopy(asm, filename);
        if (memoryBufferRef == null) {
            throw new LlvmException("Failed to create memory buffer");
        }
        StringOut errorMessage = new StringOut();
        // LLVMTargetMachineAssembleToOutputStream() takes ownership of the MemoryBuffer so there's no need for us
        // to dispose of it
        byte[] res = LLVM.TargetMachineAssemble(ref, memoryBufferRef, false, false, errorMessage);
        if (res == null) {
            String error = errorMessage.getValue() != null 
                    ? errorMessage.getValue().trim() 
                    : "Unknown error";
            throw new LlvmException(error);
        }

        return res;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TargetMachine other = (TargetMachine) obj;
        if (ref == null) {
            if (other.ref != null) {
                return false;
            }
        } else if (!ref.equals(other.ref)) {
            return false;
        }
        return true;
    }
}
