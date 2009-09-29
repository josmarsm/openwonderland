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
package org.jdesktop.wonderland.modules.xremwin.client;

import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Class which holds definitions for the Remote Window Protocol.
 *
 * @author deronj
 */
@ExperimentalAPI
public class Proto {

    /** Xremwin Protocol Message Types */
    public enum ServerMessageType {

        // These are generated by both Xremwin servers and masters.
        // An Xremwin server sends them to Xremwin masters and
        // Xremwin masters forward them to Xremwin slaves.
        CREATE_WINDOW,
        DESTROY_WINDOW,
        SHOW_WINDOW,
        CONFIGURE_WINDOW,
        POSITION_WINDOW,
        RESTACK_WINDOW,
        WINDOW_SET_DECORATED,
        WINDOW_SET_BORDER_WIDTH,
        WINDOW_SET_USER_DISPLACEMENT,
        WINDOW_SET_ROTATE_Y,
        // TODO: 0.4 protocol: temporarily insert this here
        SLAVE_CLOSE_WINDOW,
        BEEP,
        DISPLAY_PIXELS,
        COPY_AREA,
        CONTROLLER_STATUS,
        // TODO: 0.4 protocol: temporarily insert
        DISPLAY_CURSOR,
        MOVE_CURSOR,
        SHOW_CURSOR,
        // This message is seen only by the slaves. It is generated by the master and forwarded
        // to the slaves via the Xremwin server.
        SET_WINDOW_TITLE,
        /* TODO: 0.4 protocol: temporarily comment out
        // This message is sent by slaves via the Xserver to the master to indicate that a window has been closed
        SLAVE_CLOSE_WINDOW,
         */
        // These are "pseudo-messsages." They aren't ever generated by the Xremwin server.
        // The following pseudo-messages are generated only by Xremwin masters and only
        // seen by Xremwin slaves.

        WELCOME,
        SET_POPUP_PARENT,
        CONTROLLING_USER_NAME,
        // A pseudo-message seen only by Xremwin masters ; actually comes from ServerProxy code.
        SERVER_DISCONNECT,
        // These pseudo messages are seen only by Xremwin masters; they actually come from slaves.
        SLAVE_HELLO
    }

    public enum PixelEncoding {

        UNCODED,
        RLE24
    }

    public enum ClientMessageType {
        INVALID (0, -1),
        EVENT_KEY (12, 8),
        EVENT_POINTER (16, 12),
        TAKE_CONTROL (8, 4),
        RELEASE_CONTROL (8, 4),
        SET_WINDOW_TITLE (12, -1), // TODO: add client ID?
        WINDOW_SET_USER_DISPLACEMENT (24, 4),
        WINDOW_SET_SIZE (20, 4),
        WINDOW_SET_ROTATE_Y (16, 4),
        WINDOW_TO_FRONT (12, 4),
        DESTROY_WINDOW (8, -1), // TODO: add client ID?
        SLAVE_CLOSE_WINDOW (12, 4),
        HELLO (4, -1);

        int size;
        int clientIdIndex;

        ClientMessageType(int size, int clientIdIndex) {
            this.size = size;
            this.clientIdIndex = clientIdIndex;
        }

        public int size() {
            return size;
        }

        public int clientIdIndex() {
            return clientIdIndex;
        }
    }

    public enum ControllerStatus {

        REFUSED,
        LOST,
        GAINED
    }
    
    public static final int WINDOW_SET_USER_DISPL_MESSAGE_SIZE = 24;
    public static final int WINDOW_SET_ROTATE_Y_MESSAGE_SIZE = 16;
    public static final int WELCOME_MESSAGE_SIZE = 8;
    public static final int CONTROLLING_USER_NAME_MESSAGE_SIZE = 4;
    public static final int CONTROLLER_STATUS_MESSAGE_SIZE = 8;
    public static final int SET_POPUP_PARENT_MESSAGE_SIZE = 12;
  
    /** the size of the signature appended to client messages */
    public static final int SIGNATURE_SIZE = 4 + 20;

    public static class MessageArgs {
    }

    public static class CreateWindowMsgArgs extends MessageArgs {

        public boolean decorated;
        public int wid;
        public short x;
        public short y;
        public int wAndBorder;
        public int hAndBorder;
        public int borderWidth;

        @Override
        public String toString() {
            String str = "wid = " + wid;
            str += ", decorated = " + decorated;
            str += ", xy = " + x + ", " + y;
            str += ", whAndBorder = " + wAndBorder + ", " + hAndBorder;
            str += ", bw = " + borderWidth;
            return str;
        }
    }

    public static class DestroyWindowMsgArgs extends MessageArgs {

        public int wid;

        @Override
        public String toString() {
            return "wid = " + wid;
        }
    }

    public static class SlaveCloseWindowMsgArgs extends MessageArgs {

        public int clientId;
        public int wid;

        @Override
        public String toString() {
            return "wid = " + wid;
        }
    }

    public static class ShowWindowMsgArgs extends MessageArgs {

        public boolean show;
        public int wid;
        /* TODO: 0.4 protocol:
        public int transientFor;
         */
        public boolean isTransient;

        @Override
        public String toString() {
            /* TODO: 0.4 protocol:
            return "wid = " + wid + ", show = " + show + ", transientFor = " + transientFor;
             */
            return "wid = " + wid + ", show = " + show +
                    ", isTransient = " + isTransient;
        }
    }

    public static class ConfigureWindowMsgArgs extends MessageArgs {

        public int clientId;
        public int wid;
        public short x;
        public short y;
        public int wAndBorder;
        public int hAndBorder;
        public int sibid;

        @Override
        public String toString() {
            String str = "clientId = " + clientId;
            str += ", wid = " + wid;
            str += ", xy = " + x + ", " + y;
            str += ", whAndBorder = " + wAndBorder + ", " + hAndBorder;
            str += ", sibid = " + sibid;
            return str;
        }
    }

    public static class PositionWindowMsgArgs extends MessageArgs {

        public int clientId;
        public int wid;
        public short x;
        public short y;

        @Override
        public String toString() {
            String str = "clientId = " + clientId;
            str += ", wid = " + wid;
            str += ", xy = " + x + ", " + y;
            return str;
        }
    }

    public static class RestackWindowMsgArgs extends MessageArgs {

        public int clientId;
        public int wid;
        public int sibid;

        @Override
        public String toString() {
            String str = "clientId = " + clientId;
            str += ", wid = " + wid;
            str += ", sibid = " + sibid;
            return str;
        }
    }

    public static class WindowSetDecoratedMsgArgs extends MessageArgs {

        public int wid;
        public boolean decorated;

        @Override
        public String toString() {
            return "wid = " + wid + ", decorated = " + decorated;
        }
    }

    public static class WindowSetBorderWidthMsgArgs extends MessageArgs {

        public int wid;
        public int borderWidth;

        @Override
        public String toString() {
            return "wid = " + wid + ", bw = " + borderWidth;
        }
    }

    public static class WindowSetUserDisplMsgArgs extends MessageArgs {

        public int clientId;
        public int wid;
        public Vector3f userDispl;

        @Override
        public String toString() {
            return "clientId = " + clientId + "wid = " + wid + ", userDispl = " + userDispl;
        }
    }

    public static class WindowSetRotateYMsgArgs extends MessageArgs {

        public int clientId;
        public int wid;
        public float roty;

        @Override
        public String toString() {
            return "clientId = " + clientId + "wid = " + wid +
                    ", roty = " + roty;
        }
    }

    public static class DisplayPixelsMsgArgs extends MessageArgs {

        public int wid;
        public int x,  y,  w,  h;
        public PixelEncoding encoding;

        @Override
        public String toString() {
            String str = "wid = " + wid;
            str += ", x = " + x;
            str += ", y = " + y;
            str += ", w = " + w;
            str += ", h = " + h;
            str += ", enc = " + encoding;
            return str;
        }
    }

    public static class DisplayPixelsRect extends MessageArgs {
    }

    public static class CopyAreaMsgArgs extends MessageArgs {

        public int wid;
        public int srcX,  srcY;
        public int width,  height;
        public int dstX,  dstY;

        @Override
        public String toString() {
            String str = "wid = " + wid;
            str += ", srcXY = " + srcX + ", " + srcY;
            str += ", wh = " + width + ", " + height;
            str += ", dstXY = " + dstX + ", " + dstY;
            return str;
        }
    }

    public static class ControllerStatusMsgArgs extends MessageArgs {

        public ControllerStatus status;
        public int clientId;

        @Override
        public String toString() {
            String str = "status = " + status;
            str += ", clientId = " + clientId;
            return str;
        }
    }

    public static class SetWindowTitleMsgArgs extends MessageArgs {

        public int wid;
        public String title;

        @Override
        public String toString() {
            return "wid = " + wid + ", title = " + title;
        }
    }

    public static class UserNameMsgArgs extends MessageArgs {

        public String userName;

        @Override
        public String toString() {
            return "userName = " + userName;
        }
    }

    public static class SetPopupParentMsgArgs extends MessageArgs {

        public int wid;
        public int parentWid;

        @Override
        public String toString() {
            String str = "wid = " + wid;
            str += ", parentWid = " + parentWid;
            return str;
        }
    }

    // TODO: 0.4 protocol: temporarily insert
    public static class DisplayCursorMsgArgs extends MessageArgs {

        public int width;
        public int height;
        public int xhot;
        public int yhot;
        public int[] pixels;

        @Override
        public String toString() {
            String str = "wh = " + width + ", " + height;
            str += ", xyhot = " + xhot + ", " + yhot;
            return str;
        }
    }

    // TODO: 0.4 protocol: temporarily insert
    public static class MoveCursorMsgArgs extends MessageArgs {

        public int wid;
        public int x;
        public int y;

        @Override
        public String toString() {
            String str = "wid = " + wid;
            str += ", xy = " + x + ", " + y;
            return str;
        }
    }

    // TODO: 0.4 protocol: temporarily insert
    public static class ShowCursorMsgArgs extends MessageArgs {

        public boolean show;

        @Override
        public String toString() {
            return "show = " + show;
        }
    }
}
