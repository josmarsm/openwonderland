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
package org.jdesktop.wonderland.common.cell.messages;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 *
 * @author paulby
 */
public class AvatarCreateResponseMessage extends ResponseMessage {

    private CellID avatarCellID;
    
    public AvatarCreateResponseMessage(MessageID messageID, CellID avatarCellID) {
        super (messageID);
        this.avatarCellID = avatarCellID;
    }

    public CellID getAvatarCellID() {
        return avatarCellID;
    }
    
    
}