/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.client.jme;

import com.jme.math.Vector3f;

/**
 * A very simplistic third person camera model
 * 
 * @author paulby
 */
public class FrontHackPersonCameraProcessor extends ThirdPersonCameraProcessor {

    public FrontHackPersonCameraProcessor() {
        super();
        offset = new Vector3f(0,2.2f,4);
        cameraLook = new Vector3f(0,0,-1);
        cameraZoom = -0.2f;
    }
    
}
