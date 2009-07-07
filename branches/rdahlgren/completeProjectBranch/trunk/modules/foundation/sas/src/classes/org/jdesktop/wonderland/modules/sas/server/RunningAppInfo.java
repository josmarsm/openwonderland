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
package org.jdesktop.wonderland.modules.sas.server;

import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.util.HashMap;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.MessageID;
import com.sun.sgs.app.AppContext;

/**
 * Information about provider apps that are running.
 */

@ExperimentalAPI
public class RunningAppInfo implements ManagedObject, Serializable {

    public class AppInfo implements Serializable {
        public ProviderProxy provider;
        public CellID cellID;
        public AppInfo (ProviderProxy provider, CellID cellID) {
            this.provider = provider;
            this.cellID = cellID;
        }
    }

    private HashMap<MessageID,AppInfo> runningAppMap = new HashMap<MessageID,AppInfo>();

    public void addAppInfo (MessageID msgID, ProviderProxy provider, CellID cellID) {
        AppInfo msgInfo = new AppInfo(provider, cellID);
        runningAppMap.put(msgID, msgInfo);
        AppContext.getDataManager().markForUpdate(this);
    }

    public void removeAppInfo (MessageID msgID) {
        runningAppMap.remove(msgID);
        AppContext.getDataManager().markForUpdate(this);
    }

    public AppInfo getAppInfo (MessageID msgID) {
        return runningAppMap.get(msgID);
    }
}
