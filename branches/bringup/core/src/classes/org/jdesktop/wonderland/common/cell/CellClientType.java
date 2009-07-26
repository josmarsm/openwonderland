/**
 * Project Wonderland
 *
 * $RCSfile: LogControl.java,v $
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
 * $Revision$
 * $Date$
 * $State: Exp $
 */
package org.jdesktop.wonderland.common.cell;

import org.jdesktop.wonderland.common.comms.ClientType;

/**
 * The ClientType of the CellClient
 * @author jkaplan
 */
public class CellClientType extends ClientType {
    /** the client type for the cell client */
    public static final ClientType CLIENT_TYPE =
            new CellClientType("__CellClient");
    
    private CellClientType(String type) {
        super (type);
    }
}