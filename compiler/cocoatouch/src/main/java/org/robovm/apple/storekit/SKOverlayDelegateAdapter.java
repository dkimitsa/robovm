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
package org.robovm.apple.storekit;

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
import org.robovm.apple.uikit.*;
import org.robovm.apple.coregraphics.*;
/*</imports>*/

/*<javadoc>*/
/*</javadoc>*/
/*<annotations>*//*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/SKOverlayDelegateAdapter/*</name>*/ 
    extends /*<extends>*/NSObject/*</extends>*/ 
    /*<implements>*/implements SKOverlayDelegate/*</implements>*/ {

    /*<ptr>*/
    /*</ptr>*/
    /*<bind>*/
    /*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*//*</constructors>*/
    /*<properties>*/
    
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @NotImplemented("storeOverlay:didFailToLoadWithError:")
    public void didFailToLoad(SKOverlay overlay, NSError error) {}
    @NotImplemented("storeOverlay:willStartPresentation:")
    public void willStartPresentation(SKOverlay overlay, SKOverlayTransitionContext transitionContext) {}
    @NotImplemented("storeOverlay:didFinishPresentation:")
    public void didFinishPresentation(SKOverlay overlay, SKOverlayTransitionContext transitionContext) {}
    @NotImplemented("storeOverlay:willStartDismissal:")
    public void willStartDismissal(SKOverlay overlay, SKOverlayTransitionContext transitionContext) {}
    @NotImplemented("storeOverlay:didFinishDismissal:")
    public void didFinishDismissal(SKOverlay overlay, SKOverlayTransitionContext transitionContext) {}
    /*</methods>*/
}
