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
package org.jdesktop.wonderland.client.simplewhiteboard;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.setup.CellSetup;
import org.jdesktop.wonderland.client.app.base.AppType;
import org.jdesktop.wonderland.client.app.base.App2DCell;
import org.jdesktop.wonderland.client.app.base.AppTypeCell;
import org.jdesktop.wonderland.common.app.simplewhiteboard.WhiteboardCellSetup;
import org.jdesktop.wonderland.common.app.simplewhiteboard.CompoundWhiteboardCellMessage;
import org.jdesktop.wonderland.common.app.simplewhiteboard.WhiteboardAction;
import org.jdesktop.wonderland.common.app.simplewhiteboard.WhiteboardAction.Action;
import org.jdesktop.wonderland.common.app.simplewhiteboard.WhiteboardCommand.Command;
import org.jdesktop.wonderland.common.app.simplewhiteboard.WhiteboardTypeName;

/**
 * Client Cell for a whiteboard shared application.
 *
 * @author nsimpson,deronj
 */

@ExperimentalAPI
public class WhiteboardCell extends App2DCell {
    
    /** The logger used by this class */
    private static final Logger logger = Logger.getLogger(WhiteboardCell.class.getName());
    
    /** The (singleton) window created by the whiteboard app */
    private WhiteboardWindow whiteboardWin;

    /** The cell setup message received from the server cell */
    private WhiteboardCellSetup setup;
    
    /** The communications component used to communicate with the server */
    private WhiteboardComponent commComponent;

    /**
     * Create an instance of WhiteboardCell.
     *
     * @param cellID The ID of the cell.
     * @param cellCache the cell cache which instantiated, and owns, this cell.
     */
    public AppCell (CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
        addComponent(new ChannelComponent(this));
	commComponent = new WhiteboardComponent(this)
        addComponent(commComponent);
    }
    
    /** 
     * {@inheritDoc}
     */
    public AppType getAppType () {
	// TODO: changes 0.5
	return AppTypeCell.findAppType(WhiteboardTypeName.WHITEBOARD_APP_TYPE_NAME);
    }

    /**
     * Initialize the whiteboard
     *
     * @param setupData the setup data to initialize the cell with
     */
    public void setup(CellSetup setupData) {

        setup = (WhiteboardCellSetup)setupData;
        app = new WhiteboardApp(getAppType(), setup.getPreferredWidth(), setup.getPreferredHeight(),
				setup.getPixelScale(), commComponent);

	// Associate the app with this cell (must be done before making it visible)
	app.setCell(this);

	// Get the window the app created
	whiteboardWin = ((WhiteboardApp)app).getWindow();

	// Make the app window visible
	((WhiteboardApp)app).setVisible(true);

	// Note: we used to force a sync here. But in the new implementation we will
	// perform the sync when the cell status becomes BOUNDS.
    }
    
    /**
     * Process the actions in a compound message
     *
     * @param msg a compound message
     */
    private void processMessage(CompoundWhiteboardCellMessage msg) {
        switch (msg.getAction()) {
            case SET_TOOL:
                whiteboardWin.setTool(msg.getTool());
                break;
            case SET_COLOR:
                whiteboardWin.setPenColor(msg.getColor());
                break;
            case MOVE_TO:
            case DRAG_TO:
                LinkedList<Point> positions = msg.getPositions();
                Iterator<Point> iter = positions.iterator();
                
                while (iter.hasNext()) {
                    Point position = iter.next();
                    if (msg.getAction() == Action.MOVE_TO) {
                        whiteboardWin.moveTo(position);
                    } else if (msg.getAction() == Action.DRAG_TO) {
                        whiteboardWin.dragTo(position);
                    }
                }
                break;
            case EXECUTE_COMMAND:
                if (msg.getCommand() == Command.ERASE) {
                    whiteboardWin.erase();
                }
        }
    }
}