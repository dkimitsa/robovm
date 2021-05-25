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
package org.robovm.apple.metrickit;

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
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 13.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("MetricKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/MXNetworkTransferMetric/*</name>*/ 
    extends /*<extends>*/MXMetric/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class MXNetworkTransferMetricPtr extends Ptr<MXNetworkTransferMetric, MXNetworkTransferMetricPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(MXNetworkTransferMetric.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public MXNetworkTransferMetric() {}
    protected MXNetworkTransferMetric(Handle h, long handle) { super(h, handle); }
    protected MXNetworkTransferMetric(SkipInit skipInit) { super(skipInit); }
    /*</constructors>*/
    /*<properties>*/
    @Property(selector = "cumulativeWifiUpload")
    public native NSMeasurement<NSUnitInformationStorage> getCumulativeWifiUpload();
    @Property(selector = "cumulativeWifiDownload")
    public native NSMeasurement<NSUnitInformationStorage> getCumulativeWifiDownload();
    @Property(selector = "cumulativeCellularUpload")
    public native NSMeasurement<NSUnitInformationStorage> getCumulativeCellularUpload();
    @Property(selector = "cumulativeCellularDownload")
    public native NSMeasurement<NSUnitInformationStorage> getCumulativeCellularDownload();
    @Property(selector = "supportsSecureCoding")
    public static native boolean supportsSecureCoding();
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    
    /*</methods>*/
}
