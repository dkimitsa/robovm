/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.robovm.llvm.binding;

public class TypeRef {
  private transient long swigCPtr;

  protected TypeRef(long cPtr, @SuppressWarnings("unused") boolean futureUse) {
    swigCPtr = cPtr;
  }

  protected TypeRef() {
    swigCPtr = 0;
  }

  protected static long getCPtr(TypeRef obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public int hashCode() {
    return 31 + (int) (swigCPtr ^ (swigCPtr >>> 32));
  }

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
    TypeRef other = (TypeRef) obj;
    return swigCPtr == other.swigCPtr;
  }
}
