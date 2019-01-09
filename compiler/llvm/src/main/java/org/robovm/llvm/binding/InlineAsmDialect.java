/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.robovm.llvm.binding;

public enum InlineAsmDialect {
  InlineAsmDialectATT,
  InlineAsmDialectIntel;

  public final int swigValue() {
    return swigValue;
  }

  public static InlineAsmDialect swigToEnum(int swigValue) {
    InlineAsmDialect[] swigValues = InlineAsmDialect.class.getEnumConstants();
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (InlineAsmDialect swigEnum : swigValues)
      if (swigEnum.swigValue == swigValue)
        return swigEnum;
    throw new IllegalArgumentException("No enum " + InlineAsmDialect.class + " with value " + swigValue);
  }

  @SuppressWarnings("unused")
  private InlineAsmDialect() {
    this.swigValue = SwigNext.next++;
  }

  @SuppressWarnings("unused")
  private InlineAsmDialect(int swigValue) {
    this.swigValue = swigValue;
    SwigNext.next = swigValue+1;
  }

  @SuppressWarnings("unused")
  private InlineAsmDialect(InlineAsmDialect swigEnum) {
    this.swigValue = swigEnum.swigValue;
    SwigNext.next = this.swigValue+1;
  }

  private final int swigValue;

  private static class SwigNext {
    private static int next = 0;
  }
}

