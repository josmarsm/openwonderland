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
package org.jdesktop.wonderland.modules.appbase.client.view;

import org.jdesktop.mtgame.Entity;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.awt.Point;
import com.jme.scene.state.TextureState;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.input.EventListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseWheelEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2DInterior;
import org.jdesktop.wonderland.modules.appbase.client.view.GeometryNode;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D.Type;
import java.awt.Button;

/**
 * TODO
 * Each view has entity -> viewNode -> geometryNode -> Geometry
 * @author dj
 */
@ExperimentalAPI
public abstract class View2DEntity implements View2D {

    private static final Logger logger = Logger.getLogger(View2DEntity.class.getName());

    private static float PIXEL_SCALE_DEFAULT = 0.01f;

    private static enum AttachState { DETACHED, ATTACHED_TO_ENTITY, ATTACHED_TO_WORLD };

    // Category changed flags
    protected static final int CHANGED_TOPOLOGY               = 0x80000000;
    protected static final int CHANGED_SIZE                   = 0x40000000;
    protected static final int CHANGED_OFFSET_STACK_TRANSFORM = 0x20000000;
    protected static final int CHANGED_USER_TRANSFORM         = 0x10000000;
    protected static final int CHANGED_TEX_COORDS             = 0x08000000;
    protected static final int CHANGED_FRAME                  = 0x04000000;
    protected static final int CATEGORY_CHANGE_MASK           = 0xfc000000;
    // Category group flags
    protected static final int CHANGED_TRANSFORMS             = CHANGED_OFFSET_STACK_TRANSFORM |
            	                                                CHANGED_USER_TRANSFORM;

    // Attribute changed flags (these include various categories which depend on them)
    protected static final int CHANGED_TEXTURE          = 0x800  | CHANGED_TOPOLOGY;
    protected static final int CHANGED_TYPE             = 0x1    | CHANGED_TOPOLOGY | CHANGED_TRANSFORMS;
    protected static final int CHANGED_PARENT           = 0x2    | CHANGED_TOPOLOGY
                                                                 | CHANGED_OFFSET_STACK_TRANSFORM;
    protected static final int CHANGED_VISIBLE          = 0x4    | CHANGED_TOPOLOGY;
    protected static final int CHANGED_DECORATED        = 0x8    | CHANGED_FRAME | CHANGED_SIZE;
    protected static final int CHANGED_GEOMETRY         = 0x10   | CHANGED_TOPOLOGY | CHANGED_TEXTURE;
    protected static final int CHANGED_SIZE_APP         = 0x20   | CHANGED_SIZE | CHANGED_TEXTURE |
                                                                   CHANGED_TEX_COORDS; 
    protected static final int CHANGED_PIXEL_SCALE      = 0x40   | CHANGED_SIZE
                                                                 | CHANGED_OFFSET_STACK_TRANSFORM;
    protected static final int CHANGED_OFFSET           = 0x80   | CHANGED_OFFSET_STACK_TRANSFORM;
    protected static final int CHANGED_USER_TRANSLATION = 0x100  | CHANGED_USER_TRANSFORM;
    protected static final int CHANGED_TITLE            = 0x200  | CHANGED_FRAME;
    protected static final int CHANGED_Z_ORDER          = 0x400  | CHANGED_OFFSET_STACK_TRANSFORM;
    protected static final int CHANGED_ORTHO            = 0x800;

    protected static final int CHANGED_ALL = -1;

    /** The app to which the view belongs. */
    private App2D app;

    /** The name of the view. */
    protected String name;

    /** The entity of this view. */
    protected Entity entity;

    /** The parent entity to which this entity is connected. */
    private Entity parentEntity;

    /** The base node of the view. */
    protected Node viewNode;

    /** The textured 3D object in which displays the view contents. */
    protected GeometryNode geometryNode;

    /** The new geometry object to be used. */
    protected GeometryNode newGeometryNode;

    /** Whether we have created the geometry node ourselves. */
    private boolean geometrySelfCreated;

    /** The control arbitrator of the window being displayed. */
    private ControlArb controlArb;

    /** The view's window. */
    protected Window2D window;

    /** The type of this view. */
    protected Type type = Type.UNKNOWN;

    /** The parent view of this view. */
    protected View2DEntity parent;

    /** Whether the app wants the view to be visible. */
    private boolean visibleApp;

    /** Whether the user wants the view to be visible. */
    private boolean visibleUser;

    /** Whether this view should be decorated by a frame. */
    private boolean decorated;

    /** The frame title. */
    protected String title;

    /** The Z (stacking) order of the view */
    protected int zOrder;

    /** The size of the view specified by the app. */
    private Dimension sizeApp = new Dimension(1, 1);

    /** The size of the displayed pixels in local units. */
    private Vector2f pixelScale;

    /** The interactive GUI object for this view. */
    private Gui2DInterior gui;

    /** The pixel offset translation from the top left corner of the parent. */
    private Point offset = new Point(0, 0);

    /** The user translation. */
    private Vector3f userTranslation = new Vector3f(0f, 0f, 0f);

    /** The previous user translation. */
    private Vector3f userTranslationPrev = new Vector3f(0f, 0f, 0f);

    /** A copy of the user transformation (i.e. the view node's tranformation). */
    private CellTransform userTransform = new CellTransform(null, null, null);

    /** The event listeners which are attached to this view while the view is attached to its cell */
    private LinkedList<EventListener> eventListeners = new LinkedList<EventListener>();

    /** The view's which are children of this view. */
    private LinkedList<View2DEntity> children = new LinkedList<View2DEntity>();

    /* The set of changes which have occurred since the last update. */
    protected int changeMask;

    /** Whether the view entity is to be displayed in ortho mode ("on the glass"). */
    private boolean ortho;
    
    /** Whether the view entity is attached and to what. */
    private AttachState attachState = AttachState.DETACHED;

    /** A dummy AWT component (used by deliverEvent). */
    private static Button dummyButton = new Button();

    /*
     ** TODO: WORKAROUND FOR A WONDERLAND PICKER PROBLEM:
     ** TODO: >>>>>>>> Is this obsolete in 0.5?
     **
     ** We cannot rely on the x and y values in the intersection info coming from LG
     ** for mouse release events.
     ** The problem is both the same for the X11 and AWT pickers. The LG pickers are
     ** currently defined to set the node info of all events interior to and terminating
     ** a grab to the node info of the button press which started the grab. Not only is
     ** the destination node set (as is proper) but also the x and y intersection info
     ** (which is dubious and, I believe, improper). Note that there is a hack in both
     ** pickers to work around this problem for drag events, which was all LG cared about
     ** at the time. I don't want to perturb the semantics of the LG pickers at this time,
     ** but in the future the intersection info must be dealt with correctly for all
     ** events interior to and terminating a grab. Note that this problem doesn't happen
     ** with button presses, because these start grabs.
     **
     ** For now I'm simply going to treat the intersection info in button release events
     ** as garbage and supply the proper values by tracking the pointer position myself.
     */
    private boolean pointerMoveSeen = false;
    private int pointerLastX;
    private int pointerLastY;

    /**
     * Create an instance of View2DEntity with default geometry node.
     * @param The entity in which the view is displayed.
     * @param window The window displayed in this view.
     */
    public View2DEntity (Window2D window) {
        this(window, null);
    }

    /**
     * Create an instance of View2DEntity with a specified geometry node.
     * @param window The window displayed in this view.
     * @param geometryNode The geometry node on which to display the view.
     *
     * NOTE: the subclass must force a complete update after calling this constructor, as follows:
     *
     * changeMask = CHANGED_ALL;
     * update(); 
     */
    public View2DEntity (Window2D window, GeometryNode geometryNode) {
        this.window = window;
        this.newGeometryNode = geometryNode;

        name = "View for " + window.getName();

        // Create entity and node
        entity = new Entity("Entity for " + name);
        viewNode = new Node("Node for " + name);
        RenderComponent rc =
            ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(viewNode);
        entity.addComponent(RenderComponent.class, rc);
        entityMakePickable(entity);

        // Create input-related objects 
        gui = new Gui2DInterior(this);
        gui.attachEventListeners(entity);
        controlArb = getWindow().getApp().getControlArb();
    }

    /** {@inheritDoc} */
    public synchronized void cleanup () {

        /* TODO: attach state, use sgchanges */

        if (gui != null) {
            gui.detachEventListeners(entity);
            gui.cleanup();
            gui = null;
        }

        if (parentEntity != null) {
            parentEntity.removeEntity(entity);
            parentEntity = null;
        }
        entity = null;

        if (geometryNode != null) {
            viewNode.detachChild(geometryNode);
            if (geometrySelfCreated) {
                
                geometryNode.cleanup();
                geometrySelfCreated = false;
            }
            geometryNode = null;
            newGeometryNode = null;
        }

        // TODO: detach this from parent view
        parent = null;
        children.clear();

        viewNode = null;
        controlArb = null;
        window = null;
        app = null;
    }

    /** {@inheritDoc} */
    public abstract View2DDisplayer getDisplayer ();

    /** Return this view's entity. */
    public synchronized Entity getEntity () {
        return entity;
    }

    /** Return this view's root node. */
    public synchronized Node getNode () {
        return viewNode;
    }

    /** {@inheritDoc} */
    public String getName () {
        return name;
    }

    /** {@inheritDoc} */
    public synchronized Window2D getWindow () {
        return window;
    }

    /** {@inheritDoc} */
    public synchronized void setType (Type type) {
        setType(type, true);
    }

    /** {@inheritDoc} */
    public synchronized void setType (Type type, boolean update) {
        if (this.type == type) return;

        // Validate type argument
        if (this.type == Type.UNKNOWN) {
            // All new types are permitted
        } else if (this.type == Type.SECONDARY && type == Type.PRIMARY) {
            // A promotion of a secondary to a primary is permitted.
        } else {
            // No other type changes are permitted.
            logger.severe("Old view type = " + this.type);
            logger.severe("New view type = " + type);
            throw new RuntimeException("Invalid type change.");
        }
            
        logger.info("change type = " + type);

        this.type = type;
        changeMask |= CHANGED_TYPE;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized Type getType () {
        return type;
    }

    /** 
     * {@inheritDoc}
     * <br><br>
     * This also has the side effect of setting the ortho attribute of this
     * view (and all of its children) to the ortho attribute of the parent.
     */
    public synchronized void setParent (View2D parent) {
        setParent(parent, true);
    }

    /** 
     * {@inheritDoc}
     * <br><br>
     * This also has the side effect of setting the ortho attribute of this
     * view (and all of its children) to the ortho attribute of the parent.
     */
    public synchronized void setParent (View2D parent, boolean update) {
        if (this.parent == parent) return;

        // Detach this view from previous parent
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        logger.info("change parent of view " + this + " to new parent view = " + parent);
        Thread.dumpStack();

        this.parent = (View2DEntity) parent;

        // Attach view to new parent
        if (this.parent != null) {
            this.parent.children.add(this);
        }

        changeMask |= CHANGED_PARENT;
        if (update) {
            update();
        }

        // Inherit ortho state of parent (must do this after self update)
        if (this.parent != null) {
            // Update immediately
            setOrtho(this.parent.isOrtho());
        }
    }

    /** {@inheritDoc} */
    public synchronized View2D getParent () {
        return parent;
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleApp (boolean visible) {
        setVisibleApp(visible, true);
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleApp (boolean visible, boolean update) {
        logger.info("change visibleApp = " + visible);
        visibleApp = visible;

        // TODO: HACK
        visibleUser = visible;

        changeMask |= CHANGED_VISIBLE;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized boolean isVisibleApp () {
        return visibleApp;
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleUser (boolean visible) {
        setVisibleUser(visible, true);
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleUser (boolean visible, boolean update) {
        logger.info("change visibleUser = " + visible);
        visibleUser = visible;
        changeMask |= CHANGED_VISIBLE;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized boolean isVisibleUser () {
        return visibleUser;
    }

    /** Recalculates the visibility of this view. */
    private synchronized void updateVisibility () {
        changeMask |= CHANGED_VISIBLE;
        update();
    }

    /** {@inheritDoc} */
    public synchronized boolean isActuallyVisible () {
        logger.info("Check actually visible for view " + this);
        logger.info("visibleApp = " + visibleApp);
        logger.info("visibleUser = " + visibleUser);
        if (!visibleApp || !visibleUser) return false;
        /* TODO: do we really want to check parent visibility? It doesn't work out for gt
           and isn't necessary if we always parent child entities to parent entities.
        if (parent == null) {
            logger.info("No parent. isActuallyVisible = true");
        */
            return true;
            /*
        } else {
            logger.info("parent = " + parent.getName());
            logger.info("parent.isActuallyVisible() = " + parent.isActuallyVisible());
            return parent.isActuallyVisible();
        }
            */
    }

    /** {@inheritDoc} */
    public synchronized void setDecorated (boolean decorated) {
        setDecorated(decorated, true);
    }

    /** {@inheritDoc} */
    public synchronized void setDecorated (boolean decorated, boolean update) {
        logger.info("change decorated = " + decorated);
        this.decorated = decorated;
        changeMask |= CHANGED_DECORATED;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized boolean isDecorated () {
        return decorated;
    }

    /** {@inheritDoc} */
    public synchronized void setTitle (String title) {
        setTitle(title, true);
    }

    /** {@inheritDoc} */
    public synchronized void setTitle (String title, boolean update) {
        logger.info("change title = " + title);
        this.title = title;
        changeMask |= CHANGED_TITLE;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized String getTitle () {
        return title;
    }

    /** {@inheritDoc} */
    public synchronized void setZOrder(int zOrder) {
        setZOrder(zOrder, true);
    }

    /** {@inheritDoc} */
    public synchronized void setZOrder(int zOrder, boolean update) {
        logger.info("change zOrder = " + zOrder);
        this.zOrder = zOrder;
        changeMask |= CHANGED_Z_ORDER;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized int getZOrder () {
        return zOrder;
    }

    /** {@inheritDoc} */
    public synchronized void setGeometryNode (GeometryNode geometryNode) {
        setGeometryNode(geometryNode, true);
    }

    /** {@inheritDoc} */
    public synchronized void setGeometryNode (GeometryNode geometryNode, boolean update) {
        logger.info("change geometryNode = " + geometryNode);
        newGeometryNode = geometryNode;
        changeMask |= CHANGED_GEOMETRY;
        if (update) {
            update();
        }
    }
    
    /** {@inheritDoc} */
    public synchronized GeometryNode getGeometryNode () {
        return geometryNode;
    }

    /** {@inheritDoc} */
    public synchronized void setSizeApp (Dimension size) {
        setSizeApp(size, true);
    }

    /** {@inheritDoc} */
    public synchronized void setSizeApp (Dimension size, boolean update) {
        logger.info("change sizeApp = " + sizeApp);

        sizeApp = (Dimension) size.clone();

        // Note: AWT doesn't like zero image sizes
        size.width = (size.width <= 0) ? 1 : size.width;
        size.height = (size.height <= 0) ? 1 : size.height;

        changeMask |= CHANGED_SIZE_APP | CHANGED_TEXTURE;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized Dimension getSizeApp () {
        return (Dimension) sizeApp.clone();
    }

    /** {@inheritDoc} */
    public synchronized float getDisplayerLocalWidth () {
        // TODO: ignore size mode and user size for now - always track window size as specified by app
        return getPixelScaleX() * sizeApp.width;
    }

    /** {@inheritDoc} */
    public synchronized float getDisplayerLocalHeight () {
        // TODO: ignore size mode and user size for now - always track window size as specified by app
        return getPixelScaleY() * sizeApp.height;
    }

    /** {@inheritDoc} */
    public synchronized void setPixelScale (Vector2f pixelScale) {
        setPixelScale(pixelScale, true);
    }

    /** {@inheritDoc} */
    public synchronized void setPixelScale (Vector2f pixelScale, boolean update) {
        logger.info("change pixelScale = " + pixelScale);
        this.pixelScale = pixelScale.clone();
        changeMask |= CHANGED_PIXEL_SCALE;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized Vector2f getPixelScale () {
        if (pixelScale == null) {
            return new Vector2f(PIXEL_SCALE_DEFAULT, PIXEL_SCALE_DEFAULT);
        } else {
            return pixelScale.clone();
        }
    }

    /** Returns the X pixel scale. */
    private synchronized float getPixelScaleX () {
        if (pixelScale == null) {
            return PIXEL_SCALE_DEFAULT;
        } else {
            return pixelScale.x;
        }        
    }

    /** Returns the Y pixel scale. */
    private synchronized float getPixelScaleY () {
        if (pixelScale == null) {
            return PIXEL_SCALE_DEFAULT;
        } else {
            return pixelScale.y;
        }        
    }

    /** {@inheritDoc} */
    public synchronized void setOffset(Point offset) {
        setOffset(offset, true);
    }

    /** {@inheritDoc} */
    public synchronized void setOffset(Point offset, boolean update) {
        logger.info("change offset = " + offset);
        this.offset = (Point) offset.clone();
        changeMask |= CHANGED_OFFSET;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized Point getOffset () {
        return (Point) offset.clone();
    }

    /** {@inheritDoc} */
    public synchronized void setTranslationUser (Vector3f translation) {
        setTranslationUser(translation, true);
    }

    /** {@inheritDoc} */
    public synchronized void setTranslationUser (Vector3f translation, boolean update) {
        logger.info("change translationUser = " + translation);
        userTranslationPrev = userTranslation;
        userTranslation = translation.clone();
        changeMask |= CHANGED_USER_TRANSLATION;
        if (update) {
            update();
        }
    }

    /** {@inheritDoc} */
    public synchronized Vector3f getTranslationUser () {
        return userTranslation.clone();
    }

    /** TODO: these are now deltas! */

    public synchronized void userMovePlanarStart (float dx, float dy) {
    }

    public synchronized void userMovePlanarUpdate (float x, float dy) {
    }

    public synchronized void userMovePlanarFinish () {
    }

    public synchronized void userMoveZStart (float dy) {
    }

    public synchronized void userMoveZUpdate (float dy) {
    }

    public synchronized void userMoveZFinish () {
    }

    /**
     * Specifies whether the view entity is to be displayed in ortho mode ("on the glass").
     * Update immediately. In addition, the ortho attribute of all descendents is set to the
     * same value. And, when ortho is true, the decorated attribute is ignored and is regarded
     * as false.
     */
    public synchronized void setOrtho (boolean ortho) {
        setOrtho(ortho, true);
    }

    /**
     * Specifies whether the view entity is to be displayed in ortho mode ("on the glass").
     * Update if specified.
     */
    public synchronized void setOrtho (boolean ortho, boolean update) {
        this.ortho = ortho;
        changeMask |= CHANGED_ORTHO;
        if (update) {
            update();
        }
    }

    /**
     * Returns whether the view entity is in ortho mode.
     */
    public synchronized boolean isOrtho () {
        return ortho;
    }

    /** Processes attribute changes. Should be called within a synchronized block. */
    protected void processChanges () {
        // Note: all of the scene graph changes are queued up and executed at the end

        // React to topology related changes
        if ((changeMask & (CHANGED_TOPOLOGY | CHANGED_ORTHO)) != 0) {
            logger.fine("Update topology");
            int chgMask = changeMask & ~CATEGORY_CHANGE_MASK;
            
            // First, detach entity (if necessary)
            switch (attachState) {
            case ATTACHED_TO_ENTITY:
                if (parentEntity != null) {
                    logger.fine("Remove entity " + entity + " from parent entity " + parentEntity);
                    RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
                    rc.setAttachPoint(null);
                    parentEntity.removeEntity(entity);
                    parentEntity = null;
                    System.err.println("############ Detached from entity");
                }
                break;
            case ATTACHED_TO_WORLD:
                logger.fine("Remove entity " + entity + " from world manager.");
                ClientContextJME.getWorldManager().removeEntity(entity);
                System.err.println("############ Detached from entity");
                break;
            }
            attachState = AttachState.DETACHED;

            // Does the geometry node itself need to change?
            if ((chgMask & CHANGED_GEOMETRY) != 0) {
                if (geometryNode != null) {
                    // Note: don't need to do in RenderUpdater because we've detached our entity
                    sgChangeGeometryDetachFromView(viewNode, geometryNode);
                    if (geometrySelfCreated) {
                        geometryNode.cleanup();
                        geometrySelfCreated = false;
                    }
                }
                if (newGeometryNode != null) {
                    geometryNode = newGeometryNode;
                    newGeometryNode = null;
                } else {
                    geometryNode = new GeometryNodeQuad(this);
                    geometrySelfCreated = true;
                }
                // Note: don't need to do in RenderUpdater because we've detached our entity
                sgChangeGeometryAttachToView(viewNode, geometryNode);
            }

            // Uses: window
            if ((chgMask & CHANGED_TEXTURE) != 0) {
                logger.fine("Update texture");
                if (geometryNode != null) {
                    geometryNode.setTexture(getWindow().getTexture());
                }
            }

            // Now reattach geometry if view should be visible
            // Uses: visible, ortho
            System.err.println("isActuallyVisible() = " + isActuallyVisible());
            if (isActuallyVisible()) {
                if (ortho) {
                    logger.fine("Attach entity " + entity + " to world manager.");
                    ClientContextJME.getWorldManager().addEntity(entity);
                    attachState = AttachState.ATTACHED_TO_WORLD;
                    System.err.println("############ Attached to world");
                } else {
                    parentEntity = getParentEntity();
                    if (parentEntity != null) {
                        logger.fine("Attach entity " + entity + " to parent entity " + parentEntity);
                        parentEntity.addEntity(entity);
                        RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
                        RenderComponent rcParent = (RenderComponent) parentEntity.getComponent(RenderComponent.class);
                        rc.setAttachPoint(rcParent.getSceneRoot());
                        attachState = AttachState.ATTACHED_TO_ENTITY;
                    }
                }
            }

            if ((chgMask & CHANGED_ORTHO) != 0) {
                // Propagate our ortho setting to our children
                logger.fine("Force children ortho to " + ortho);
                for (View2DEntity child : children) {
                    child.setOrtho(ortho);
                }
            }

            if ((chgMask & CHANGED_VISIBLE) != 0) {
                // Update visibility of children
                logger.fine("Update children visibility");
                for (View2DEntity child : children) {
                    child.updateVisibility();
                }
            }            
        }

        // React to frame changes (must do before handling size changes)
        if ((changeMask & (CHANGED_FRAME | CHANGED_ORTHO)) != 0) {
            logger.fine("Update frame");
            int chgMask = changeMask & ~CATEGORY_CHANGE_MASK;

            if ((chgMask & (CHANGED_DECORATED | CHANGED_ORTHO)) != 0) {
                if (decorated && !ortho) {
                    if (!hasFrame()) {
                        logger.fine("Attach frame");
                        attachFrame();
                        System.err.println("############ Attached frame");
                    }
                } else {
                    if (hasFrame()) {
                        logger.fine("Detach frame");
                        detachFrame();
                        System.err.println("############ Detached frame");
                    }
                }
            }
            
            if ((chgMask & CHANGED_TITLE) != 0) {
                logger.fine("Update title");
                frameUpdateTitle();
            }
        }            

        if ((changeMask & CHANGED_Z_ORDER) != 0) {
            logger.fine("Update geometry ortho Z order");
            sgChangeGeometryOrthoZOrderSet(geometryNode, zOrder);
        }

        if ((changeMask & CHANGED_ORTHO) != 0) {
            logger.fine("Update ortho, ortho = " + ortho);
            logger.severe("entity = " + entity);
            logger.severe("entity.getComponent(RenderComponent.class) = " + entity.getComponent(RenderComponent.class));
            entity.getComponent(RenderComponent.class).setOrtho(ortho);
            System.err.println("############ Entity ortho set to " + ortho);
            sgChangeViewNodeOrthoSet(viewNode, ortho);
        }

        // React to size related changes (must be done before handling transform changes)
        /// TODO       if ((changeMask & CHANGED_SIZE) != 0) {
        if ((changeMask & (CHANGED_SIZE | CHANGED_ORTHO)) != 0) {
            float width = getDisplayerLocalWidth();
            float height = getDisplayerLocalHeight();
            // TODO
            if (ortho) {
                /*
                width = 500f;
                height = 200f;
                */
                width = 10f;
                height = 10f;
            }
            sgChangeGeometrySizeSet(geometryNode, width, height);
        }

        // React to texture coordinate changes
        // Uses: window, texture
        if ((changeMask & CHANGED_TEX_COORDS) != 0) {
            // TODO: for now, texcoords only depend on app size
            float width = (float) sizeApp.width;
            float height = (float) sizeApp.height;
            Image image = getWindow().getTexture().getImage();
            float widthRatio = width / image.getWidth();
            float heightRatio = height / image.getHeight();
            sgChangeGeometryTexCoordsSet(geometryNode, widthRatio, heightRatio);
        }

        // React to transform related changes

        // Uses: type, parent, pixelscale, size, offset
        if ((changeMask & (CHANGED_OFFSET_STACK_TRANSFORM | CHANGED_ORTHO | CHANGED_Z_ORDER)) != 0) {
            CellTransform transform = null;

            switch (type) {
            case UNKNOWN:
            case PRIMARY:
                // Always set to identity
                transform = new CellTransform(null, null, null);
                break;
            case SECONDARY:
            case POPUP:
                // Uses: type, parent, pixelScale, size, offset
                transform = calcOffsetStackTransform();
            }
            sgChangeGeometryTransformOffsetStackSet(geometryNode, transform);
        }

        // Uses: type, userTranslation
        //TODO: if ((changeMask & CHANGED_USER_TRANSFORM) != 0) {
        if ((changeMask & (CHANGED_USER_TRANSFORM | CHANGED_ORTHO)) != 0) {
            CellTransform deltaTransform;

            switch (type) {
            case PRIMARY:
                deltaTransform = calcUserDeltaTransform();
                updatePrimaryTransform(deltaTransform);
                break;
            case SECONDARY:
                deltaTransform = calcUserDeltaTransform();
                System.err.println("################# PC: ut post mul");
                sgChangeTransformUserPostMultiply(viewNode, deltaTransform); 
                break;
            case UNKNOWN:
            case POPUP:
                // Always set to identity
                System.err.println("################# PC: ut set to identity");
                sgChangeTransformUserSet(viewNode, new CellTransform(null, null, null));
            }
        }

        sgProcessChanges();
        
        frameUpdate();
    }

    /** {@inheritDoc} */
    public synchronized void update () {
        processChanges();
        changeMask = 0;
    }

    /**
     * This usually should be overridden by the subclass.
     * Uses: type, parent.
     */
    protected Entity getParentEntity () {
        switch (type) {

        case UNKNOWN:
        case PRIMARY:
            return null;
        
        default:
            // Attach non-primaries to the entity of their parent
            if (parent == null) {
                return null;
            } else {
                return parent.getEntity();
            }
        }
    }                

    // Uses: type, parent, pixelscale, size, offset
    private CellTransform calcOffsetStackTransform () {
        CellTransform transform = new CellTransform(null, null, null);

        // Uses: parent, pixelScale, size, offset
        Vector3f offsetTranslation = calcOffsetTranslation();

        // Uses: type
        Vector3f stackTranslation = calcStackTranslation();

        offsetTranslation.addLocal(stackTranslation);
        transform.setTranslation(offsetTranslation);

        return transform;
    }

    // Uses: parent, pixelscale, size, offset
    // Convert the pixel-offset-from-upper-left of parent to a distance vector from the center of parent
    private Vector3f calcOffsetTranslation () {
        Vector3f translation = new Vector3f();
        if (parent == null) return translation;

        // TODO: does the width/height need to include the scroll bars?
        Dimension parentSize = parent.getSizeApp();
        translation.x = -parentSize.width * getPixelScaleX() / 2f;
        translation.y = parentSize.height * getPixelScaleY() / 2f;
        translation.x += sizeApp.width * getPixelScaleX() / 2f;
        translation.y -= sizeApp.height * getPixelScaleY() / 2f;
        translation.x += offset.x * getPixelScaleX();
        translation.y -= offset.y * getPixelScaleY();

        return translation;
    }

    protected Vector3f calcStackTranslation () {
        return new Vector3f(0f, 0f, 0f);
    }

    // Uses: userTranslation
    protected CellTransform calcUserDeltaTransform () {
        return calcUserTranslationDeltaTransform();
    }

    // Uses: userTranslation
    protected CellTransform calcUserTranslationDeltaTransform () {
        Vector3f deltaTranslation = userTranslation.subtract(userTranslationPrev);
        CellTransform transDeltaTransform = new CellTransform(null, null, null);
        transDeltaTransform.setTranslation(deltaTranslation);
        return transDeltaTransform;
    }

    protected void updatePrimaryTransform (CellTransform userDeltaTransform) {
    }

    private enum SGChangeOp { 
        GEOMETRY_ATTACH_TO_VIEW,
        GEOMETRY_DETACH_FROM_VIEW,
        GEOMETRY_SIZE_SET, 
        GEOMETRY_TEX_COORDS_SET,
        GEOMETRY_ORTHO_Z_ORDER_SET,
        VIEW_NODE_ORTHO_SET,
        GEOMETRY_TRANSFORM_OFFSET_STACK_SET,
        TRANSFORM_USER_POST_MULT,
        TRANSFORM_USER_SET,
    };

    private static class SGChange {
        private SGChangeOp op;
        private SGChange (SGChangeOp op) { 
            this.op = op; 
        }
        private SGChangeOp getOp () {
            return op;
        }
    }

    private static class SGChangeGeometryAttachToView extends SGChange {
        private Node viewNode;
        private GeometryNode geometryNode;
        private SGChangeGeometryAttachToView (Node viewNode, GeometryNode geometryNode) {
            super(SGChangeOp.GEOMETRY_ATTACH_TO_VIEW);
            this.viewNode = viewNode;
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeGeometryDetachFromView extends SGChange {
        private Node viewNode;
        private GeometryNode geometryNode;
        private SGChangeGeometryDetachFromView (Node viewNode, GeometryNode geometryNode) {
            super(SGChangeOp.GEOMETRY_DETACH_FROM_VIEW);
            this.viewNode = viewNode;
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeGeometrySizeSet extends SGChange {
        private GeometryNode geometryNode;
        private float width;
        private float height;
        private SGChangeGeometrySizeSet (GeometryNode geometryNode, float width, float height) {
            super(SGChangeOp.GEOMETRY_SIZE_SET);
            this.geometryNode = geometryNode;
            this.width = width;
            this.height = height;
        }
    }

    private static class SGChangeGeometryTexCoordsSet extends SGChange {
        private GeometryNode geometryNode;
        private float widthRatio;
        private float heightRatio;
        private SGChangeGeometryTexCoordsSet (GeometryNode geometryNode, 
                                              float widthRatio, float heightRatio) {
            super(SGChangeOp.GEOMETRY_TEX_COORDS_SET);
            this.geometryNode = geometryNode;
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
        }
    }

    private static class SGChangeGeometryOrthoZOrderSet extends SGChange {
        private GeometryNode geometryNode;
        private int zOrder;
        private SGChangeGeometryOrthoZOrderSet (GeometryNode geometryNode, int zOrder) {
            super(SGChangeOp.GEOMETRY_ORTHO_Z_ORDER_SET);
            this.geometryNode = geometryNode;
            this.zOrder = zOrder;
        }
    }

    private static class SGChangeViewNodeOrthoSet extends SGChange {
        private Node viewNode;
        private boolean ortho;
        private SGChangeViewNodeOrthoSet (Node viewNode, boolean ortho) {
            super(SGChangeOp.VIEW_NODE_ORTHO_SET);
            this.viewNode = viewNode;
            this.ortho = ortho;
        }
    }

    private static class SGChangeTransform extends SGChange {
        protected CellTransform transform;
        private SGChangeTransform (SGChangeOp op, CellTransform transform) {
            super(op);
            this.transform = transform;
        }
    }
    
    private static class SGChangeGeometryTransformOffsetStackSet extends SGChangeTransform {
        private GeometryNode geometryNode;
        private SGChangeGeometryTransformOffsetStackSet (GeometryNode geometryNode, CellTransform transform) {
            super(SGChangeOp.GEOMETRY_TRANSFORM_OFFSET_STACK_SET, transform);
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeTransformUserSet extends SGChangeTransform {
        private Node viewNode;
        private SGChangeTransformUserSet (Node viewNode, CellTransform transform) {
            super(SGChangeOp.TRANSFORM_USER_SET, transform);
            this.viewNode = viewNode;
       }
    }

    private static class SGChangeTransformUserPostMultiply extends SGChangeTransform {
        private Node viewNode;
        private SGChangeTransformUserPostMultiply (Node viewNode, CellTransform transform) {
            super(SGChangeOp.TRANSFORM_USER_POST_MULT, transform);
            this.viewNode = viewNode;
        }
    }

    // The list of scene graph changes (to be applied at the end of update).
    private LinkedList<SGChange> sgChanges = new LinkedList<SGChange>();

    private synchronized void sgChangeGeometryAttachToView(Node viewNode, GeometryNode geometryNode) {
        sgChanges.add(new SGChangeGeometryAttachToView(viewNode, geometryNode));
    }

    private synchronized void sgChangeGeometryDetachFromView(Node viewNode, GeometryNode geometryNode) {
        sgChanges.add(new SGChangeGeometryDetachFromView(viewNode, geometryNode));
    }

    private synchronized void sgChangeGeometrySizeSet(GeometryNode geometryNode, float width, float height) {
        sgChanges.add(new SGChangeGeometrySizeSet(geometryNode, width, height));
    }

    private synchronized void sgChangeGeometryTexCoordsSet(GeometryNode geometryNode, float widthRatio, 
                                                           float heightRatio) {
        sgChanges.add(new SGChangeGeometryTexCoordsSet(geometryNode, widthRatio, heightRatio));
    }

    private synchronized void sgChangeGeometryOrthoZOrderSet(GeometryNode geometryNode, int zOrder) {
        sgChanges.add(new SGChangeGeometryOrthoZOrderSet(geometryNode, zOrder));
    }

    private synchronized void sgChangeViewNodeOrthoSet(Node viewNode, boolean ortho) {
        sgChanges.add(new SGChangeViewNodeOrthoSet(viewNode, ortho));
    }

    private synchronized void sgChangeGeometryTransformOffsetStackSet (GeometryNode geometryNode,
                                                                       CellTransform transform) {
        sgChanges.add(new SGChangeGeometryTransformOffsetStackSet(geometryNode,transform));
    }

    private synchronized void sgChangeTransformUserPostMultiply (Node viewNode, 
                                                                 CellTransform deltaTransform) {
        sgChanges.add(new SGChangeTransformUserPostMultiply(viewNode, deltaTransform));
    }

    private synchronized void sgChangeTransformUserSet (Node viewNode, CellTransform transform) {
        sgChanges.add(new SGChangeTransformUserSet(viewNode, transform));
    }

    private synchronized void sgProcessChanges () {
        if (sgChanges.size() <= 0) return;

         ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
             public void update(Object arg0) {

                 for (SGChange sgChange : sgChanges) {
                     switch (sgChange.getOp()) {

                     case GEOMETRY_ATTACH_TO_VIEW: {
                         SGChangeGeometryAttachToView chg = (SGChangeGeometryAttachToView) sgChange;
                         chg.viewNode.attachChild(chg.geometryNode);
                         logger.fine("Attach geometryNode " + chg.geometryNode + " to viewNode " + 
                                     chg.viewNode);
                         break;
                     }

                     case GEOMETRY_DETACH_FROM_VIEW: {
                         SGChangeGeometryDetachFromView chg = (SGChangeGeometryDetachFromView) sgChange;
                         chg.viewNode.detachChild(chg.geometryNode);
                         logger.fine("Detach geometryNode " + chg.geometryNode + " from viewNode " + 
                                     chg.viewNode);
                         break;
                     }

                     case GEOMETRY_SIZE_SET: {
                         SGChangeGeometrySizeSet chg = (SGChangeGeometrySizeSet) sgChange;
                         chg.geometryNode.setSize(chg.width, chg.height);
                         forceTextureIdAssignment();
                         logger.fine("Geometry node setSize, wh = " + chg.width + ", " + chg.height);
                         System.err.println("############ setsize to wh = " + chg.width + ", " + chg.height);
                         break;
                     }

                     case GEOMETRY_TEX_COORDS_SET: {
                         SGChangeGeometryTexCoordsSet chg = (SGChangeGeometryTexCoordsSet) sgChange;
                         chg.geometryNode.setTexCoords(chg.widthRatio, chg.heightRatio);
                         logger.fine("Geometry node setSize, whRatio = " + chg.widthRatio + ", " + 
                                     chg.heightRatio);
                         break;
                     }

                     case GEOMETRY_ORTHO_Z_ORDER_SET: {
                         SGChangeGeometryOrthoZOrderSet chg = (SGChangeGeometryOrthoZOrderSet) sgChange;
                         if (chg.geometryNode != null) {
                             chg.geometryNode.setOrthoZOrder(chg.zOrder);
                         }
                         logger.fine("Geometry set ortho z order = " + chg.zOrder);
                         System.err.println("############ Geometry ortho z order set to " + chg.zOrder);
                         break;
                     }

                     case VIEW_NODE_ORTHO_SET: {
                         SGChangeViewNodeOrthoSet chg = (SGChangeViewNodeOrthoSet) sgChange;
                         if (chg.ortho) {
                             chg.viewNode.setCullHint(Spatial.CullHint.Never);
                             System.err.println("############ Cull hint set to " + Spatial.CullHint.Never);
                         } else {
                             chg.viewNode.setCullHint(Spatial.CullHint.Inherit);
                             System.err.println("############ Cull hint set to " + Spatial.CullHint.Inherit);
                         }
                         logger.fine("View node ortho cull hint set = " + chg.ortho);
                         break;
                     }

                     case GEOMETRY_TRANSFORM_OFFSET_STACK_SET: {
                         // The offset/stack transform resides in the geometry
                         SGChangeGeometryTransformOffsetStackSet chg =
                             (SGChangeGeometryTransformOffsetStackSet) sgChange;
                         chg.geometryNode.setTransform(chg.transform);
                         logger.fine("Geometry node set transform, transform = " + chg.transform);
                         break;
                     }

                     case TRANSFORM_USER_POST_MULT: {
                         SGChangeTransformUserPostMultiply chg = (SGChangeTransformUserPostMultiply) sgChange;
                         // TODO
                         if (ortho) {
                             System.err.println("############## set user transform to identity");
                             viewNode.setLocalRotation(new Quaternion());
                             viewNode.setLocalScale(1.0f);
                             viewNode.setLocalTranslation(new Vector3f(0f, 0f, 0f));
                         } else {
                         userTransform.mul(chg.transform);
                         Quaternion r = userTransform.getRotation(null);
                         chg.viewNode.setLocalRotation(r);
                         logger.fine("View node set rotation = " + r);
                         Vector3f t = userTransform.getTranslation(null);
                         chg.viewNode.setLocalTranslation(t);
                         logger.fine("View node set translation = " + t);
                         // TODO
                         }
                         break;                         
                     }

                     case TRANSFORM_USER_SET: {
                         SGChangeTransformUserSet chg = (SGChangeTransformUserSet) sgChange;
                         // TODO
                         if (ortho) {
                             System.err.println("############## set user transform to identity");
                             viewNode.setLocalRotation(new Quaternion());
                             viewNode.setLocalScale(1.0f);
                             viewNode.setLocalTranslation(new Vector3f(0f, 0f, 0f));
                         } else {
                         userTransform = chg.transform.clone(null);
                         Quaternion r = userTransform.getRotation(null);
                         chg.viewNode.setLocalRotation(r);
                         logger.fine("View node set rotation = " + r);
                         Vector3f t = userTransform.getTranslation(null);
                         chg.viewNode.setLocalTranslation(t);
                         logger.fine("View node set translation = " + t);
                         // TODO
                         }
                         break;
                     }
                     }
                 }


                 // Propagate changes to JME
                 ClientContextJME.getWorldManager().addToUpdateList(viewNode);


                 sgChanges.clear();
                 synchronized (sgChanges) {
                     sgChanges.notifyAll();
                 }
             }
         }, null);

         // Wait until all changes are performed
         synchronized (sgChanges) {
             while (sgChanges.size() > 0) {
                 try { sgChanges.wait(); } catch (InterruptedException ex) {}
             }
         }
    }

    /** {@inheritDoc} */
    public synchronized void deliverEvent(Window2D window, MouseEvent3D me3d) {
        /*
        System.err.println("********** me3d = " + me3d);
        System.err.println("********** awt event = " + me3d.getAwtEvent());
        PickDetails pickDetails = me3d.getPickDetails();
        System.err.println("********** pt = " + pickDetails.getPosition());
         */

        // No special processing is needed for wheel events. Just
        // send the 2D wheel event which is contained in the 3D event.
        if (me3d instanceof MouseWheelEvent3D) {
            controlArb.deliverEvent(window, (MouseEvent) me3d.getAwtEvent());
            return;
        }

        // Can't convert if there is no geometry
        if (geometryNode == null) {
            return;
        }

        // Convert mouse event intersection point to 2D. For most events this is the intersection
        // point based on the destination pick details calculated by the input system, but for drag
        // events this needs to be derived from the actual hit pick details (because for drag events
        // the destination pick details might be overridden by a grab).
        Point point;
        if (me3d.getID() == MouseEvent.MOUSE_DRAGGED) {
            MouseDraggedEvent3D de3d = (MouseDraggedEvent3D) me3d;
            point = geometryNode.calcWorldPositionInPixelCoordinates(de3d.getHitIntersectionPointWorld(), true);
        } else {
            point = geometryNode.calcWorldPositionInPixelCoordinates(me3d.getIntersectionPointWorld(), false);
        }
        if (point == null) {
            // Event was outside our panel so do nothing
            // This can happen for drag events
            return;
        }

        // Construct a corresponding 2D event
        MouseEvent me = (MouseEvent) me3d.getAwtEvent();
        int id = me.getID();
        long when = me.getWhen();
        int modifiers = me.getModifiers();
        int button = me.getButton();

        // TODO: WORKAROUND FOR A WONDERLAND PICKER PROBLEM:
        // See comment for pointerMoveSeen above
        if (id == MouseEvent.MOUSE_RELEASED && pointerMoveSeen) {
            point.x = pointerLastX;
            point.y = pointerLastY;
        }

        me = new MouseEvent(dummyButton, id, when, modifiers, point.x, point.y,
                0, false, button);

        // Send event to the window's control arbiter
        controlArb.deliverEvent(window, me);

        // TODO: WORKAROUND FOR A WONDERLAND PICKER PROBLEM:
        // See comment for pointerMoveSeen above
        if (id == MouseEvent.MOUSE_MOVED || id == MouseEvent.MOUSE_DRAGGED) {
            pointerMoveSeen = true;
            pointerLastX = point.x;
            pointerLastY = point.y;
        }
    }

    /** {@inheritDoc} */
    public Point calcPositionInPixelCoordinates(Vector3f point, boolean clamp) {
        if (geometryNode == null) {
            return null;
        }
        return geometryNode.calcWorldPositionInPixelCoordinates(point, clamp);
    }

    /** {@inheritDoc} */
    public Point calcIntersectionPixelOfEyeRay(int x, int y) {
        if (geometryNode == null) {
            return null;
        }
        return geometryNode.calcIntersectionPixelOfEyeRay(x, y);
    }

    /** {@inheritDoc} */
    public void forceTextureIdAssignment() {
        if (geometryNode == null) {
            setGeometryNode(null);
            if (geometryNode == null) {
                logger.severe("***** Cannot allocate geometry node for view!!");
                return;
            }
        }
        final TextureState ts = geometryNode.getTextureState();
        if (ts == null) {
            logger.warning("Trying to force texture id assignment while view texture state is null");
            return;
        }

        logger.fine("texid alloc: ts.getTexture() = " + ts.getTexture());

        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                // The JME magic - must be called from within the render loop
                ts.load();
            }
         }, null);
    }

    /** {@inheritDoc} */
    public synchronized void addEventListener(EventListener listener) {
        listener.addToEntity(entity);
    }

    /** {@inheritDoc} */
    public synchronized void removeEventListener(EventListener listener) {
        listener.removeFromEntity(entity);
    }

    /** {@inheritDoc} */
    public void addEntityComponent(Class clazz, EntityComponent comp) {
        entity.addComponent(clazz, comp);
        comp.setEntity(entity);
    }

    /** {@inheritDoc} */
    public void removeEntityComponent(Class clazz) {
        entity.removeComponent(clazz);
    }

    /**
     * Return whether this view has an attached frame.
     */
    protected boolean hasFrame () {
        return false;
    }

    /**
     * Attach a frame to this view. This can be overridden by the subclass so a 
     * subclass-specific frame can be attached.
     */
    protected void attachFrame () {
    }

    /**
     * Detach this view's frame from the view.
     */
    protected void detachFrame () {
    }

    /**
     * Update the frame's title.
     */
    protected void frameUpdateTitle () {
    }

    /**
     * Update the frame.
     */
    protected void frameUpdate () {
    }

    /** 
     * Make an entity pickable by attaching a collision component. Entity must already have
     * a render component and a scene root node.
     */
    public static void entityMakePickable (Entity entity) {
        JMECollisionSystem collisionSystem = (JMECollisionSystem) ClientContextJME.getWorldManager().
            getCollisionManager().loadCollisionSystem(JMECollisionSystem.class);
        RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
        CollisionComponent cc = collisionSystem.createCollisionComponent(rc.getSceneRoot());
        entity.addComponent(CollisionComponent.class, cc);
    }

    @Override
    public String toString () {
        return name;
    }
}
