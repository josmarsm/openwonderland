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

import com.jme.math.Vector2f;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.View2DCellFactory;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DSet;

/**
 * The generic 2D application superclass. All 2D apps in Wonderland have this
 * root class.
 * <br><br>
 * 2D apps provide a window stack in which to arrange their visible windows.
 * This stack is a list. The top window in the stack is first in the list.
 * The bottom window is the last in the list. 
 * <br><br>
 * The stack position index of the top window is N-1, where N is the number of windows. 
 * The stack position of the bottom window is 0.
 *
 * In order to display this app you must add a displayer to it. Once added, as windows of the
 * app become visible they will be displayed in the displayer.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class App2D {

    private static final Logger logger = Logger.getLogger(App2D.class.getName());

    /** The global default appbase View2DCell factory.*/
    private static View2DCellFactory view2DCellFactory;

    /** The window stack for this app */
    protected WindowStack stack = new WindowStack();
    /** The world size of pixels */
    protected Vector2f pixelScale;

    /** The set of all views of the windows of this app. */
    private View2DSet viewSet = new View2DSet();

    /** The primary window of this app. */
    private Window2D primaryWindow;

    /** 
     * This list contains the windows of the app which are visible. It also tracks the order in which 
     * windows are made visible. The head of the list is the least recently made visible window and 
     * the tail of the list is the most recently made visible window.
     */
    /* TODO: Not yet: Primary policy: least recently shown. 
    private LinkedList<Window2D> windowVisibleOrder = new LinkedList<Window2D>();
    */

    /** The list of all windows created by this app */
    protected LinkedList<Window2D> windows = new LinkedList<Window2D>();

    /** The control arbiter for this app. null means that all users can control the app at the same time */
    protected ControlArb controlArb;

    /** The focus entity of the app. */
    protected Entity focusEntity;

    /** The name of the app. */
    private String name;

    /**
     * Set the default View2DCell factory to be used for all apps in this client. (Called by 
     * AppClientPlugin.initialize).
     */
    static void setView2DCellFactory (View2DCellFactory vFactory) {
        view2DCellFactory = vFactory;
    }

    /** Returns the default View2DCell factory. */
    public static View2DCellFactory getView2DCellFactory () {
        return view2DCellFactory;
    }

    /**
     * Create a new instance of App2D with a default name..
     *
     * @param controlArb The control arbiter to use. null means that all users can control at the same time.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public App2D(ControlArb controlArb, Vector2f pixelScale) {
        this(null, controlArb, pixelScale);
    }

    /**
     * Create a new instance of App2D with the given name.
     *
     * @param name The name of the app.
     * @param controlArb The control arbiter to use. null means that all users can control at the same time.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public App2D(String name, ControlArb controlArb, Vector2f pixelScale) {
        this.name = name;
        this.controlArb = controlArb;
        this.pixelScale = pixelScale;
        focusEntity = new Entity("App focus entity for app " + getName());
    }

    /**
     * Deallocate resources.
     */
    public void cleanup() {
        if (controlArb != null) {
            controlArb.cleanup();
            controlArb = null;
        }
        if (stack != null) {
            stack.cleanup();
            stack = null;
        }
        for (Window2D window : windows) {
            window.cleanup();
        }
        windows.clear();
        pixelScale = null;
        viewSet.cleanup();
    }

    /** 
     * Returns the pixel scale 
     */
    public Vector2f getPixelScale() {
        return new Vector2f(pixelScale);
    }

    public void addDisplayer (View2DDisplayer displayer) {
        viewSet.add(displayer);
    }

    public void removeDisplayer (View2DDisplayer displayer) {
        viewSet.remove(displayer);
    }

    /**
     * Add a window to this app. It is added on top of the app's window stack.
     *
     * @param window The window to add.
     */
    public synchronized void addWindow(Window2D window) {
        windows.add(window);
        viewSet.add(window);
        stack.add(window);
    }

    /**
     * Add the given window to the stack so that it is above the given sibling.
     *
     * @param window The window to add.
     * @param sibling The window that is immediately above the added window.
     */
    public void addWindowSiblingAbove(Window2D window, Window2D sibling) {
        windows.add(window);
        viewSet.add(window);
        stack.addSiblingAbove(window, sibling);
    }

    /**
     * Remove the given window from the window stack.
     *
     * @param window The window to remove.
     */
    public void removeWindow(Window2D window) {
        windows.remove(window);
        stack.remove(window);
        viewSet.remove(window);
    }

    /**
     * Returns the number of windows in this view set.
     */
    public int getNumWindows () {
        return viewSet.getNumWindows();
    }

    /**
     * Specify the primary window.
     */
    public void setPrimaryWindow (Window2D window) {
        primaryWindow = window;
    }

    /**
     * Returns the primary window.
     */
    public Window2D getPrimaryWindow () {
        return primaryWindow;
    }

    /**
     * Move the given window to the front of the window stack.
     *
     * @param window The window to move.
     */
    public void windowToFront(Window2D window) {
        stack.toFront(window);
    }

    /** 
     * Return the top window of the window stack.
     */
    public Window2D windowGetTop() {
        return stack.getTop();
    }

    /** 
     * Return the bottom window of the window stack.
     */
    public Window2D windowGetBottom() {
        return stack.getBottom();
    }

    /**
     * Rearrange the window stack so that the windows are in the given order.
     *
     * @param order An array which indicates the order in which the windows
     * are to appear in the stack. The window at order[index] should have
     * stack position N-index, where N is the number of windows in the stack.
     */
    public void restackWindows(Window2D[] order) {
        stack.restack(order);
    }

    public void windowSetVisible (Window2D window, boolean visible) {
        /* TODO: Not yet: Primary policy: least recently shown. 
        TODO: if popup return;
        windowVisibleOrder.remove(window);
        if (visible) {
            windowVisibleOrder.addLast(window);
        }
        */
    }

    private Window2D windowLeastRecentlyMadeVisible () {
        // TODO: return windowVisibleOrder.getFirst();
        return null;
    }

    /**
     * Returns the focus entity of the app.
     */
    @InternalAPI
    public Entity getFocusEntity () {
        return focusEntity;
    }

    /**
     * Returns the Control Arbiter for this app.
     * If this is null the app supports fine-grained control swapping.
     * That is, the app accepts user events from different users equally
     * on a first-come first-served basis.
     */
    public ControlArb getControlArb() {
        return controlArb;
    }

    /**
     * Returns an iterator over all the windows of this app.
     */
    public Iterator<Window2D> getWindows() {
        return windows.iterator();
    }

    /**
     * Returns an iterator over all the displayers of this app.
     */
    public Iterator<View2DDisplayer> getDisplayers() {
        return viewSet.getDisplayers();
    }

    /**
     * Returns the name of the app.
     */
    public String getName () {
        if (name == null) {
            return "(Unnamed App)";
        } else {
            return name;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString () {
        return getName();
    }
}