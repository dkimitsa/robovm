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
package org.robovm.apple.matter;

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
import org.robovm.apple.security.*;
/*</imports>*/

/*<javadoc>*/

/*</javadoc>*/
/*<annotations>*/@Marshaler(ValuedEnum.AsMachineSizedSIntMarshaler.class) @Library("Matter")/*</annotations>*/
public enum /*<name>*/MTRInteractionErrorCode/*</name>*/ implements NSErrorCode {
    /*<values>*/
    Failure(1L),
    InvalidSubscription(125L),
    UnsupportedAccess(126L),
    UnsupportedEndpoint(127L),
    InvalidAction(128L),
    UnsupportedCommand(129L),
    InvalidCommand(133L),
    UnsupportedAttribute(134L),
    ConstraintError(135L),
    UnsupportedWrite(136L),
    ResourceExhausted(137L),
    NotFound(139L),
    UnreportableAttribute(140L),
    InvalidDataType(141L),
    UnsupportedRead(143L),
    DataVersionMismatch(146L),
    Timeout(148L),
    Busy(156L),
    UnsupportedCluster(195L),
    NoUpstreamSubscription(197L),
    NeedsTimedInteraction(198L),
    UnsupportedEvent(199L),
    PathsExhausted(200L),
    TimedRequestMismatch(201L),
    FailsafeRequired(202L);
    /*</values>*/

    /*<bind>*/static { Bro.bind(MTRInteractionErrorCode.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @GlobalValue(symbol="MTRInteractionErrorDomain", optional=true)
    public static native String getClassDomain();
    /*</methods>*/

    private final long n;

    private /*<name>*/MTRInteractionErrorCode/*</name>*/(long n) { this.n = n; }
    public long value() { return n; }
    public static /*<name>*/MTRInteractionErrorCode/*</name>*/ valueOf(long n) {
        for (/*<name>*/MTRInteractionErrorCode/*</name>*/ v : values()) {
            if (v.n == n) {
                return v;
            }
        }
        throw new IllegalArgumentException("No constant with value " + n + " found in "
            + /*<name>*/MTRInteractionErrorCode/*</name>*/.class.getName());
    }

    // bind wrap to include it in compilation as long as nserror enum is used 
    static { Bro.bind(NSErrorWrap.class); }
    @StronglyLinked
    public static class NSErrorWrap extends NSError {
        protected NSErrorWrap(SkipInit skipInit) {super(skipInit);}

        @Override public NSErrorCode getErrorCode() {
             try {
                 return  /*<name>*/MTRInteractionErrorCode/*</name>*/.valueOf(getCode());
             } catch (IllegalArgumentException e) {
                 return null;
             }
         }

        public static String getClassDomain() {
            /** must be inserted in value section */
            return /*<name>*/MTRInteractionErrorCode/*</name>*/.getClassDomain();
        }
    }
}
