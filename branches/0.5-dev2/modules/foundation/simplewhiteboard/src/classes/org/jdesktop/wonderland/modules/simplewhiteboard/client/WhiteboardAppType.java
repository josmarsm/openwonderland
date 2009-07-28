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
package org.jdesktop.wonderland.modules.simplewhiteboard.client;

import org.jdesktop.wonderland.modules.appbase.client.AppType2D;
import org.jdesktop.wonderland.modules.appbase.common.AppLaunchMethods;
import org.jdesktop.wonderland.modules.simplewhiteboard.common.WhiteboardLaunchMethods;
import org.jdesktop.wonderland.modules.simplewhiteboard.common.WhiteboardTypeName;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The AppType for a simple whiteboard
 *
 * @author deronj
 */

@ExperimentalAPI
public class WhiteboardAppType extends AppType2D {

    /** 
     * Return the name of the whiteboard app type.
     */
    public String getName () {
	return WhiteboardTypeName.WHITEBOARD_APP_TYPE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public AppLaunchMethods getLaunchMethods () {
	return new WhiteboardLaunchMethods();
    }
}