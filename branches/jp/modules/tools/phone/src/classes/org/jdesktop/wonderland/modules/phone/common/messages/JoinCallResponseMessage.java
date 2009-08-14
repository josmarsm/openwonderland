/**
 * Project Looking Glass
 * 
 * $RCSfile: JoinCallResponseMessage.java,v $
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

import org.jdesktop.wonderland.common.messages.Message;

/*
 *
 * @author: jprovino
 */
public class JoinCallResponseMessage extends PhoneResponseMessage {
    
    public JoinCallResponseMessage(CallListing listing, boolean wasSuccessful) {
	super(listing, wasSuccessful);
    }

}