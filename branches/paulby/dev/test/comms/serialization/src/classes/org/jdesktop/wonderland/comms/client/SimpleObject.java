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
package org.jdesktop.wonderland.comms.client;

import java.io.Serializable;

/**
 * A simple serializable test object
 * 
 * @author paulby
 */
public class SimpleObject implements Serializable {

    private long testLong;
    
    public SimpleObject(long testLong) {
        this.testLong = testLong;
    }

    public long getTestLong() {
        return testLong;
    }

    public void setTestLong(long testLong) {
        this.testLong = testLong;
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof SimpleObject))
            return false;
        
        if (((SimpleObject)o).testLong!=testLong)
            return false;
        
        return true;
    }
    
}