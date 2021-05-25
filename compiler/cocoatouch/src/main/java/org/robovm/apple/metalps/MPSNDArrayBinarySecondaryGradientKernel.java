/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.apple.metalps;

/*<imports>*/
import java.io.*;
import java.nio.*;
import java.util.*;
import org.robovm.objc.*;
import org.robovm.objc.annotation.*;
import org.robovm.objc.block.*;
import org.robovm.rt.*;
import org.robovm.rt.annotation.*;
import org.robovm.rt.bro.*;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.coregraphics.*;
import org.robovm.apple.metal.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 13.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("MetalPerformanceShaders") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/MPSNDArrayBinarySecondaryGradientKernel/*</name>*/ 
    extends /*<extends>*/MPSNDArrayMultiaryGradientKernel/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class MPSNDArrayBinarySecondaryGradientKernelPtr extends Ptr<MPSNDArrayBinarySecondaryGradientKernel, MPSNDArrayBinarySecondaryGradientKernelPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(MPSNDArrayBinarySecondaryGradientKernel.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public MPSNDArrayBinarySecondaryGradientKernel() {}
    protected MPSNDArrayBinarySecondaryGradientKernel(Handle h, long handle) { super(h, handle); }
    protected MPSNDArrayBinarySecondaryGradientKernel(SkipInit skipInit) { super(skipInit); }
    @Method(selector = "initWithDevice:")
    public MPSNDArrayBinarySecondaryGradientKernel(MTLDevice device) { super((SkipInit) null); initObject(init(device)); }
    @Method(selector = "initWithCoder:device:")
    public MPSNDArrayBinarySecondaryGradientKernel(NSCoder coder, MTLDevice device) { super((SkipInit) null); initObject(init(coder, device)); }
    @Method(selector = "initWithCoder:")
    public MPSNDArrayBinarySecondaryGradientKernel(NSCoder decoder) { super(decoder); }
    /*</constructors>*/
    /*<properties>*/
    @Property(selector = "supportsSecureCoding")
    public static native boolean supportsSecureCoding();
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @Method(selector = "initWithDevice:")
    protected native @Pointer long init(MTLDevice device);
    @Method(selector = "initWithCoder:device:")
    protected native @Pointer long init(NSCoder coder, MTLDevice device);
    @Method(selector = "encodeToCommandBuffer:primarySourceArray:secondarySourceArray:sourceGradient:gradientState:")
    public native MPSNDArray encode(MTLCommandBuffer cmdBuf, MPSNDArray primarySourceArray, MPSNDArray secondarySourceArray, MPSNDArray gradient, MPSState state);
    @Method(selector = "encodeToCommandBuffer:primarySourceArray:secondarySourceArray:sourceGradient:gradientState:destinationArray:")
    public native void encode(MTLCommandBuffer cmdBuf, MPSNDArray primarySourceArray, MPSNDArray secondarySourceArray, MPSNDArray gradient, MPSState state, MPSNDArray destination);
    /*</methods>*/
}
