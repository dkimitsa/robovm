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
 * @since Available in iOS 11.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Marshaler(Bits.AsMachineSizedIntMarshaler.class)/*</annotations>*/
public final class /*<name>*/MTLResourceUsage/*</name>*/ extends Bits</*<name>*/MTLResourceUsage/*</name>*/> {
    /*<values>*/
    public static final MTLResourceUsage None = new MTLResourceUsage(0L);
    public static final MTLResourceUsage Read = new MTLResourceUsage(1L);
    public static final MTLResourceUsage Write = new MTLResourceUsage(2L);
    /**
     * @since Available in iOS 11.0 and later.
     * @deprecated Deprecated in iOS 16.0. Use MTLResourceUsageRead
     */
    @Deprecated
    public static final MTLResourceUsage Sample = new MTLResourceUsage(4L);
    /*</values>*/

    /*<bind>*/
    /*</bind>*/
    /*<constants>*//*</constants>*/
    /*<methods>*//*</methods>*/

    private static final /*<name>*/MTLResourceUsage/*</name>*/[] values = _values(/*<name>*/MTLResourceUsage/*</name>*/.class);

    public /*<name>*/MTLResourceUsage/*</name>*/(long value) { super(value); }
    private /*<name>*/MTLResourceUsage/*</name>*/(long value, long mask) { super(value, mask); }
    protected /*<name>*/MTLResourceUsage/*</name>*/ wrap(long value, long mask) {
        return new /*<name>*/MTLResourceUsage/*</name>*/(value, mask);
    }
    protected /*<name>*/MTLResourceUsage/*</name>*/[] _values() {
        return values;
    }
    public static /*<name>*/MTLResourceUsage/*</name>*/[] values() {
        return values.clone();
    }
}
