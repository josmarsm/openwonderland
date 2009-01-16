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
package org.jdesktop.wonderland.modules.appbase.client;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import com.jme.image.Texture;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;

/**
 * A window view is a visual representation of a window. A window can
 * have multiple views. Each view has a space in which it resides,
 * for example the world, the HUD, etc. 
 *
 * NOTE: concrete subclasses provide static factory creation methods.
 *
 * @author deronj
 */ 

@ExperimentalAPI
public abstract class Window2DView extends WindowView {

    /** A change flag which indicates all attributes of the window changed */
    public static final int CHANGED_ALL        = -1;

    /** A change flag which indicates that the window visibility changed */
    public static final int CHANGED_VISIBILITY = 0x01;

    /** A change flag which indicates that the window transform changed */
    public static final int CHANGED_TRANSFORM  = 0x02;

    /** A change flag which indicates that the window size changed */
    public static final int CHANGED_SIZE       = 0x04;

    /** A change flag which indicates that the app's window stack changed */
    public static final int CHANGED_STACK      = 0x08;

    /** A change flag which indicates that the top level attribute of the window changed */
    public static final int CHANGED_TOP_LEVEL  = 0x10;

    /** A change flag which indicates that the window title changed */
    public static final int CHANGED_TITLE      = 0x20;

    /** The texture of the window. */
    protected Texture texture; 

    /** 
     * Create a new instance of Window2DView.
     *
     * @param window The window this view displays.
     * @param spaceName The GUI space in which the view resides.
     */
    public Window2DView (Window2D window, String spaceName) {
	super(window, spaceName);
    }

    /** 
     * Clean up resources 
     */
    public void cleanup () {
	super.cleanup();
	texture = null;
    }

    /**
     * Updates all view based on current window state.
     *
     * @param changeMask This is an OR of all of the change flags (e.g. Window2DView.CHANGED_xxxx).
     * These indicate which window attributes have changed. These changes will be reflected in the view.
     */
    public abstract void update (int changeMask);

    /** 
     * Converts the given 3D mouse event into a 2D event and forwards it along  to the view's controlArb.
     *
     * @param window The window this view displays.
     * @param me3d The 3D mouse event to deliver.
     */
    public abstract void deliverEvent (Window2D window, MouseEvent3D me3d);

    // TODO: temporary until ImageGraphics can be fixed to allocated texture id when necessary
    public void forceTextureIdAssignment () {}
}
