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
package org.jdesktop.wonderland.modules.simplewhiteboard.common;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Whiteboard action types
 *
 * @author nsimpson
 */

@ExperimentalAPI
public interface WhiteboardAction {
    public enum Action { 
        NO_ACTION, 
        SET_TOOL, 
        SET_COLOR, 
        MOVE_TO, 
        DRAG_TO,
        REQUEST_SYNC, 
        EXECUTE_COMMAND 
    };
    
    public final static Action NO_ACTION = Action.NO_ACTION;
    public final static Action SET_TOOL = Action.SET_TOOL;
    public final static Action SET_COLOR = Action.SET_COLOR;
    public final static Action MOVE_TO = Action.MOVE_TO;
    public final static Action DRAG_TO = Action.DRAG_TO;
    public final static Action REQUEST_SYNC = Action.REQUEST_SYNC;  
    public final static Action EXECUTE_COMMAND = Action.EXECUTE_COMMAND; 
}