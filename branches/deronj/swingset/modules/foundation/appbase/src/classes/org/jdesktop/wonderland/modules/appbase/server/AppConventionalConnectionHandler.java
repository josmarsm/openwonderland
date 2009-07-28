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
package org.jdesktop.wonderland.modules.appbase.server;

import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.common.AppConventionalConnectionType;
import org.jdesktop.wonderland.modules.appbase.common.AppConventionalMessage;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import com.sun.sgs.app.ClientSession;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.appbase.server.AppConventionalCellMO;
import org.jdesktop.wonderland.modules.appbase.common.AppConventionalCellCreateMessage;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
  * Server handler for the app base conventional connection.
 *
 * @author deronj
 */

@InternalAPI
public abstract class AppConventionalConnectionHandler implements ClientConnectionHandler, Serializable {

    private static final Logger logger = Logger.getLogger(AppConventionalConnectionHandler.class.getName());
    
    protected static final ConnectionType CLIENT_TYPE = AppConventionalConnectionType.CLIENT_TYPE;
    
    public ConnectionType getConnectionType () {
        return CLIENT_TYPE;
    }

    public void registered (WonderlandClientSender sender) {
        // ignore
    }
    
    public void clientConnected (WonderlandClientSender sender, WonderlandClientID clientID, Properties properties) {
	// TODO: anything to do?
    }

    public void clientDisconnected (WonderlandClientSender sender, WonderlandClientID clientID) {
	// TODO: anything to do?
    }
    
    public void messageReceived (WonderlandClientSender sender, WonderlandClientID clientID, Message message)
    {
        if (message instanceof AppConventionalMessage) {
            messageReceived(sender, clientID, (AppConventionalMessage) message);
        } else {
            sender.send(clientID, new ErrorMessage(message.getMessageID(),
						  "Unexpected message type: " + message.getClass()));
        }
    }
  
    /**
     * The handler for App Base conventional messages.
     * @param sender The message sender to use to send responses.
     * @session The client session
     * @param message The app base message
     */
    public void messageReceived (WonderlandClientSender sender, WonderlandClientID clientID, AppConventionalMessage message)
    {        
        switch(message.getActionType()) {

	case CELL_CREATE:
	    AppConventionalCellMO cellMO = createCell((AppConventionalCellCreateMessage)message);
	    // TODO: add cell to list of cells created by this connection. Model after old 
	    // Wonderland session listeners
	    CellID cellID = cellMO.getCellID();
	    // TODO: return cellID in response message
	    break;

	default :
	    logger.severe("Unexpected message in AppConventionalClientHandler " + message.getActionType());
	    sender.send(clientID, new ErrorMessage(message.getMessageID(),
						  "Unexpected message in AppConventionalClientHandler: " +
						  message.getActionType()));
	    break;
        }
    }
    
    /**
     * Get the channel used for sending to all clients of this type
     * @return the channel to send to all clients
     */
    public static WonderlandClientSender getSender() {
        return WonderlandContext.getCommsManager().getSender(CLIENT_TYPE);
    }


    /**
     * Create and return a server cell of the appropriate type.
     * Subclasses should override this to return a cell of the subclass-specific server cell type.
     */
    public abstract AppConventionalCellMO createCell (AppConventionalCellCreateMessage msg);
}