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
package org.jdesktop.wonderland.testharness.slave;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A Log Handler that sends the log messages back to the master
 * 
 * @author paulby
 */
public class SlaveLogHandler extends Handler {

    private static ObjectOutputStream out = null;  // Needs to be static because there can be multiple SlaveLogHandler instances
    
    static void setOutputStream(ObjectOutputStream outStream) {
        out = outStream;
    }
    
    
    @Override
    public void publish(LogRecord record) {
        if (out==null || record==null)
            return;
        
        synchronized(out) {
            try {
                out.writeObject(record);
            } catch (IOException ex) {
                Logger.getLogger(SlaveLogHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void flush() {
        if (out!=null) {
            synchronized(out) {
                try {
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(SlaveLogHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void close() throws SecurityException {
        // Nothing to do.
    }

}