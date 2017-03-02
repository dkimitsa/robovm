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
package org.robovm.apple.uikit;

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
import org.robovm.apple.coreanimation.*;
import org.robovm.apple.coregraphics.*;
import org.robovm.apple.coredata.*;
import org.robovm.apple.coreimage.*;
import org.robovm.apple.coretext.*;
import org.robovm.apple.corelocation.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 10.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("UIKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/UIGraphicsImageRenderer/*</name>*/ 
    extends /*<extends>*/UIGraphicsRenderer/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class UIGraphicsImageRendererPtr extends Ptr<UIGraphicsImageRenderer, UIGraphicsImageRendererPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(UIGraphicsImageRenderer.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public UIGraphicsImageRenderer() {}
    protected UIGraphicsImageRenderer(Handle h, long handle) { super(h, handle); }
    protected UIGraphicsImageRenderer(SkipInit skipInit) { super(skipInit); }
    @Method(selector = "initWithSize:")
    public UIGraphicsImageRenderer(@ByVal CGSize size) { super((SkipInit) null); initObject(init(size)); }
    @Method(selector = "initWithSize:format:")
    public UIGraphicsImageRenderer(@ByVal CGSize size, UIGraphicsImageRendererFormat format) { super((SkipInit) null); initObject(init(size, format)); }
    @Method(selector = "initWithBounds:format:")
    public UIGraphicsImageRenderer(@ByVal CGRect bounds, UIGraphicsImageRendererFormat format) { super((SkipInit) null); initObject(init(bounds, format)); }
    /*</constructors>*/
    /*<properties>*/
    
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @Method(selector = "initWithSize:")
    protected native @Pointer long init(@ByVal CGSize size);
    @Method(selector = "initWithSize:format:")
    protected native @Pointer long init(@ByVal CGSize size, UIGraphicsImageRendererFormat format);
    @Method(selector = "initWithBounds:format:")
    protected native @Pointer long init(@ByVal CGRect bounds, UIGraphicsImageRendererFormat format);
    @Method(selector = "imageWithActions:")
    public native UIImage toImage(@Block VoidBlock1<UIGraphicsImageRendererContext> actions);
    @Method(selector = "PNGDataWithActions:")
    public native NSData toPNG(@Block VoidBlock1<UIGraphicsImageRendererContext> actions);
    @Method(selector = "JPEGDataWithCompressionQuality:actions:")
    public native NSData toJPEG(@MachineSizedFloat double compressionQuality, @Block VoidBlock1<UIGraphicsImageRendererContext> actions);
    /*</methods>*/
}