/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State$
 */
package org.jdesktop.wonderland.modules.simplewhiteboard.common;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.common.AppLaunchMethods;
import org.jdesktop.wonderland.modules.appbase.common.AppLaunchMethodsWonderland;

/**
 * The Whiteboard is a Wonderland app. It currently is only
 * launched from the world. TODO: revisit this.
 *
 * @author deronj
 */

@ExperimentalAPI
public class WhiteboardLaunchMethods extends AppLaunchMethodsWonderland {

    /** Create an instance of WhiteboardAppLaunchMethods */
    public WhiteboardLaunchMethods () {
	addLauncher(AppLaunchMethods.Launcher.WORLD);
    }
}