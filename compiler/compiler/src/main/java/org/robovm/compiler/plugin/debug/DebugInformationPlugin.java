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
package org.robovm.compiler.plugin.debug;

import org.robovm.compiler.Annotations;
import org.robovm.compiler.Functions;
import org.robovm.compiler.ModuleBuilder;
import org.robovm.compiler.Symbols;
import org.robovm.compiler.Version;
import org.robovm.compiler.clazz.Clazz;
import org.robovm.compiler.config.Config;
import org.robovm.compiler.config.OS;
import org.robovm.compiler.llvm.Alloca;
import org.robovm.compiler.llvm.ArrayType;
import org.robovm.compiler.llvm.BasicBlock;
import org.robovm.compiler.llvm.Call;
import org.robovm.compiler.llvm.Constant;
import org.robovm.compiler.llvm.ConstantBitcast;
import org.robovm.compiler.llvm.Function;
import org.robovm.compiler.llvm.FunctionDeclaration;
import org.robovm.compiler.llvm.Global;
import org.robovm.compiler.llvm.Instruction;
import org.robovm.compiler.llvm.IntegerConstant;
import org.robovm.compiler.llvm.Linkage;
import org.robovm.compiler.llvm.MetadataValue;
import org.robovm.compiler.llvm.debug.dwarf.DIMetadataValueList;
import org.robovm.compiler.llvm.NamedMetadata;
import org.robovm.compiler.llvm.PointerType;
import org.robovm.compiler.llvm.Type;
import org.robovm.compiler.llvm.Value;
import org.robovm.compiler.llvm.Variable;
import org.robovm.compiler.llvm.VariableRef;
import org.robovm.compiler.llvm.ZeroInitializer;
import org.robovm.compiler.llvm.debug.dwarf.DIBasicType;
import org.robovm.compiler.llvm.debug.dwarf.DICompileUnit;
import org.robovm.compiler.llvm.debug.dwarf.DIFile;
import org.robovm.compiler.llvm.debug.dwarf.DILocation;
import org.robovm.compiler.llvm.debug.dwarf.DILocalVariable;
import org.robovm.compiler.llvm.debug.dwarf.DILocalVariableList;
import org.robovm.compiler.llvm.debug.dwarf.DISubprogram;
import org.robovm.compiler.llvm.debug.dwarf.DISubroutineType;
import org.robovm.compiler.llvm.debug.dwarf.DwarfConst;
import org.robovm.compiler.plugin.AbstractCompilerPlugin;
import org.robovm.compiler.plugin.PluginArgument;
import org.robovm.compiler.plugin.PluginArguments;
import org.robovm.compiler.plugin.debug.kotlin.KotlinTools;
import org.robovm.llvm.LineInfo;
import org.robovm.llvm.ObjectFile;
import org.robovm.llvm.Symbol;
import org.robovm.llvm.debuginfo.DwarfDebugMethodInfo;
import org.robovm.llvm.debuginfo.DwarfDebugObjectFileInfo;
import org.robovm.llvm.debuginfo.DwarfDebugVariableInfo;
import soot.Local;
import soot.LocalVariable;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.tagkit.GenericAttribute;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceFileTag;
import soot.util.Chain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * provides only line number debug information for now
 */
public class DebugInformationPlugin extends AbstractCompilerPlugin {


    public DebugInformationPlugin() {
    }

    public PluginArguments getArguments() {
        return new PluginArguments("debug", Collections.<PluginArgument>emptyList());
    }

    @Override
    public void helloClass(Config config, Clazz clazz) {
        super.helloClass(config, clazz);

        ClassDataBundle classBundle = clazz.getAttachment(ClassDataBundle.class);
        if (classBundle != null)
            clazz.removeAttachement(classBundle);

        // keep all data for class in one structure, allows to reset thing by placing null there
        classBundle = new ClassDataBundle();
        clazz.attach(classBundle);

        if (config.isDebug()) {
            // make a list of java methods as it is in class
            // as during compilation class going to be heavily adjusted and lot of synthetics method will appear
            classBundle.methodsBeforeCompile = new HashSet<>();
            for (SootMethod m : clazz.getSootClass().getMethods()) {
                if (m.isAbstract() || m.isNative())
                    continue;
                classBundle.methodsBeforeCompile.add(m.getSignature());
            }
        }
    }

    @Override
    public void beforeClass(Config config, Clazz clazz, ModuleBuilder mb) throws IOException {
        super.beforeClass(config, clazz, mb);

        String producer = "RoboVM " + Version.getVersion();
        ClassDataBundle classBundle = clazz.getAttachment(ClassDataBundle.class);
        // diFile and diCompileUnit
        classBundle.diFile = new DIFile(mb, getDwarfSourceFile(clazz), getDwarfSourceFilePath(clazz));
        classBundle.diCompileUnit =  new DICompileUnit(mb, classBundle.diFile, !config.isDebug(), producer,
                        config.isDebug() ? DICompileUnit.DebugEmissionKind.FullDebug : DICompileUnit.DebugEmissionKind.LineTablesOnly);

        //noinspection unused
        NamedMetadata dwarfCompileUnit = NamedMetadata.withNamedTuple(mb, "llvm.dbg.cu", classBundle.diCompileUnit);

        // module flags
        NamedMetadata dwarfVersion = NamedMetadata.withTuple(mb, DwarfConst.ModuleFlagBehavior.Warning.raw,
                "Dwarf Version", DwarfConst.LLVMConstants.DWARF_VERSION);
        NamedMetadata debugInfoVersion = NamedMetadata.withTuple(mb,DwarfConst.ModuleFlagBehavior.Warning.raw,
                "Debug Info Version", DwarfConst.LLVMConstants.DEBUG_INFO_VERSION);
        classBundle.flags = NamedMetadata.withNamedTuple(mb, "llvm.module.flags", dwarfVersion, debugInfoVersion);
        // llvm.ident
        //noinspection unused
        NamedMetadata ident = NamedMetadata.withNamedTuple(mb, "lllvm.ident", NamedMetadata.withTuple(mb, producer));

        if (config.isDebug()) {
            // create a list where method inforation will be saved
            classBundle.methods = new ArrayList<>();

            // register llvm.dbg.declare
            mb.addFunctionDeclaration(new FunctionDeclaration(Functions.LLVM_DBG_DECLARE));

            if (config.getTarget().getArch().isArm()) {
                // add global variable to emit sp-fp offset
                // refer to 04-emit-sp-fp-offset-on-arm for details
                mb.addGlobal(new Global("robovm.emitSpFpOffsets", Type.I8));
            }
        }

        // kotlin uses source line remapping as it injects collections code and so on
        LineNumberMapper lineNumberMapper = LineNumberMapper.DIRECT;
        SourceFileTag sourceFileTag = (SourceFileTag) clazz.getSootClass().getTag("SourceFileTag");
        if (sourceFileTag != null && sourceFileTag.getSourceFile() != null && sourceFileTag.getSourceFile().endsWith(".kt")) {
            // kotlin, get line number mapping table from SMAP section
            GenericAttribute smap = (GenericAttribute) clazz.getSootClass().getTag("SourceDebugExtension");
            if (smap != null) {
                lineNumberMapper = KotlinTools.parseSMAP(smap.getValue(), clazz.getInternalName());
            }
        }
        // save mapper
        clazz.attach(lineNumberMapper);
    }

    @Override
    public void afterMethod(Config config, Clazz clazz, SootMethod method, ModuleBuilder mb, Function function) throws IOException {
        super.afterMethod(config, clazz, method, mb, function);

        ClassDataBundle classBundle = clazz.getAttachment(ClassDataBundle.class);

        // don't try to generate shadow frames for native or abstract methods
        // or methods that don't have any instructions in them
        if (method.isNative() || method.isAbstract() || !method.hasActiveBody()) {
            return;
        }

        BasicBlock entryBlock = function.getBasicBlocks().get(0);

        //Method has only a return null statement
        if (entryBlock.getInstructions().size() == 1) {
            return;
        }

        // build unit to line map
        Map<Unit, Integer> unitToLine = new HashMap<>();
        for (Unit unit : method.getActiveBody().getUnits()) {
            LineNumberTag tag = (LineNumberTag) unit.getTag("LineNumberTag");
            if (tag != null)
                unitToLine.put(unit, tag.getLineNumber());

        }

        // find out if debug information to be fetched for this method
        // skip debug information for class initializer, generated callbacks and bridge methods
        boolean includeDebuggerInfo = config.isDebug()
                && !Annotations.hasCallbackAnnotation(method)
                && !Annotations.hasBridgeAnnotation(method)
                && classBundle.methodsBeforeCompile.contains(method.getSignature());

        // build per-unit local snapshot map
        DebuggerDebugVariableSlicer localsDebugInfo = null;
        if (includeDebuggerInfo) {
            // build local variables map
            localsDebugInfo = new DebuggerDebugVariableSlicer(config, method);
        }

        // maps for debugger
        Map<Unit, Instruction> unitToInstruction = new HashMap<>();
        Map<Instruction, Integer> instructionToLineNo = new HashMap<>();
        Map<Instruction, Integer> instructionToDebugInfoIdx = new HashMap<>();

        // pick up created line number mapper to remap line numbers for kotlin. for pure java it provides
        // x->x mapping
        LineNumberMapper lineNumberMapper = clazz.getAttachment(LineNumberMapper.class);

        // insert line number and additional debug information
        // column is used as index for additional debug information, once it is compiled for each code position
        // be possible to pick up variables list that are visible at program counter
        int methodLineNumber = Integer.MAX_VALUE;
        int methodEndLineNumber = Integer.MIN_VALUE;
        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction instruction : bb.getInstructions()) {
                int lineNumber = -1;
                int columnAsDebugInfoIdx = 0;

                List<Object> units = instruction.getAttachments();
                for (Object object : units) {
                    if (!(object instanceof Unit))
                        continue;
                    Unit unit = (Unit) object;
                    LineNumberTag tag = (LineNumberTag) unit.getTag("LineNumberTag");
                    if (tag != null) {
                        // map line number to another one (required for kotlin)
                        lineNumber = lineNumberMapper.map(tag.getLineNumber());
                        methodLineNumber = Math.min(methodLineNumber, lineNumber);
                        methodEndLineNumber = Math.max(methodEndLineNumber, lineNumber);
                        instructionToLineNo.put(instruction, lineNumber);
                    }

                    // pick up local variable snaphot index for this unit. Index specifies snapshot of
                    // variables that is visible at this unit position
                    if (localsDebugInfo != null && localsDebugInfo.containsVariableSliceForUnit(unit)) {
                        int idx = localsDebugInfo.getVariableSliceIndexForUnit(unit);
                        if (idx >= 0) {
                            // using +1 here as ZERO considered as debug information missing
                            columnAsDebugInfoIdx = idx + 1;
                        }
                    }

                    // save mapping of unit to instructions for debugger needs
                    unitToInstruction.put(unit, instruction);
                }


                if (lineNumber != -1) {
                    // save, and will be attached once diSubprogram is created
                    instructionToLineNo.put(instruction, lineNumber);
                    if (columnAsDebugInfoIdx != 0)
                        instructionToDebugInfoIdx.put(instruction, columnAsDebugInfoIdx);
                }
            }
        }

        if (methodLineNumber == Integer.MAX_VALUE) {
            // there was no debug information for this method
            // it will be not possible to resolve variables, just return
            return;
        }

        // forward definition for variables
        // FIXME: seems locals list is not supported in DISubprogram, don't add it for now
        // DILocalVariableList diVariableList = new DILocalVariableList(mb);

        // forward definition for subprogram
        DISubprogram diSubprogram = new DISubprogram(mb, function.ref().getName(), classBundle.diFile,
                methodLineNumber, classBundle.getDummySubprogramType(mb), classBundle.diCompileUnit,
                null /*diVariableList*/);

        // add debug information to function attributes
        function.setDebugMetadata(diSubprogram.toDebugMetadata());

        // use cache to re-use same DILocation for same line/col
        LocationCache diLocationCache = new LocationCache(mb, diSubprogram);

        // attach debug line number information to each instruction that is in map
        for (Map.Entry<Instruction, Integer> e : instructionToLineNo.entrySet()) {
            Instruction instruction = e.getKey();
            int lineNo = e.getValue();
            int debugInfoIdx = instructionToDebugInfoIdx.getOrDefault(instruction, 0);
            instruction.addMetadata(diLocationCache.get(lineNo, debugInfoIdx).toDebugMetadata());
        }


        if (!includeDebuggerInfo) {
            return;
        }

        //
        //
        // debugger only part bellow
        //
        //

        // make a list of instructions for instrumented hook call
        // need to skip identity instructions, otherwise there is a risk that debugger will stop before arguments are
        // being copied to locals
        Unit firstHooksUnit = null;
        for (Unit unit : method.getActiveBody().getUnits()) {
            if (!(unit instanceof IdentityStmt)) {
                firstHooksUnit = unit;
                break;
            }
        }

        Instruction firstHooksInst = firstHooksUnit != null ? unitToInstruction.get(firstHooksUnit) : null;
        if (firstHooksInst == null) {
            // this means code doesn't contain anything beside identity statement which is not possible
            return;
        }

        // get all instruction that are subject to be instrumented after last Identity Statement
        boolean startInstrumenting = false;
        Map<Integer, Instruction> hookInstructionLines = new HashMap<>();
        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction instruction : bb.getInstructions()) {
                if (!startInstrumenting && firstHooksInst != instruction)
                    continue;
                startInstrumenting = true;
                Integer lineNo = instructionToLineNo.get(instruction);
                if (lineNo == null || hookInstructionLines.containsKey(lineNo))
                    continue;
                hookInstructionLines.put(lineNo, instruction);
            }
        }

        // instrument hooks call, there is known line range, create global for method breakpoints
        int arraySize = ((methodEndLineNumber - methodLineNumber + 1) + 7) / 8;
        // global value to this array (without values as zeroinit)
        Global bpTable = new Global(Symbols.bptableSymbol(method), Linkage.internal,
                new ZeroInitializer(new ArrayType(arraySize, Type.I8)));
        mb.addGlobal(bpTable);
        // cast to byte pointer
        ConstantBitcast bpTableRef = new ConstantBitcast(bpTable.ref(), Type.I8_PTR);
        for (Map.Entry<Integer, Instruction> e : hookInstructionLines.entrySet()) {
            Instruction instr = e.getValue();
            int lineNo = e.getKey();
            int debugInfoIdx = instructionToDebugInfoIdx.getOrDefault(instr, 0);
            injectHookInstrumented(diLocationCache, lineNo, debugInfoIdx, lineNo - methodLineNumber, function, bpTableRef, instr);
        }

        // at this point it is required to attach debug information for locals to be able pick them latter
        // this functionality moved to DebuggerDebugVariableSlicer class


        //
        //
        // Processing local variables
        //
        //

        // at this point it is required to attach debug information for locals to be able pick them latter
        // this functionality moved to DebuggerDebugVariableSlicer class

        // build local to alloca map
        Map<Local, Alloca> localToAlloca = new HashMap<>();
        for (BasicBlock bb : function.getBasicBlocks()) {
            for (Instruction instruction : bb.getInstructions()) {
                // if it is alloca -- save it to map (
                if (!(instruction instanceof Alloca))
                    continue;

                // find Local
                Alloca alloca = (Alloca) instruction;
                for (Object o : alloca.getAttachments()) {
                    if (!(o instanceof Local))
                        continue;
                    Local local = (Local) o;
                    if (local.getIndex() < 0)
                        continue;
                    localToAlloca.put(local, alloca);
                }
            }
        }

        // find out arguments
        // as kotlin case shows that generated code can re-use slots initially designated for
        // parameters for local variables we can't rely on local index only, lets find argument locals using
        // assignment from parameters
        // RoboVM added: ignore parameters locals in split
        Set<Local> parameterLocals = new HashSet<>();
        for (Unit u : method.getActiveBody().getUnits()) {
            if (u instanceof IdentityStmt && ((IdentityStmt)u).getRightOp() instanceof ParameterRef &&
                    ((IdentityStmt)u).getLeftOpBox().getValue() instanceof Local) {
                Local l = (Local)((IdentityStmt)u).getLeftOpBox().getValue();
                parameterLocals.add(l);
            }
        }

        // RoboVM adds env to each method call, and adds this to all not static method
        // so real argument index will start from 3 in static method and 2 in not static
        int argStartOffset = method.isStatic() ? 3 : 2;

        // generate variable dwarf information for all locals that inserts LLVM_DBG_DECLARE calls starting at
        // firstHooksInst
        for (Map.Entry<Local, Alloca> e : localToAlloca.entrySet()) {
            Local local = e.getKey();
            Alloca alloca = e.getValue();

            // get arg idx as: in dwarf arg index starts from 1
            int argIdx = 0;
            if (local.getIndex() <  method.getParameterCount() && parameterLocals.contains(local)) {
                argIdx = argStartOffset + local.getIndex();
            }

            // variable start line doesn't matter as all local are being declared at top
            //noinspection UnnecessaryLocalVariable
            int varStartLine = methodLineNumber;

            // add llvm.dbg.declare call
            DILocalVariable diLocalVariable = new DILocalVariable(mb, local.getName(), varStartLine, argIdx,
                    classBundle.diFile, diSubprogram,
                    classBundle.getDummyJavaVariableType(mb));

            // insert before firstHooksInst, the order of these calls will be mess but it doesn't matter actually
            Call call = new Call(Functions.LLVM_DBG_DECLARE,
                    new MetadataValue(new VariableRef(alloca.getResult().getName(), new PointerType(alloca.getResult().getType()))),
                    diLocalVariable.ref(), new MetadataValue(new NamedMetadata.Ref("DIExpression()")));
            call.addMetadata((diLocationCache.get(varStartLine, 0)).toDebugMetadata());
            firstHooksInst.getBasicBlock().insertBefore(firstHooksInst, call);

            // save variable to the list
            // FIXME: it is not required for now, commenting out for now
            // diVariableList.add(diLocalVariable);
        }

        // remember method debug information
        classBundle.methods.add(new MethodDataBundle(function.getName(), methodLineNumber, methodEndLineNumber,
                localsDebugInfo));
    }

    @Override
    public void afterObjectFile(Config config, Clazz clazz, File objectFile, ObjectFile objectFileData) throws IOException {
        super.afterObjectFile(config, clazz, objectFile, objectFileData);

        ClassDataBundle classBundle = clazz.getAttachment(ClassDataBundle.class);

        // pick DWARF debug information about local variables from objective file
        if (config.isDebug()) {

            // get debug information from objective file and write it to file cache
            DwarfDebugObjectFileInfo debugInfo = objectFileData.getDebugInfo();
            if (debugInfo != null) {
                processDebugInformation(config, clazz, debugInfo, classBundle, objectFileData);
            }
        }

        // reset bundle data
        clazz.removeAttachement(classBundle);
    }

    private void processDebugInformation(Config config, Clazz clazz, DwarfDebugObjectFileInfo debugInfo, ClassDataBundle classBundle,
                                         ObjectFile objectFileData) {
        // get symbol from object file
        String symbolPrefix = (config.getOs().getFamily() == OS.Family.darwin ? "_" : "");
        Map<String, Symbol> symbols = objectFileData.getSymbols().stream()
                .filter(s -> s.getName().startsWith(symbolPrefix + Symbols.EXTERNAL_SYMBOL_PREFIX))
                .collect(Collectors.toMap(Symbol::getName, e -> e));

        // now it is a task to combine it with data received during compilation
        List<DebuggerDebugMethodInfo.RawData> methods = new ArrayList<>();
        for (MethodDataBundle methodBundle :  classBundle.methods) {
            DwarfDebugMethodInfo dbgMethodInfo = debugInfo.methodBySignature(methodBundle.signature);
            List<LineInfo> lineInfos = null;
            Symbol symbol = symbols.get(symbolPrefix + methodBundle.signature);
            if (symbol != null)
                lineInfos = objectFileData.getLineInfos(symbol);
            if (dbgMethodInfo == null || lineInfos == null) {
                config.getLogger().warn("Failed to get debug information for method %s in class %s",
                        methodBundle.signature, clazz.getClassName());
                continue;
            }

            if (lineInfos.size() == 0)
                continue;


            DebuggerDebugMethodInfo.RawData debuggerMethodInfo = buildDebuggerMethodInfo(config, clazz, symbol, dbgMethodInfo, methodBundle, lineInfos);
            methods.add(debuggerMethodInfo);
        }

        // dump final info to file
        DebuggerDebugObjectFileInfo.RawData finalDebugInfo = clazz.getAttachment(DebuggerDebugObjectFileInfo.RawData.class);
        if (finalDebugInfo != null)
            clazz.removeAttachement(finalDebugInfo);
        finalDebugInfo = new DebuggerDebugObjectFileInfo.RawData(getJdwpSourceFile(clazz), methods.toArray(new DebuggerDebugMethodInfo.RawData[0]));

        // save as attachment to class file
        clazz.attach(finalDebugInfo);
    }

    private DebuggerDebugMethodInfo.RawData buildDebuggerMethodInfo(Config config, Clazz clazz, Symbol symbol,
                                                                    DwarfDebugMethodInfo dbgMethodInfo, MethodDataBundle methodBundle,
                                                                    List<LineInfo> lineInfos) {
        // sort line info by address an build
        lineInfos.sort(Comparator.comparingLong(LineInfo::getAddress));

        // remove duplicates if there are any in sequence
        int last = -1;
        Iterator<LineInfo> it = lineInfos.iterator();
        while (it.hasNext()) {
            LineInfo l = it.next();
            if (l.getColumnNumber() == last)
                it.remove();
            else
                last = l.getColumnNumber();
        }

        // pick up created line number mapper to remap line numbers for kotlin. for pure java it provides
        // x->x mapping
        LineNumberMapper lineNumberMapper = clazz.getAttachment(LineNumberMapper.class);

        // process variable slices -- assign debug information to each Local
        DebuggerDebugVariableSlicer localsDebugInfo = methodBundle.variablesInfo;

        // maps snapshot index to snapshot data
        // data is integer array where information comes in sequence of pairs: index to variable, index to alloca
        Map<LocalVariable, DebuggerDebugVariableInfo> localVariableToDebugVariable = new HashMap<>();
        Map<DebuggerDebugVariableInfo, Integer> usedVariables = new HashMap<>();
        Map<DwarfDebugVariableInfo, Integer> usedAllocas = new HashMap<>();
        TreeMap<Integer, int[]> slices = new TreeMap<>();
        TreeMap<Integer, Integer> offsetToSlice = new TreeMap<>();
        for (LineInfo li : lineInfos) {
            int sliceIdx = li.getColumnNumber();

            // save offset to slice info
            int offset = (int)(li.getAddress() - symbol.getAddress());
            offsetToSlice.put(offset, sliceIdx);

            if (slices.containsKey(sliceIdx))
                continue;

            if (sliceIdx == 0) {
                // special case -- no variables at point, create empty snapshot
                slices.put(sliceIdx, new int[]{});
                continue;
            }

            // process slice
            // in Dwarf slice information is kept starting from 1, but in VO it starts from 0
            DebuggerDebugVariableSlicer.UnitVariableSlice slice = localsDebugInfo.getVariableSlice(sliceIdx - 1);
            Chain<Unit> units = localsDebugInfo.getMethod().getActiveBody().getUnits();
            int[] sliceData = new int[slice.variables.size() * 2];
            int written = 0;
            for (int idx = 0; idx < slice.variables.size(); idx++) {
                // get alloca for this local from Dwarf info
                LocalVariable v = slice.variables.get(idx);
                DebuggerDebugVariableInfo var = localVariableToDebugVariable.get(v);
                if (var == null) {
                    // convert to debug variable
                    int startLine;
                    if (v.getStartUnit() == null) {
                        startLine = methodBundle.startLine;
                    } else {
                        LineNumberTag tag = (LineNumberTag) v.getStartUnit().getTag("LineNumberTag");
                        if (tag != null)
                            startLine = lineNumberMapper.map(tag.getLineNumber());
                        else
                            startLine = methodBundle.startLine;
                    }
                    int finalLine;
                    if (v.getEndUnit() == null) {
                        finalLine = methodBundle.finalLine;
                    } else {
                        // get previous as v.getEndUnit() is exclusive
                        Unit endUnit = units.getPredOf(v.getEndUnit());
                        LineNumberTag tag = null;
                        if (endUnit != null)
                            tag = (LineNumberTag) endUnit.getTag("LineNumberTag");
                        if (tag != null)
                            finalLine = lineNumberMapper.map(tag.getLineNumber());
                        else
                            finalLine = methodBundle.startLine;
                    }

                    var = new DebuggerDebugVariableInfo(v.getName(), v.getDescriptor(),
                            v.getIndex() < localsDebugInfo.getMethod().getParameterCount(), startLine, finalLine);
                    localVariableToDebugVariable.put(v, var);
                }

                // get corresponding local
                Local local = slice.locals.get(idx);
                DwarfDebugVariableInfo alloca = dbgMethodInfo.variableByName(local.getName());
                if (alloca == null) {
                    // bad: variable should be visible at this point but value in slot has wrong type
                    if (config != null && config.getHome().isDev()) {
                        config.getLogger().error("Alloca not found for variable " + local.getName());
                    }
                    continue;
                }

                // get indexes
                int varIdx;
                if (usedVariables.containsKey(var)) {
                    varIdx = usedVariables.get(var);
                } else {
                    varIdx = usedVariables.size();
                    usedVariables.put(var, varIdx);
                }
                int allocaIdx;
                if (usedAllocas.containsKey(alloca)) {
                    allocaIdx = usedAllocas.get(alloca);
                } else {
                    allocaIdx = usedAllocas.size();
                    usedAllocas.put(alloca, allocaIdx);
                }

                // add them to slice data
                sliceData[written++] = varIdx;
                sliceData[written++] = allocaIdx;
            }

            // if fails to map variable size could be smaller than expected
            if (written < sliceData.length) {
                sliceData = Arrays.copyOf(sliceData, written);
            }

            slices.put(sliceIdx, sliceData);
        }

        // convert data to be prepared for saving into DebuggerDebugMethodInfo.RawData
        int[][] rawSlices = slices.values().toArray(new int[0][]);
        DwarfDebugVariableInfo[] rawAllocas = new DwarfDebugVariableInfo[usedAllocas.size()];
        usedAllocas.forEach((alloca, idx) -> rawAllocas[idx] = alloca);
        DebuggerDebugVariableInfo[] rawVariables = new DebuggerDebugVariableInfo[usedVariables.size()];
        usedVariables.forEach((variable, idx) -> rawVariables[idx] = variable);
        int[] rawOffsets = new int[offsetToSlice.size()];
        int[] rawOffsetSliceIndexes = new int[offsetToSlice.size()];
        int idx = 0;
        for (Map.Entry<Integer,Integer> e : offsetToSlice.entrySet()) {
            rawOffsets[idx] = e.getKey();
            rawOffsetSliceIndexes[idx] = e.getValue();
            idx += 1;
        }

        // remove class prefix from method name
        String methodName = dbgMethodInfo.signature();
        if (methodName.startsWith("[J]" + clazz.getClassName() + "."))
            methodName = methodName.substring(clazz.getClassName().length() + 4);
        //noinspection UnnecessaryLocalVariable
        DebuggerDebugMethodInfo.RawData mi = new DebuggerDebugMethodInfo.RawData(methodName, methodBundle.startLine, methodBundle.finalLine,
                rawVariables, rawAllocas, rawOffsets, rawOffsetSliceIndexes, rawSlices);
        return mi;
    }

    /** injects calls to _bcHookInstrumented to allow breakpoints/step by step debugging */
    private void injectHookInstrumented(LocationCache locationCache, int lineNo, int debugeInfoIdx, int lineNumberOffset,
                                        Function function, Constant bpTableRef, Instruction instruction) {
        BasicBlock block = instruction.getBasicBlock();
        // prepare a call to following function:
        // void _bcHookInstrumented(DebugEnv* debugEnv, jint lineNumber, jint lineNumberOffset, jbyte* bptable, void* pc)

        // pick params
        Value debugEnv = function.getParameterRef(0);
        Variable pc = function.newVariable(Type.I8_PTR);
        Call getPcCall = new Call(pc, Functions.GETPC, new Value[0]);
        block.insertBefore(instruction, getPcCall);

        // lineNumberOffset is zero as single breakpoint table per class
        Call bcHookInstrumented = new Call(Functions.BC_HOOK_INSTRUMENTED, debugEnv, new IntegerConstant(lineNo),
                new IntegerConstant(lineNumberOffset), bpTableRef, pc.ref());
        block.insertBefore(instruction, bcHookInstrumented);

        // attach line number metadata otherwise stack entry will have previous line number index
        bcHookInstrumented.addMetadata(locationCache.get(lineNo, debugeInfoIdx).toDebugMetadata());
    }

    /**
     * Simple file name resolution to be included as Dwarf debug entry, for LineNumbers there is no need in absolute file location, just in name
     */
    private String getDwarfSourceFile(Clazz clazz) {
        String sourceFile;
        String ext = ".java";
        String className = clazz.getInternalName();
        // create source file name from class internal name to preserve full path to inner classes and
        // lambdas as it is required for dsymutils fix
        // but also look for "SourceFileTag" to pick up proper extension in case of kotlin and others
        SourceFileTag sourceFileTag = (SourceFileTag) clazz.getSootClass().getTag("SourceFileTag");
        if (sourceFileTag != null) {
            String tagSourceFile = sourceFileTag.getSourceFile();
            int extIdx = tagSourceFile.lastIndexOf('.');
            if (extIdx > 0)
                ext = tagSourceFile.substring(extIdx);
        }

        if (className.contains("/"))
            sourceFile = className.substring(clazz.getInternalName().lastIndexOf("/") + 1) + ext;
        else
            sourceFile = className + ext;

        return sourceFile;
    }

    /**
     * Simple source file path resolution
     */
    private String getDwarfSourceFilePath(Clazz clazz) {
        String sourcePath = clazz.getPath().toString();
        if (!sourcePath.endsWith("/"))
            sourcePath += "/";
        if (!sourcePath.startsWith("/"))
            sourcePath = "/" + sourcePath;

        String className = clazz.getInternalName();
        if (className.contains("/")) {
            sourcePath += className.substring(0, clazz.getInternalName().lastIndexOf("/") + 1);
        }

        return sourcePath;
    }

    /**
     * picks real source file name, will be used with JDWP ReferenceType(2).SourceFile(7) command
     */
    private String getJdwpSourceFile(Clazz clazz) {
        String sourceFile;
        // create source file name from class internal name to preserve full path to inner classes and
        // lambdas as it is required for dsymutils fix
        // but also look for "SourceFileTag" to pick up proper extension in case of kotlin and others
        SourceFileTag sourceFileTag = (SourceFileTag) clazz.getSootClass().getTag("SourceFileTag");
        if (sourceFileTag != null) {
            sourceFile = sourceFileTag.getSourceFile();
        } else {
            sourceFile = clazz.getInternalName();
            int sepIdx = sourceFile.lastIndexOf('/');
            if (sepIdx > 0)
                sourceFile = sourceFile.substring(sepIdx + 1);
            sepIdx = sourceFile.indexOf('$');
            if (sepIdx > 0)
                sourceFile = sourceFile.substring(0, sepIdx);

            // there is no name attached so guess it was compiled from java
            sourceFile += ".java";
        }

        return sourceFile;
    }

    /**
     * data bundle that contains debug information for class
     */
    private static class ClassDataBundle {
        DICompileUnit diCompileUnit;
        DIFile diFile;
        NamedMetadata flags;

        // information for debugger

        // basic type definitions (required for debugger purpose)
        // variable location information -- nobody cares (e.g. there is no debugger that relies on this data)
        // if future it shall be fixed
        private DIBasicType dummyJavaVariableType;

        // empty metadata -- to reuse where required
        private NamedMetadata<DIMetadataValueList> emptyMetadata;

        // basic subprogramType definition, will define just empty
        private DISubroutineType dummySubprogramType;

        // debug information for methods
        List<MethodDataBundle> methods;

        // method signatures before compilation starts
        Set<String> methodsBeforeCompile;

        DIBasicType getDummyJavaVariableType(ModuleBuilder mb) {
            if (dummyJavaVariableType == null) {
                dummyJavaVariableType = new DIBasicType(mb, "DummyType", 32, DwarfConst.TypeKind.DW_ATE_address);
            }
            return dummyJavaVariableType;
        }

        NamedMetadata<DIMetadataValueList> getEmptyMetadata(ModuleBuilder mb) {
            if (emptyMetadata == null) {
                emptyMetadata = new NamedMetadata<>(mb, new DIMetadataValueList());
            }
            return emptyMetadata;
        }

        DISubroutineType getDummySubprogramType(ModuleBuilder mb) {
            if (dummySubprogramType == null) {
                dummySubprogramType = new DISubroutineType(mb, getEmptyMetadata(mb));
            }
            return dummySubprogramType;
        }
    }

    /**
     * interface declares line number mapping if it is required (e.g. kotlin case)
     */
    public interface LineNumberMapper {
        LineNumberMapper DIRECT = l -> l;
        int map(int l);
    }

    /**
     * Data bundle that contains debug information about variables -- required for debugger only
     */
    private static class MethodDataBundle {
        final String signature;
        final int startLine;
        final int finalLine;
        final DebuggerDebugVariableSlicer variablesInfo;

        MethodDataBundle(String signature, int startLine, int finalLine, DebuggerDebugVariableSlicer variablesInfo) {
            this.signature = signature;
            this.startLine = startLine;
            this.finalLine = finalLine;
            this.variablesInfo = variablesInfo;
        }
    }

    /**
     * Used to minimize number for DILocation being created for same locations (e.g. with same line and cols)
     */
    private static class LocationCache {
        final ModuleBuilder mb;
        final DISubprogram scope;
        final Map<Long, DILocation> cache = new HashMap<>();

        LocationCache(ModuleBuilder mb, DISubprogram scope) {
            this.mb = mb;
            this.scope = scope;
        }

        DILocation get(int line, int col) {
            long key = (long) line  << 32;
            key += col;
            DILocation loc = cache.get(key);
            if (loc == null) {
                loc = new DILocation(mb, line, col, scope);
                cache.put(key, loc);
            }

            return loc;
        }
    }
}
