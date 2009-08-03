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
package org.jdesktop.wonderland.modules.simplewhiteboard.client;

import java.math.BigInteger;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.simplewhiteboard.client.BufferedCompoundMessageSender;
import org.jdesktop.wonderland.modules.simplewhiteboard.client.cell.WhiteboardCell;
import org.jdesktop.wonderland.modules.simplewhiteboard.common.cell.WhiteboardCompoundCellMessage;
import org.jdesktop.wonderland.modules.simplewhiteboard.common.WhiteboardAction;
import org.jdesktop.wonderland.modules.simplewhiteboard.common.cell.WhiteboardCellMessage;

/**
 * The client side of the communication component that provides communication between the whiteboard client and server.
 * Requires ChannelComponent to also be connected to the cell prior to construction.
 *
 * @author deronj
 */
@ExperimentalAPI
public class WhiteboardComponent extends CellComponent {

    private static Logger logger = Logger.getLogger(WhiteboardComponent.class.getName());
    /** An object which sends drawing messages to the server cell */
    private BufferedCompoundMessageSender bufferedSender;
    /** The cell channel communications component. */
    private ChannelComponent channelComp;
    /** The message receiver of this class */
    private ChannelComponent.ComponentMessageReceiver msgReceiver;
    /** The cell to which this component belongs. */
    private WhiteboardCell cell;

    /** 
     * Create a new instance of WhiteboardComponent. 
     * @param cell The cell to which this component belongs.
     * @throws IllegalStateException If the cell does not already have a ChannelComponent IllegalStateException will be thrown.
     */
    public WhiteboardComponent(Cell cell) {
        super(cell);
        this.cell = (WhiteboardCell) cell;
    }

    /**
     * React to cell status changes by listening to 
     * @param status The current status of this cell.
     */
    @Override
    public void setStatus(CellStatus status) {
        switch (status) {

            case BOUNDS: {

                channelComp = cell.getComponent(ChannelComponent.class);
                if (channelComp == null) {
                    throw new IllegalStateException("Cell does not have a ChannelComponent");
                }
                bufferedSender = new BufferedCompoundMessageSender(channelComp);

                if (msgReceiver == null) {
                    msgReceiver = new ChannelComponent.ComponentMessageReceiver() {

                        public void messageReceived(CellMessage message) {
                            // All messages sent over the connection are of this type
                            WhiteboardCompoundCellMessage msg = (WhiteboardCompoundCellMessage) message;

                            // Note: The new DarkStar doesn't support the exclusion of certain clients from
                            // its message broadcasts. Therefore all messages that we send will be echoed
                            // back to us! Therefore we must ignore the echoed messages.
                            BigInteger msgClientID = msg.getClientID();
                            if (msgClientID == null ||
                                !msgClientID.equals(cell.getClientID())) {
                                cell.processMessage(msg);
                            }
                        }
                    };
                    channelComp.addMessageReceiver(WhiteboardCompoundCellMessage.class, msgReceiver);
                }

                // Must do *after* registering the listener.
                sync();

                logger.severe("********* Whiteboard cell initialization complete, cellID = " + cell.getCellID());
                break;
            }

            case DISK:
                if (msgReceiver != null) {
                    channelComp.removeMessageReceiver(WhiteboardCompoundCellMessage.class);
                    msgReceiver = null;
                }
                channelComp = null;
                bufferedSender = null;
                break;
        }
    }

    /**
     * Send a command to sync with the shared whiteboard data. The server will respond with
     * messages which provide the latest data.
     */
    private void sync() {
        WhiteboardCompoundCellMessage cmsg = new WhiteboardCompoundCellMessage(cell.getClientID(), cell.getCellID(),
                WhiteboardAction.REQUEST_SYNC);
        channelComp.send(cmsg);
    }

    /**
     * Send a drawing command to the server.
     * @param msg The message to send.
     */
    public void sendMessage(WhiteboardCellMessage msg) {
        bufferedSender.enqueue(msg);
    }
}