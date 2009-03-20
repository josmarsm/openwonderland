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
package org.jdesktop.wonderland.modules.swingtest.client;

import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import com.jme.math.Vector2f;
import com.jme.scene.Node;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.utils.graphics.GraphicsUtils;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DEntity;
import org.jdesktop.wonderland.modules.swingtest.client.cell.SwingTestCell;
import com.jme.math.Vector3f;

/**
 *
 * The window for the Swing test.
 *
 * @author deronj
 */
@ExperimentalAPI
public class SwingTestWindow 
    extends WindowSwing  
    implements TestPanel.Container 
{
    /** The logger used by this class. */
    private static final Logger logger = Logger.getLogger(SwingTestWindow.class.getName());

    /** The cell in which this window is displayed. */
    private SwingTestCell cell;

    /**
     * Create a new instance of SwingTestWindow.
     *
     * @param cell The cell in which this window is displayed.
     * @param app The app which owns the window.
     * @param width The width of the window (in pixels).
     * @param height The height of the window (in pixels).
     * @param topLevel Whether the window is top-level (e.g. is decorated) with a frame.
     * @param pixelScale The size of the window pixels.
     */
    public SwingTestWindow (SwingTestCell cell, App2D app, int width, int height, boolean topLevel, 
                            Vector2f pixelScale)
        throws InstantiationException
    {
	super(app, width, height, topLevel, pixelScale);
        this.cell = cell;

	setTitle("Swing Test");

	TestPanel testPanel = new TestPanel();
	// Note: this seems to only be required for the swing set, but do it here for safety
	// TODO: test without
       	JmeClientMain.getFrame().getCanvas3DPanel().add(testPanel);

        testPanel.setContainer(this);

	setComponent(testPanel);
        setTitle("Swing Test");

        /* Test Force a the preferred size
        System.err.println("test panel size = " + width + ", " + height);
        setSize(width, height);
        */
    }

    public void setOrtho(boolean ortho) {
        View2DEntity view = (View2DEntity) getView(cell);

        view.setOrtho(ortho, false);
        view.setTranslationUser(new Vector3f(300f, 300f, 0f), false);

        // Test (TODO: use app size for now)
        //view.setSizeApp(500, 200, false);

        view.update();

        /* TODO: remove
        Entity entity = view.getEntity();
        System.err.println("%%%%%%%%%% view entity = " + entity);
        RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
        System.err.println("%%%%%%%%%% rc = " + rc);
        Node attachPoint = rc.getAttachPoint();
        System.err.println("%%%%%%%%%% attachPoint = " + attachPoint);
        Node sceneRoot = rc.getSceneRoot();
        System.err.println("%%%%%%%%%% sceneRoot = " + sceneRoot);
        boolean o = rc.getOrtho();
        System.err.println("%%%%%%%%%% ortho = " + o);
        */        
    }

    /* TODO: for testing view.setVisible hack: uncomment the following
    public void setVisibleHack (boolean visible) {
        Window2DViewWorld view = getPrimaryView();
        System.err.println("************** Set view visible = " + visible);
        view.setVisible(visible);
    }
    */
}
