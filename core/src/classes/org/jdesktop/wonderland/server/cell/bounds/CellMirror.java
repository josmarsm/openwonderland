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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.server.cell.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.j3d.Bounds;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix4d;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.server.UserPerformanceMonitor;

/**
 * A partial mirror of a CellMO. Used for performing time processing that
 * requires tree walking while avoiding the need to access all the objects
 * from the sgs db. This mirror is constructed from data in the CellMO, so the
 * mirror does not need to be persisted.
 * 
 * @author paulby
 */
public class CellMirror {

    private Bounds computedWorldBounds;
    private Bounds localBounds;
    private Matrix4d localToVWorld;
    private CellID cellID;
    private Matrix4d transform;
    
    private CellMirror parent;
    private ArrayList<CellMirror> children = null;
    private static Logger logger = Logger.getLogger(CellMirror.class.getName());
    
    CellMirror(CellMO cell) {
        cellID = cell.getCellID();
        localBounds = cell.getLocalBounds();
        transform = cell.getTransform();
    }
    
    /**
     * Returns the local bounds transformed into VW coordinates. These bounds
     * do not include the subgraph bounds. This call is only valid for live
     * cells
     * 
     * @return
     */
    public Bounds getCachedVWBounds() {
        Bounds ret = (Bounds) localBounds.clone();
        Transform3D t3d = new Transform3D(localToVWorld);
        ret.transform(t3d);
        
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
    public Bounds getComputedWorldBounds() {
        return computedWorldBounds;        
    }
    
    /**
     * Set the computed world bounds of this cell
     * 
     * @param bounds
     */
    public void setComputedWorldBounds(Bounds bounds) {
        computedWorldBounds = bounds;
    }

    public Bounds getLocalBounds() {
        return localBounds;
    }

    public void setLocalBounds(Bounds bounds) {
        localBounds = bounds;
    }

    public void setLocalToVWorld(Matrix4d transform) {
        localToVWorld = transform;
    }
    
    public Matrix4d getLocalToVWorld() {
        return localToVWorld;
    }
    
    public void setTransform(Matrix4d transform) {
        this.transform = transform;
    }
    
    public Matrix4d getTransform() {
        return transform;
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
    public void addChild(CellMirror child) throws MultipleParentException {
        if (children==null)
            children = new ArrayList<CellMirror>();
        
        children.add(child);
        child.setParent(this);
    }
    
    /**
     * Remove the specified
     * @param child
     */
    public void removeChild(CellMirror child) {
            if (children == null) {
                return;
            }
            if (children.remove(child)) {
                try {
                    child.setParent(null);
                } catch (MultipleParentException ex) {
                    // Should never get here, setting a null parent does not
                    // throw this exception
                    Logger.getLogger(CellMirror.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
    
    /**
     * Return an iterator over all the children of this object
     * 
     * @return
     */
    public Iterator<CellMirror> getAllChildren() {
        if (children==null)
            return new ArrayList<CellMirror>().iterator();
        return children.iterator();
    }
    
    /**
     * Set the parent of this object, throws a MultipleParentExcetion if this
     * object already has a parent
     * 
     * @param parent
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    void setParent(CellMirror newParent) throws MultipleParentException {
        if (newParent!=null && parent!=null)
            throw new MultipleParentException();
        
        this.parent = newParent;
    }
    
    /**
     * Return the parent CellMirror
     * @return
     */
    public CellMirror getParent() {
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
    ArrayList<CellID> getVisibleCells(
                ArrayList<CellID> list, 
                Bounds bounds,
                UserPerformanceMonitor monitor) {
        
        long t = System.nanoTime();
        
        /*
         * We first must check whether a the cell's origin is relative to its
         * parent. If so, then we translate the bounds of this cell by the
         * origin of the parent. If the parent's bounds is given as null, then
         * we do not need to translate the bounds at all either.
         */
        Bounds tmpComputedWorldBounds = (Bounds)this.getComputedWorldBounds();
        
        /*
         * We now intersect with the bounds of the cell 
         */
        boolean intersect = bounds.intersect(tmpComputedWorldBounds);
        monitor.incRevalidateCalcTime( System.nanoTime()-t );
        monitor.incRevalidateCellCount(getClass());

        /*
        CellGLO.logger.log(Level.INFO, this.getCellID() +
            " - " + ((intersect == true) ? "(VISIBLE) " : "(NOT VISIBLE) ") +
            "worldBounds: " + worldBounds.toString() +
            " parent origin: " +
            ((parentOrigin != null) ? ("(" + parentOrigin.m03 + "," + parentOrigin.m13 + "," + parentOrigin.m23 + ")") : "null") +
            " cellBounds: " + this.getBounds().toString() +
            " viewing bounds: " + bounds.toString());
         */
        
        if (intersect) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Intersect " + cellID + " " + tmpComputedWorldBounds);
            }
            
            list.add(cellID);
            if (children!=null) {
                /*
                 * Recursively call getVisibleCells() to find the visible
                 * cells amongst the children. 
                 */
                
                for(CellMirror c : children) {
                    t = System.nanoTime();
                    monitor.incRevalidateCellGetTime(c.getClass(), System.nanoTime()-t);
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
    
}
