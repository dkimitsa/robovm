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
package org.robovm.apple.assetslibrary;

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
import org.robovm.apple.imageio.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
 */
/*</javadoc>*/
/*<annotations>*/@Marshaler(ValuedEnum.AsMachineSizedSIntMarshaler.class) @Deprecated/*</annotations>*/
public enum /*<name>*/ALAssetOrientation/*</name>*/ implements ValuedEnum {
    /*<values>*/
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    Up(0L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    Down(1L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    Left(2L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    Right(3L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    UpMirrored(4L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    DownMirrored(5L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    LeftMirrored(6L),
    /**
     * @deprecated Deprecated in iOS 9.0. Use UIImageOrientation in the Photos framework instead
     */
    @Deprecated
    RightMirrored(7L);
    /*</values>*/

    /*<bind>*/
    /*</bind>*/
    /*<constants>*//*</constants>*/
    /*<methods>*//*</methods>*/

    private final long n;

    private /*<name>*/ALAssetOrientation/*</name>*/(long n) { this.n = n; }
    public long value() { return n; }
    public static /*<name>*/ALAssetOrientation/*</name>*/ valueOf(long n) {
        for (/*<name>*/ALAssetOrientation/*</name>*/ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException("No constant with value " + n + " found in " 
            + /*<name>*/ALAssetOrientation/*</name>*/.class.getName());
    }
}
