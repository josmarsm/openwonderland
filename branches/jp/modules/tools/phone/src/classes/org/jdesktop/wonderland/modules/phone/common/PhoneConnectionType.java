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
package org.jdesktop.wonderland.modules.phone.common;

import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * The client type for phones
 * @author jkaplan
 */
public class PhoneConnectionType extends ConnectionType {
    /** The phone client type */
    public static final ConnectionType CONNECTION_TYPE =
            new PhoneConnectionType();
    
    /** Use the static CLIENT_TYPE, not this constructor */
    public PhoneConnectionType() {
        super ("__PhoneConnection");
    }

}