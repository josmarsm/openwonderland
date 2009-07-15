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
 * $Revision: 1.3 $
 * $Date: 2007/10/23 18:27:41 $
 * $State: Exp $
 */
package org.jdesktop.wonderland.client.comms;

import org.jdesktop.wonderland.ExperimentalAPI;

/**
 * Exception when attaching a new client to a Wonderland session fails.
 * @author jkaplan
 */
@ExperimentalAPI
public class AttachFailureException extends Exception {

    /**
     * Creates a new instance of <code>AttachFailureException</code> without 
     * detail message or cause.
     */
    public AttachFailureException() {
    }


    /**
     * Constructs an instance of <code>AttachFailureException</code> with the 
     * specified detail message.
     * @param msg the detail message.
     */
    public AttachFailureException(String msg) {
        super (msg);
    }

    /**
     * Constructs an instance of <code>AttachFailureException</code> with the 
     * specified cause.
     * @param cause the cause of this error.
     */
    public AttachFailureException(Throwable cause) {
        super (cause);
    }

    /**
     * Constructs an instance of <code>AttachFailureException</code> with the 
     * specified detail message and cause.
     * @param msg the detail message.
     * @param cause the cause
     */
    public AttachFailureException(String msg, Throwable cause) {
        super (msg, cause);
    }
}