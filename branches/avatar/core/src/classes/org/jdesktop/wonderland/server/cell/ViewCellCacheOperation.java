/**
 * Project Wonderland
 *
 * $Id$
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
 */
package org.jdesktop.wonderland.server.cell;

import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * An interface that provides for active cells to process the view position
 * each time the Cache is revalidated.
 * 
 * @author paulby
 */
public interface ViewCellCacheOperation {
    
    /**
     * Called by the ViewCellCache during each revalidation if
     * this cell is active
     */
    public void execute(CellTransform viewTransform);
}