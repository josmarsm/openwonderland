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

package org.jdesktop.wonderland.server.cell;

import com.sun.sgs.app.ClientSessionId;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellCacheClientType;
import org.jdesktop.wonderland.common.cell.messages.CellHierarchyMessage;
import org.jdesktop.wonderland.common.comms.ClientType;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.server.UserMO;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.ClientHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientChannel;

/**
 * Handler for the cell cache
 * @author paulby
 */
class CellCacheClientHandler implements ClientHandler, Serializable {
    
    private  String avatarID=null;
    private static final Logger logger = Logger.getLogger(CellCacheClientHandler.class.getName());
    
    protected static final ClientType CLIENT_TYPE =
            CellCacheClientType.CLIENT_TYPE;
    
    public ClientType getClientType() {
        return CLIENT_TYPE;
    }

    public void registered(WonderlandClientChannel channel) {
        // ignore
    }
    
    public void clientAttached(WonderlandClientChannel channel,
                               ClientSessionId sessionId)    {
        // Nothing to do, setup is done when we get the SET_AVATAR
        // message
    }

    public void clientDetached(WonderlandClientChannel channel,
                               ClientSessionId sessionId) 
    {
        UserMO user = WonderlandContext.getUserManager().getUser(sessionId);
        AvatarMO avatar = user.getAvatar(avatarID);
        if (avatar == null) {
            logger.severe("clientDetached has null avatar for session");
            return;
        }
        avatar.getCellCache().logout(sessionId);
    }
    
    public void messageReceived(WonderlandClientChannel channel,
                                ClientSessionId sessionId, 
                                Message message)
    {
        if (message instanceof CellHierarchyMessage) {
            messageReceived(channel, sessionId, (CellHierarchyMessage) message);
        } else {
            channel.send(sessionId, new ErrorMessage(message.getMessageID(),
                         "Unexpected message type: " + message.getClass()));
        }
    }
  
    /**
     * When a cell message is received, dispatch it to the appropriate cell.
     * If the cell does not exist, send back an error message.
     * @param message the cell message
     * @param sender the message sender to send responses to
     */
    public void messageReceived(WonderlandClientChannel channel,
                                ClientSessionId sessionId,
                                CellHierarchyMessage message) {        
        switch(message.getActionType()) {
            case SET_AVATAR :
                avatarID = message.getAvatarID();
                UserMO user = WonderlandContext.getUserManager().getUser(sessionId);
                AvatarMO avatar = user.getAvatar(avatarID);
                if (avatar == null) {
                    logger.severe("Unable to locate avatar");
                    return;
                }

                avatar.getCellCache().login(channel, sessionId);
                
                break;
            default :
                logger.severe("Unexpected message in CellCacheClietnHandler "+message.getActionType());
        }
    }
    
    /**
     * Get the channel used for sending to all clients of this type
     * @return the channel to send to all clients
     */
    public static WonderlandClientChannel getChannel() {
        return WonderlandContext.getCommsManager().getChannel(CLIENT_TYPE);
    }
}