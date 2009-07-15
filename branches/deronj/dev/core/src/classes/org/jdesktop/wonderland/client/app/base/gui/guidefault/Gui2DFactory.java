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
package org.jdesktop.wonderland.client.app.base.gui.guidefault;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.app.base.GuiFactory;
import org.jdesktop.wonderland.client.app.base.Window;
import org.jdesktop.wonderland.client.app.base.Window2D;
import org.jdesktop.wonderland.client.app.base.WindowFrame;
import org.jdesktop.wonderland.client.app.base.WindowView;

/**
 * The default 2D GUI factory (a singleton). This creates window views and frame objects
 * for 2D applications. The frames are implemented using a very rudimentary component system.
 *
 * @author deronj
 */

@ExperimentalAPI
public class Gui2DFactory implements GuiFactory {

   /** The logger for gui.guidefault */
    static final Logger logger = Logger.getLogger("wl.app.base.gui.guidefault");

    /** The singleton gui factory */
    private static GuiFactory guiFactory;

    /**
     * Returns the singleton GUI factory object.
     */
    public static GuiFactory getFactory () {
	if (guiFactory == null) {
	    guiFactory = new Gui2DFactory();
	}
	return guiFactory;
    }

    /**
     * {@inheritDoc}
     */
    public WindowView createView (Window window, String spaceName) {
	if        ("World".equals(spaceName)) {
	    return new ViewWorldDefault((Window2D)window);
	} else if ("HUD".equals(spaceName)) {
	    // TODO: not yet
	    //return new ViewHUD(window);
	    return null;
	}
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public WindowFrame createFrame (WindowView view) {
	if (view instanceof ViewWorldDefault) {
	    return new FrameWorldDefault(view);
	} else {
	    // TODO: notyet
	    // return new FrameHUD(view);
	    return null;
	}
    }

}
