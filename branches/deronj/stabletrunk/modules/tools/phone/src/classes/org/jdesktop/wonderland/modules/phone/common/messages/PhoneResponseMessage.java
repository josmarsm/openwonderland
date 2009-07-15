/**
 * Project Looking Glass
 * 
 * $RCSfile: PhoneResponseMessage.java,v $
 * 
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 * 
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 * 
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 * 
 * $Revision: 1.8 $
 * $Date: 2008/06/12 18:48:17 $
 * $State: Exp $ 
 */

package org.jdesktop.wonderland.modules.phone.common.messages;

import org.jdesktop.wonderland.modules.phone.common.CallListing;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.cell.CellID;

/*
 *
 * @author: jprovino
 */
public class PhoneResponseMessage extends CellMessage {
    
    private CallListing listing; 
    private boolean wasSuccessful;
    
    public PhoneResponseMessage(CellID cellID, CallListing listing, boolean wasSuccessful) {
	super(cellID);

        this.listing = listing;
        this.wasSuccessful = wasSuccessful;
    }

    public CallListing getCallListing() {
        return listing;
    }

    public boolean wasSuccessful() {
        return wasSuccessful;
    }
    
}