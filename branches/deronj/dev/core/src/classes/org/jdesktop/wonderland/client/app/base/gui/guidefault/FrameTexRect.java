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

import com.jme.image.Texture;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import org.jdesktop.wonderland.client.app.base.WindowView;
import org.jdesktop.wonderland.client.jme.utils.GraphicsUtils;
import org.jdesktop.wonderland.client.jme.utils.TexturedQuad;
import com.jme.bounding.BoundingBox;

/**
 * A textured rectangle component derived from <code>FrameRect</code>.
 *
 * NOTE: The entire texture is displayed. If you wish to display only a portion of
 * the texture on the object you must subclass this class, override createGeometry, and modify the 
 * texture coordinates appropriately.
 *
 * @author deronj
 */ 

@ExperimentalAPI
public class FrameTexRect extends FrameRect {

    /** The texture of the component. */
    protected Texture texture;

    /** 
     * Create a new instance of <code>FrameTexRect</code> with a default name.
     *
     * @param view The view the frame encloses.
     * @param gui The event handler.
     * @param texture The texture to display in the rectangle.
     * @param width The width of side in local coordinates.
     * @param height The height of side in local coordinates.
     */
    public FrameTexRect (WindowView view, /*TODO Gui2D*/ Object gui, Texture texture, float width, float height) {
        this("FrameTexRect", view, gui, texture, width, height);
    }

    /** 
     * Create a new instance of <code>FrameTexRect</code>.
     *
     * @param name The node name.
     * @param view The view the frame encloses.
     * @param gui The event handler.
     * @param texture The texture to display in the rectangle.
     * @param width The width of side in local coordinates.
     * @param height The height of side in local coordinates.
     */
    public FrameTexRect (String name, WindowView view, /*TODO Gui2D*/ Object gui, Texture texture, 
			 float width, float height) {
        super(name, view, gui, width, height);
	this.texture = texture;
	try {
	    update();
        } catch (InstantiationException ex) {
            Gui2DFactoryDefault.logger.warning("Cannot update FrameTexRect component");
        }
	setTexture(texture);
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup () {
        super.cleanup();
	texture = null;
    }

    /**
     * {@inheritDoc}
     */
    public void update () throws InstantiationException {
	updateLayout();

	if (quad == null) {
	    // Init GUI only once
	    if (gui != null) {
		// TODO: gui.initEventHandling(this);
	    }
	} else {
	    detachChild(quad);
	}

	// Create state
	quad = new TexturedQuad(texture, "FrameTexRect-Quad", width, height);
	attachChild(quad);

	// This should be the same as FrameComponent.update
	updateColor();
    }

    /**
     * Specify a new texture for this object.
     */
    public void setTexture (Texture texture) {
	TextureState ts = (TextureState) quad.getRenderState(RenderState.RS_TEXTURE);
	if (ts == null) {
	    ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    quad.setRenderState(ts);
	}
	ts.setTexture(texture);
    }

    /**
     * Returns the texture of this component.
     */
    public Texture getTexture () {
	return texture;
    }

    /**
     * For debug: Print the contents of this component's render state.
     */
    public void printRenderState () {
	super.printRenderState();
	TextureState ts = (TextureState) quad.getRenderState(RenderState.RS_TEXTURE);
	GraphicsUtils.printRenderState(ts);
    }

    /**
     * For debug: Print the contents of this component's geometry
     */
    public void printGeometry () {
	super.printGeometry();
	GraphicsUtils.printGeometry(quad, true);
    }    
}