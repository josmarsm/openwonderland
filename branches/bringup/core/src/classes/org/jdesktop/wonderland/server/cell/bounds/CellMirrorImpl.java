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
package org.jdesktop.wonderland.server.cell.bounds;

import com.jme.bounding.BoundingVolume;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.server.cell.*;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdesktop.wonderland.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.server.cell.RevalidatePerformanceMonitor;

/**
 * A partial mirror of a CellMO. Used for performing time processing that
 * requires tree walking while avoiding the need to access all the objects
 * from the sgs db. This mirror is constructed from data in the CellMO, so the
 * mirror does not need to be persisted.
 * 
 * @author paulby
 */
@InternalAPI
public class CellMirrorImpl implements CellMirror {

    private BoundingVolume computedWorldBounds;
    private BoundingVolume localBounds;
    private CellTransform localToVWorld;
    private CellID cellID;
    private CellTransform transform;
    private int transformVersion;
    private int contentsVersion;
    private short priority;
    private Class cellClass;
    
    private CellMirrorImpl parent;
    private ArrayList<CellMirrorImpl> children = null;
    private static Logger logger = Logger.getLogger(CellMirrorImpl.class.getName());
    
    CellMirrorImpl(CellMO cell) {
        cellID = cell.getCellID();
        localBounds = cell.getLocalBounds();
        transform = cell.getTransform();
        cellClass = cell.getClass();
        this.priority = cell.getPriority();
    }
    
    /**
     * Create new CellMirror from the provided mirror
     * @param mirror
     */
    CellMirrorImpl(CellMirror mirror) {
        this.cellID = mirror.getCellID();
        this.transformVersion = mirror.getTransformVersion();
        this.contentsVersion = mirror.getContentsVersion();
        this.priority = mirror.getPriority();
        this.localBounds = mirror.getLocalBounds();
        this.transform = mirror.getTransform();
    }
    
    /**
     * Returns the local bounds transformed into VW coordinates. These bounds
     * do not include the subgraph bounds. This call is only valid for live
     * cells
     * 
     * @return
     */
    public BoundingVolume getCachedVWBounds() {
        BoundingVolume ret = localBounds.clone(null);
        localToVWorld.transform(ret);
        
        return ret;
    }
    
    /**
     * Return a computed bounds for this cell in World coordinates that 
     * encapsulates the bounds of this cell and all it's children.
     * 
     * The bounds returned by this call are computed periodically so changes
     * to the local bounds of this node or any of it's children may not be 
     * immediately reflected in this bounds.
     * 
     * @return
     */
    public BoundingVolume getComputedWorldBounds() {
        return computedWorldBounds;        
    }
    
    /**
     * Set the computed world bounds of this cell
     * 
     * @param bounds
     */
    public void setComputedWorldBounds(BoundingVolume bounds) {
//        System.out.println("Setting CWB "+getCellID()+" : "+bounds);
        computedWorldBounds = bounds.clone(computedWorldBounds);
    }

    /**
     * Returns a copy of the local bounds
     * @return
     */
    public BoundingVolume getLocalBounds() {
        return localBounds.clone(null);
    }

    public void setLocalBounds(BoundingVolume bounds) {
        localBounds = bounds.clone(null);
    }

    public void setLocalToVWorld(CellTransform transform) {
        localToVWorld = (CellTransform) transform.clone();
    }
    
    public CellTransform getLocalToVWorld() {
        return (CellTransform) localToVWorld.clone();
    }
    
    public void setTransform(CellTransform transform) {
        this.transform = (CellTransform) transform.clone();
        transformVersion++;
    }
    
    public CellTransform getTransform() {
        return (CellTransform) transform.clone();
    }
    
    /**
     * Return the cellID of the cell associated with this bounds
     * @return
     */
    public CellID getCellID() {
        return cellID;
    }
    
    /**
     * Add a child object. Throws a MultipleParentException if this object
     * aleady has a parent
     * 
     * @param child
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    public void addChild(CellMirrorImpl child) throws MultipleParentException {
        if (children==null)
            children = new ArrayList<CellMirrorImpl>();
        
        children.add(child);
        child.setParent(this);
    }
    
    /**
     * Remove the specified
     * @param child
     */
    public void removeChild(CellMirrorImpl child) {
            if (children == null) {
                return;
            }
            if (children.remove(child)) {
                try {
                    child.setParent(null);
                } catch (MultipleParentException ex) {
                    // Should never get here, setting a null parent does not
                    // throw this exception
                    Logger.getLogger(CellMirrorImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
    
    /**
     * Return an iterator over all the children of this object
     * 
     * @return
     */
    public Iterator<CellMirrorImpl> getAllChildren() {
        if (children==null)
            return new ArrayList<CellMirrorImpl>().iterator();
        return children.iterator();
    }

    void contentsChanged() {
        contentsVersion++;
    }
    
    /**
     * Set the parent of this object, throws a MultipleParentExcetion if this
     * object already has a parent
     * 
     * @param parent
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    void setParent(CellMirrorImpl newParent) throws MultipleParentException {
        if (newParent!=null && parent!=null)
            throw new MultipleParentException();
        
        this.parent = newParent;
    }
    
    /**
     * Return the parent CellMirrorImpl
     * @return
     */
    public CellMirrorImpl getParent() {
        return parent;
    }

    /**
     * Returns a list of Cells that intersect with the supplied bounds. This
     * method adds to the list of visible cells passed to it and returned the
     * new list.
     *
     * @param list The list of visible cells
     * @param bounds The viewing bounds, in world coordinates
     * @param monitor The performance monitor
     * @return A list of visible cells
     */
    ArrayList<CellMirror> getVisibleCells(
                ArrayList<CellMirror> list, 
                BoundingVolume bounds,
                RevalidatePerformanceMonitor monitor) {
        
        long t = System.nanoTime();
        
        /*
         * We first must check whether a the cell's origin is relative to its
         * parent. If so, then we translate the bounds of this cell by the
         * origin of the parent. If the parent's bounds is given as null, then
         * we do not need to translate the bounds at all either.
         */
        BoundingVolume tmpComputedWorldBounds = (BoundingVolume)this.getComputedWorldBounds();
        
        /*
         * We now intersect with the bounds of the cell 
         */
        boolean intersect = bounds.intersects(tmpComputedWorldBounds);
        monitor.incBoundsCalcTime( System.nanoTime()-t );
        monitor.incBoundsCellCount(getClass());

//        logger.log(Level.INFO, this.getCellID() +
//            " - " + ((intersect == true) ? "(VISIBLE) " : "(NOT VISIBLE) ") +
//            "worldBounds: " + getComputedWorldBounds() +
//            " parent origin: " +
//            " cellBounds: " + getCachedVWBounds() +
//            " viewing bounds: " + bounds);
        
        if (intersect) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Intersect " + cellID + " " + tmpComputedWorldBounds);
            }
            
            list.add(new CellMirrorImpl(this));
            if (children!=null) {
                /*
                 * Recursively call getVisibleCells() to find the visible
                 * cells amongst the children. 
                 */
                
                for(CellMirrorImpl c : children) {
                    t = System.nanoTime();
                    monitor.incBoundsGetTime(c.getClass(), System.nanoTime()-t);
                    c.getVisibleCells(list, bounds, monitor);
                }
            }
        } else {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("No Intersection  " + cellID + " " + tmpComputedWorldBounds);
            }
        }
        
        return list;
    }

    /**
     * @{inheritDoc}
     */
    public int getTransformVersion() {
        return transformVersion;
    }

    /**
     * @{inheritDoc}
     */
    public int getContentsVersion() {
        return contentsVersion;
    }

    /**
     * @{inheritDoc}
     */
    public short getPriority() {
        return priority;
    }
    
}