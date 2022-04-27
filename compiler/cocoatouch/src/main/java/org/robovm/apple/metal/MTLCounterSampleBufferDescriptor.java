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
package org.robovm.apple.metal;

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
import org.robovm.apple.dispatch.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 14.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("Metal") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/MTLCounterSampleBufferDescriptor/*</name>*/ 
    extends /*<extends>*/NSObject/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class MTLCounterSampleBufferDescriptorPtr extends Ptr<MTLCounterSampleBufferDescriptor, MTLCounterSampleBufferDescriptorPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(MTLCounterSampleBufferDescriptor.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public MTLCounterSampleBufferDescriptor() {}
    protected MTLCounterSampleBufferDescriptor(Handle h, long handle) { super(h, handle); }
    protected MTLCounterSampleBufferDescriptor(SkipInit skipInit) { super(skipInit); }
    /*</constructors>*/
    /*<properties>*/
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "counterSet")
    public native MTLCounterSet getCounterSet();
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "setCounterSet:")
    public native void setCounterSet(MTLCounterSet v);
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "label")
    public native String getLabel();
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "setLabel:")
    public native void setLabel(String v);
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "storageMode")
    public native MTLStorageMode getStorageMode();
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "setStorageMode:")
    public native void setStorageMode(MTLStorageMode v);
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "sampleCount")
    public native @MachineSizedUInt long getSampleCount();
    /**
     * @since Available in iOS 14.0 and later.
     */
    @Property(selector = "setSampleCount:")
    public native void setSampleCount(@MachineSizedUInt long v);
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    
    /*</methods>*/
}
