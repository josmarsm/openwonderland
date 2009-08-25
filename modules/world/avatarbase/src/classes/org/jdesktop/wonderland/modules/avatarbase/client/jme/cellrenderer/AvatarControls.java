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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.input.DefaultCharacterControls;
import imi.input.InputClient;
import imi.input.InputClientGroup;
import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.client.jme.*;
import imi.scene.JScene;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;

/**
 * An AvatarControl using the control scheme from the avatars project
 *
 * @author paulby
 */
public class AvatarControls extends ViewControls {
    private static final Logger logger =
            Logger.getLogger(AvatarControls.class.getName());

    private JScene      m_jscene = null;
    private InputClient m_inputClient;
    private InputClientGroup  inputGroup;
    private final LinkedList<Event> events = new LinkedList();

    private final AvatarEventListener eventListener = new AvatarEventListener();

    private Cell viewCell = null;

    private boolean enable = false;

    public AvatarControls() {
        m_inputClient = new DefaultCharacterControls(ClientContextJME.getWorldManager());
        inputGroup = new InputClientGroup();
        inputGroup.setScheme(m_inputClient);
    }

    @Override
    public void compute(ProcessorArmingCollection arg0) {
            for (Event evt : events) {
                System.err.println(evt);
                if (evt instanceof KeyEvent3D && evt.isFocussed()) {
                    // Strip out KEY_PRESSED caused by auto repeat and ignore KEY_TYPED
                    KeyEvent ke = (KeyEvent) ((KeyEvent3D)evt).getAwtEvent();
                    inputGroup.processKeyEvent(ke); // give the group the event
                } else if (evt instanceof MouseEvent3D && evt.isFocussed()) {
                    MouseEvent me = (MouseEvent) ((MouseEvent3D)evt).getAwtEvent();
                    inputGroup.processMouseEvent(me); // give the group the event
                }
            }
            events.clear();
    }

    @Override
    public void setEnabled(boolean enable) {
        if (this.enable==enable)
            return;

        super.setEnabled(enable);

        logger.fine("[AvatarControls] " + this + " enabled: " + enable);

        if (enable) {
            ClientContext.getInputManager().addGlobalEventListener(eventListener);
            // register the avatar controls with the world manager
            // RED July 2: Class no longer implements a meaningful interface, added as
            //      user data under its class
            ClientContextJME.getWorldManager().addUserData(AvatarControls.class, this);
            ((AvatarCell)viewCell).setSelectedForInput(enable);
        } else {
            ((AvatarCell)viewCell).setSelectedForInput(enable);
            ClientContext.getInputManager().removeGlobalEventListener(eventListener);
            ClientContextJME.getWorldManager().removeUserData(AvatarControls.class);
        }

        this.enable = enable;
    }
    
    @Override
    public void commit(ProcessorArmingCollection arg0) {
        
    }

    @Override
    public void initialize() {
        // Chain with the eventListener so we exectue after, but in the same frame
        eventListener.addToChain(this);
    }

    public void clearInputClients()
    {
        logger.fine("[AvatarControls] clear schemes on " + this);

        m_inputClient = inputGroup.getInputScheme();
        inputGroup.clearSchemes();
        inputGroup.addScheme(m_inputClient);
    }
    
    public InputClient setDefault(InputClient defaultScheme)
    {
        logger.fine("[AvatarControls] set scheme " + defaultScheme +
                    " on " + this.hashCode());
        
        m_inputClient = defaultScheme;
        inputGroup.clearSchemes();
        inputGroup.setScheme(m_inputClient);
        return m_inputClient;
    }
    
    public void addInputClient(InputClient scheme)
    {
        logger.fine("[AvatarControls] add scheme " + scheme + " to " + this);
        inputGroup.addScheme(scheme);
    }

    public JScene getJScene() 
    {
        return m_jscene;
    }

    public void setJScene(JScene jscene) 
    {
        logger.fine("[AvatarControls] set scene to " + jscene + " on " + this);

        m_jscene = jscene;
        // Unsafe assumption!
        if (m_inputClient instanceof DefaultCharacterControls)
            ((DefaultCharacterControls)m_inputClient).setJScene(jscene);
        else
            logger.fine("[AvatarControls] Default scheme was not a " +
                    "\"DefaultCharacterControls\" object. Could not set jscene on it");
    }
    
    public InputClient getInputClient()
    {
        return m_inputClient;
    }

    public void attach(Cell cell) {
        this.viewCell = cell;
    }

//    class AvatarEventListener extends EventClassFocusListener {
//        @Override
//        public Class[] eventClassesToConsume () {
//            return new Class[] { KeyEvent3D.class, MouseEvent3D.class };
//        }
//
//        @Override
//        public void computeEvent (Event event) {
////            System.out.println("evt " +event);
//            // Access to events does not need to be synchronised as the commit
//            // is guaranteed to happen after this computeEvent becuase the processors
//            // are chained
//            events.add(event);
//        }
//
//    }


    class AvatarEventListener extends EventClassListener {
        @Override
        public Class[] eventClassesToConsume () {
            return new Class[] { KeyEvent3D.class, MouseEvent3D.class };
        }

        @Override
        public void computeEvent (Event event) {
//            System.out.println("evt " +event);
            // Access to events does not need to be synchronised as the commit
            // is guaranteed to happen after this computeEvent becuase the processors
            // are chained
            events.add(event);
        }

    }
}