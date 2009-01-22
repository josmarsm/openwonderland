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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CreateWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DestroyWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ConfigureWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.PositionWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.RestackWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetDecoratedMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetUserDisplMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetRotateYMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetBorderWidthMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayPixelsMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CopyAreaMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ControllerStatusMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MoveCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SetWindowTitleMsgArgs;

/**
 * Classes that communicate Xremwin protocol to an Xremwin server (or master)
 * must implement this class.
 *
 * @author deronj
 */

@ExperimentalAPI
interface ServerProxy {

    public void connect() throws IOException;
    public void disconnect();

    public void cleanup();

    public Proto.ServerMessageType getMessageType ();

    public void getData (CreateWindowMsgArgs msgArgs);
    public void getData (DestroyWindowMsgArgs msgArgs);
    public void getData (ShowWindowMsgArgs msgArgs);
    public void getData (ConfigureWindowMsgArgs msgArgs);
    public void getData (PositionWindowMsgArgs msgArgs);
    public void getData (RestackWindowMsgArgs msgArgs);
    public void getData (WindowSetDecoratedMsgArgs msgArgs);
    public void getData (WindowSetBorderWidthMsgArgs msgArgs);
    public void getData (WindowSetUserDisplMsgArgs msgArgs);
    public void getData (WindowSetRotateYMsgArgs msgArgs);
    public void getData (DisplayPixelsMsgArgs msgArgs);
    public void getData (CopyAreaMsgArgs msgArgs);
    public void getData (ControllerStatusMsgArgs msgArgs);
    public void getData (SetWindowTitleMsgArgs msgArgs);

    // TODO: 0.4 protocol: temporarily insert
    public void getData (DisplayCursorMsgArgs msgArgs);
    public void getData (MoveCursorMsgArgs msgArgs);
    public void getData (ShowCursorMsgArgs msgArgs);

    // Returns no data; currently used only for Beep
    public void getData ();

    // Set scanline width (in pixels) 
    void setScanLineWidth (int width);

    byte[] readScanLine ();

    // The next three are for RLE24 only
    // Note: these don't broadcast to pull slaves

    int readRleInt ();

    // Read a chunk of data which is the length of the given buffer
    void readRleChunk (byte[] buf);

    // Read a chunk of data len bytes long
    void readRleChunk (byte[] buf, int len);

    public void writeEvent(int wid, MouseEvent event) throws IOException;
    public void writeWheelEvent(int wid, MouseWheelEvent event) throws IOException;
    public void writeEvent(KeyEvent event) throws IOException;
    public void writeTakeControl (boolean steal) throws IOException;
    public void writeReleaseControl () throws IOException;
    public void writeSetWindowTitle (int wid, String title) throws IOException;
    public void windowSetUserDisplacement (int clientId, int wid, Vector3f userDispl) throws IOException;
    public void windowSetSize (int clientId, int wid, int w, int h) throws IOException;
    public void windowSetRotateY (int clientId, int wid, float rotY) throws IOException;
    public void windowToFront (int clientId, int wid) throws IOException;
    public void destroyWindow (int wid) throws IOException;
    public String getControllingUser();
}
