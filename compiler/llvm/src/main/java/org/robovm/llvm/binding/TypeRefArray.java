/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.robovm.llvm.binding;

public class TypeRefArray {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected TypeRefArray(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TypeRefArray obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        LLVMJNI.delete_TypeRefArray(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setValue(TypeRef value) {
    LLVMJNI.TypeRefArray_value_set(swigCPtr, this, TypeRef.getCPtr(value), value);
  }

  public TypeRef getValue() {
    long cPtr = LLVMJNI.TypeRefArray_value_get(swigCPtr, this);
    return (cPtr == 0) ? null : new TypeRef(cPtr, false);
  }

  public TypeRefArray(int nelements) {
    this(LLVMJNI.new_TypeRefArray(nelements), true);
  }

  public TypeRef get(int index) {
    long cPtr = LLVMJNI.TypeRefArray_get(swigCPtr, this, index);
    return (cPtr == 0) ? null : new TypeRef(cPtr, false);
  }

  public void set(int index, TypeRef value) {
    LLVMJNI.TypeRefArray_set(swigCPtr, this, index, TypeRef.getCPtr(value), value);
  }

}
