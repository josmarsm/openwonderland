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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import java.awt.event.ComponentEvent;
import java.util.logging.Level;
import org.jdesktop.mtgame.Entity;
import com.jme.scene.CameraNode;
import com.jme.scene.GeometricUpdateListener;
import com.jme.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer.MoveProcessor;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.event.ComponentListener;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import javax.swing.JPanel;
import org.jdesktop.mtgame.BufferUpdater;
import org.jdesktop.mtgame.OnscreenRenderBuffer;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderBuffer;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * 
 * Manages the view into the 3D world. The JME Camera is internal to this
 * class, it is associated with a Cell and a CameraProcessor is defined which
 * specified how the camera tracks the cell. For example ThirdPersonCameraProcessor
 * makes the camera follow the origin of the cell from behind and above the origin
 * giving a third person view.
 * 
 * TODO currently the camera processor is hardcoded to ThirdPerson
 *
 * The system provides the concept of a primary ViewCell, when using Wonderland with
 * a set of federated servers the primary ViewCell is the one that is providing most
 * of the user interaction. For example the rendering of the avatar and audio in formation
 * will come from the primary ViewCell. The non primary ViewCells track the primary.
 *
 * The JME Camera and input controls will normally be attached to the View Cells (primary and others), however
 * the system does support attaching the camera and controls to any cell. This is useful in some
 * tool interfaces for positioning cells and visually checking things. It should be noted that
 * when the camera and controls are not attached to a ViewCell the client will have some limitations, for
 * example as the ViewCell is not moving on the server no cache updates will be sent to the client so
 * tools should be careful not to allow the users to move outside of the current cell set.
 *
 * @author paulby
 */
@ExperimentalAPI
public class ViewManager {
    private static final Logger logger =
            Logger.getLogger(ViewManager.class.getName());

    private static ViewManager viewManager=null;

    private CameraNode cameraNode;
    private CameraProcessor cameraProcessor = null;
    private CameraComponent cameraComponent = null;
    private CameraController cameraController = new ThirdPersonCameraProcessor();
            
    /**
     * The width and height of our 3D window
     */
    private int width;
    private int height;
    private float aspect;
    
    private Cell attachCell = null;
    private CellListener listener = null;
    private SimpleAvatarControls eventProcessor = null;
    
    private ArrayList<ViewManagerListener> viewListeners = new ArrayList();

    private HashMap<WonderlandSession, ViewCell> sessionViewCells = new HashMap();

    private ViewCell primaryViewCell = null;
    private ViewControls avatarControls = null;

    // TODO remove this
    public boolean useAvatars = false;

    private RenderBuffer rb;

    private HashSet<CameraListener> cameraListeners = null;

    ViewManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.aspect = (float)width/(float)height;

//        String avatarDetail = System.getProperty("avatar.detail", "high");
//        if (avatarDetail.equalsIgnoreCase("high") || avatarDetail.equalsIgnoreCase("medium"))
            useAvatars=true;
    }

    public static void initialize(int width, int height) {
        viewManager = new ViewManager(width, height);
    }
    
    public static ViewManager getViewManager() {
        if (viewManager==null)
            throw new RuntimeException("View has not been initialized");
        
        return viewManager;
    }

    void attachViewCanvas(JPanel panel) {
        rb = ClientContextJME.getWorldManager().getRenderManager().createRenderBuffer(RenderBuffer.Target.ONSCREEN, width, height);
        ClientContextJME.getWorldManager().getRenderManager().addRenderBuffer(rb);
        Canvas canvas = ((OnscreenRenderBuffer)rb).getCanvas();

        canvas.setVisible(true);
        canvas.setBounds(0, 0, width, height);

        panel.add(canvas, BorderLayout.CENTER);

        panel.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
//                System.err.println("Resizing "+e);
                int width = e.getComponent().getWidth();
                int height = e.getComponent().getHeight();
                cameraComponent.setViewport(width, height);
                cameraComponent.setAspectRatio(((float)width/(float)height));
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentShown(ComponentEvent e) {
            }

            public void componentHidden(ComponentEvent e) {
            }
        });

        final Semaphore waitForReady = new Semaphore(0);

        // Wait for the renderer to become ready
        rb.setBufferUpdater(new BufferUpdater() {
            public void init(RenderBuffer arg0) {
                logger.info("RENDERER IS READY !");
                waitForReady.release();
            }
        });
        
        try {
            waitForReady.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ViewManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        createCameraEntity(ClientContextJME.getWorldManager());
        listener = new CellListener();
    }

    Canvas getCanvas() {
        return ((OnscreenRenderBuffer)rb).getCanvas();
    }
    
    /**
     * Register a ViewCell for a session with the ViewManager
     * 
     * @param cell ViewCell to register
     */
    public void register(ViewCell cell) {
        sessionViewCells.put(cell.getCellCache().getSession(), cell);
    }

    /**
     * Deregister a ViewCell (usually called when a session is closed)
     * TODO  implement
     * 
     * @param cell ViewCell to deregister
     */
    public void deregister(ViewCell cell) {
        throw new RuntimeException("Not Implemented");
    }

    /**
     * Set the Primary ViewCell for this client. The primary ViewCell is the one
     * that is currently rendering the client avatar etc.
     * @param cell
     */
    public void setPrimaryViewCell(ViewCell cell) {
        attach(cell);
        ViewCell oldViewCell = primaryViewCell;
        primaryViewCell = cell;

        // TODO all non primary view cells should track the movements of the primary

        notifyViewManagerListeners(oldViewCell, primaryViewCell);
    }

    /**
     * Returns the primary view cell.
     * The session can be obtained from the cell, cell.getCellCache().getSession().
     *
     * The primaryViewCell may be null, especially during startup. Use the
     * ViewManagerListener to track changes to the primaryViewCell.
     *
     */
    public ViewCell getPrimaryViewCell() {
        return primaryViewCell;
    }
    
    /**
     * Attach the 3D view to the specified cell. Note the 3D view (camera and controls) are usually attached
     * to a ViewCell, however they can be attached to any cell in the system. This can be useful for
     * position cells etc, but note that the Primary ViewCell is not changed in that case so there may be some
     * interesting side effects. For example the ViewCell on the server is not being moved so the client will
     * not receive any cache updates.
     * @param cell
     */
    public void attach(Cell cell) {
        logger.fine("[ViewManager] attach " + cell + " current " + attachCell +
                    " controls " + avatarControls);

        // if there is already a view attached, detach it
        if (attachCell!=null) {
            detach();
        }
        
        if (avatarControls==null) {
            // This will need to be updated in the future. ViewControls can
            // only drive true avatars, if the Camera is being attached to
            // another type of cell then another control system will be
            // required.
            
            // Create the input listener and process to control the avatar
            if (useAvatars) {
                avatarControls = AvatarRenderManager.getAvatarRenderManager().createViewControls(cell.getCellCache().getSession().getSessionManager());
            } else {
                avatarControls = new SimpleAvatarControls(cell, ClientContextJME.getWorldManager());
            }

            avatarControls.attach((ViewCell) cell);
            // enable the controls
            avatarControls.setEnabled(true);
            

        }

        Entity entity = ((CellRendererJME)cell.getCellRenderer(RendererType.RENDERER_JME)).getEntity();

        CellRendererJME renderer = (CellRendererJME) cell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        avatarControls.setEnabled(true);

        // TODO move this into the SimpleAvatarControls
        if (renderer!=null) {
            if (renderer instanceof BasicRenderer) {
                BasicRenderer.MoveProcessor moveProc = (MoveProcessor) renderer.getEntity().getComponent(BasicRenderer.MoveProcessor.class);
                if (moveProc!=null) {
                    moveProc.addToChain(cameraProcessor);
                    avatarControls.addToChain(moveProc);
                }
            }
        }

        // Set initial camera position
        cameraProcessor.viewMoved(cell.getWorldTransform());
        
        entity.addComponent(ViewControls.class, avatarControls);
        attachCell = cell;
        attachCell.addTransformChangeListener(listener);
    }
    
    /**
     * Detach the 3D view from the cell it's currently attached to.
     */
    public void detach() {
        logger.fine("[ViewManager] detach current " + attachCell +
                    " controls " + avatarControls);

        if (attachCell==null) {
            Logger.getAnonymousLogger().warning("VIEW NOT ATTACHED TO A CELL (BUT CONTINUE ANYWAY)");
            return;
        }
        
        Entity entity = ((CellRendererJME)attachCell.getCellRenderer(RendererType.RENDERER_JME)).getEntity();
        entity.removeComponent(ViewControls.class);
        
        CellRendererJME renderer = (CellRendererJME) attachCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
        if (renderer!=null) {
//            if (renderer instanceof ViewControls.AvatarInputSelector) {
//                ((ViewControls.AvatarInputSelector)renderer).selectForInput(false);
//            }

            if (renderer instanceof BasicRenderer) {
                BasicRenderer.MoveProcessor moveProc = (MoveProcessor) renderer.getEntity().getComponent(BasicRenderer.MoveProcessor.class);
                if (moveProc!=null) {
                    moveProc.removeFromChain(cameraProcessor);
                    avatarControls.removeFromChain(moveProc);
                }
            }
        }

        entity.removeComponent(ViewControls.class);
        avatarControls.setEnabled(false);
        avatarControls = null;
        attachCell.removeTransformChangeListener(listener);
        attachCell = null;
    }
    
    /**
     * Set the controller for the camera processor
     */
    public void setCameraController(CameraController cameraController) {
        cameraProcessor.setCameraController(cameraController);
//        Entity entity = ((CellRendererJME)attachCell.getCellRenderer(RendererType.RENDERER_JME)).getEntity();
//
//        CellRendererJME renderer = (CellRendererJME) attachCell.getCellRenderer(Cell.RendererType.RENDERER_JME);
//
//            if (renderer instanceof BasicRenderer) {
//                BasicRenderer.MoveProcessor moveProc = (MoveProcessor) renderer.getEntity().getComponent(BasicRenderer.MoveProcessor.class);
//                if (moveProc!=null) {
//                    moveProc.addToChain(cameraProcessor);
//                }
//            }

        cameraProcessor.viewMoved(primaryViewCell.getWorldTransform());
    }

    /**
     * Return the current camera processor
     * @return
     */
    public CameraProcessor getCameraProcessor() {
        return cameraProcessor;
    }

    /**
     * Add a ViewManagerListener which will be notified of changes in the view system
     * @param listener to be added
     */
    public void addViewManagerListener(ViewManagerListener listener) {
        viewListeners.add(listener);
    }

    /**
     * Remove the specified ViewManagerListner.
     * @param listener
     */
    public void removeViewManagerListener(ViewManagerListener listener) {
        viewListeners.remove(listener);
    }

    private void notifyViewManagerListeners(ViewCell oldViewCell, ViewCell newViewCell) {
        for(ViewManagerListener vListener : viewListeners)
            vListener.primaryViewCellChanged(oldViewCell, newViewCell);
    }
    
    protected void createCameraEntity(WorldManager wm) {
        Node cameraSG = createCameraGraph(wm);
        
        // Add the camera
        Entity camera = new Entity("DefaultCamera");
        cameraComponent = wm.getRenderManager().createCameraComponent(cameraSG, cameraNode, width, height, 45.0f, aspect, 1.0f, 2000.0f, true);
        cameraComponent.setCameraSceneGraph(cameraSG);
        cameraComponent.setCameraNode(cameraNode);
        camera.addComponent(CameraComponent.class, cameraComponent);

        cameraController = new ThirdPersonCameraProcessor();
        cameraProcessor = new CameraProcessor(cameraNode, cameraController);
        camera.addComponent(ProcessorComponent.class, cameraProcessor);

        rb.setCameraComponent(cameraComponent);
        
        wm.addEntity(camera);         
    }

    
    /**
     * Add a camera listener.
     * The listener will be called immediately with the current camera position
     * and then every time the camera moves.
     * @param cameraListener
     */
    public void addCameraListener(CameraListener cameraListener) {
        synchronized(cameraNode) {
            if (cameraListeners==null)
                cameraListeners = new HashSet();
            
            cameraListeners.add(cameraListener);

            cameraListener.cameraMoved(new CellTransform(cameraNode.getWorldRotation(), cameraNode.getWorldTranslation()));
        }
    }
    
    /**
     * Remove the specified camera listener
     * @param cameraListener
     */
    public void removeCameraListener(CameraListener cameraListener) {
        synchronized(cameraNode) {
            if (cameraListeners==null)
                return;
            
            cameraListeners.add(cameraListener);
        }
        
    }

    /**
     * Return the transform of the camera
     * 
     * @return the transform of the camera (in world coordinates) for this view
     */
    public CellTransform getCameraTransform() {
        if (cameraNode!=null)
            return new CellTransform(cameraNode.getWorldRotation(), cameraNode.getWorldTranslation(), cameraNode.getWorldScale());
        return new CellTransform(null, new Vector3f(0,0,0));
    }

    /**
     * Convienence method to return the camera position as a vector.
     *
     * NOTE: THIS IS UNTESTED
     *
     * @return The camera position
     */
    public Vector3f getCameraPosition(Vector3f v3f) {
        return getCameraTransform().getTranslation(v3f);
    }

    /**
     * Returns the camera "look direction" as a vector.
     *
     * NOTE: THIS IS UNTESTED
     *
     * @return The camera look direction
     */
    public Vector3f getCameraLookDirection(Vector3f v) {
        Quaternion rot = cameraNode.getWorldRotation();
        if (v==null)
            v = new Vector3f(0,0,1);
        else
            v.set(0, 0, 1);
        rot.multLocal(v);
        v.normalizeLocal();
        return v;
    }

    /**
     * Return the CameraComponent. This is an internal api.
     * 
     * @return
     * @InternalAPI
     */
    CameraComponent getCameraComponent() {
        return cameraComponent;
    }
    
    private Node createCameraGraph(WorldManager wm) {
        Node cameraSG = new Node("MyCamera SG");        
        cameraNode = new CameraNode("MyCamera", null);
        cameraSG.attachChild(cameraNode);

        cameraNode.addGeometricUpdateListener(new GeometricUpdateListener() {
            public void geometricDataChanged(Spatial arg0) {
                notifyCameraMoved(new CellTransform(arg0.getWorldRotation(), arg0.getWorldTranslation()));
            }
        });

        return (cameraSG);
    }

    private void notifyCameraMoved(CellTransform worldTranform) {
        synchronized(cameraNode) {
            if (cameraListeners!=null) {
                for(CameraListener cameraL : cameraListeners)
                    cameraL.cameraMoved(worldTranform);
            }
        }
    }

    /**
     * Listen for movement of the view cell
     */
    class CellListener implements TransformChangeListener {

        public void transformChanged(Cell cell, ChangeSource source) {
            if (source==ChangeSource.LOCAL) {
                cameraProcessor.viewMoved(cell.getWorldTransform());
            }
        }
        
    }

    /**
     * Listener interface for ViewManager changes
     */
    public interface ViewManagerListener {
        /**
         * Notification of a change in Primary ViewCell. Both the old viewCell and the new
         * view cell are provided. This notification occurs after the change of primary
         * view has taken place.
         * 
         * @param oldViewCell the old view cell, may be null
         * @param newViewCell the new view cell, may be null
         */
        public void primaryViewCellChanged(ViewCell oldViewCell, ViewCell newViewCell);
    }

    /**
     * An interface for listening for camera changes
     */
    public interface CameraListener {
        /**
         * Called when the camera moves
         * @param cameraWorldTransform the world transform of the camera
         */
        public void cameraMoved(CellTransform cameraWorldTransform);
    }
}
