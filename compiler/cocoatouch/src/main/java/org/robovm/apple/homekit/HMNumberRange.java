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
package org.robovm.apple.homekit;

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
import org.robovm.apple.corelocation.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 11.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("HomeKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/HMNumberRange/*</name>*/ 
    extends /*<extends>*/NSObject/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class HMNumberRangePtr extends Ptr<HMNumberRange, HMNumberRangePtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(HMNumberRange.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    protected HMNumberRange() {}
    protected HMNumberRange(Handle h, long handle) { super(h, handle); }
    protected HMNumberRange(SkipInit skipInit) { super(skipInit); }
    public HMNumberRange(NSNumber minValue, NSNumber maxValue) { super((Handle) null, create(minValue, maxValue)); retain(getHandle()); }
    /*</constructors>*/
    /*<properties>*/
    @Property(selector = "minValue")
    public native NSNumber getMinValue();
    @Property(selector = "maxValue")
    public native NSNumber getMaxValue();
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @Method(selector = "numberRangeWithMinValue:maxValue:")
    protected static native @Pointer long create(NSNumber minValue, NSNumber maxValue);
    @Method(selector = "numberRangeWithMinValue:")
    public static native HMNumberRange createWithMinValue(NSNumber minValue);
    @Method(selector = "numberRangeWithMaxValue:")
    public static native HMNumberRange createWithMaxValue(NSNumber maxValue);
    /*</methods>*/
}
