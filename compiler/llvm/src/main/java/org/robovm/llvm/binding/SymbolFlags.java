/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.robovm.llvm.binding;

public enum SymbolFlags {
  SF_None(0),
  SF_Undefined(1 << 0),
  SF_Global(1 << 1),
  SF_Weak(1 << 2),
  SF_Absolute(1 << 3),
  SF_Common(1 << 4),
  SF_Indirect(1 << 5),
  SF_Exported(1 << 6),
  SF_FormatSpecific(1 << 7),
  SF_Thumb(1 << 8),
  SF_Hidden(1 << 9),
  SF_Const(1 << 10),
  SF_Executable(1 << 11);

  public final int swigValue() {
    return swigValue;
  }

  public static SymbolFlags swigToEnum(int swigValue) {
    SymbolFlags[] swigValues = SymbolFlags.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (SymbolFlags swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + SymbolFlags.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private SymbolFlags() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private SymbolFlags(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private SymbolFlags(SymbolFlags swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

