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

package org.jdesktop.wonderland.modules.sample.client;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.modules.sample.common.SampleCellSubComponentClientState;

/**
 * Client-side sample cell sub-component
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SampleCellSubComponent extends CellComponent {

    private static Logger logger = Logger.getLogger(SampleCellSubComponent.class.getName());
    private String info = null;

    public SampleCellSubComponent(Cell cell) {
        super(cell);
    }

    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);
        info = ((SampleCellSubComponentClientState)clientState).getInfo();
    }

    @Override
    public void setStatus(CellStatus status) {
        super.setStatus(status);
        logger.warning("Setting status on SampleCellSubComponent to " + status);
    }
}
