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
package org.jdesktop.wonderland.client.jme;

import com.jme.scene.GeometricUpdateListener;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;

/**
 *
 */
public class JmeClientMain {
    
    // properties
    private Properties props;
    
    // standard properties
    private static final String SERVER_NAME_PROP = "sgs.server";
    private static final String SERVER_PORT_PROP = "sgs.port";
    private static final String USER_NAME_PROP   = "cellboundsviewer.username";
    
    // default values
    private static final String SERVER_NAME_DEFAULT = "localhost";
    private static final String SERVER_PORT_DEFAULT = "1139";
    private static final String USER_NAME_DEFAULT   = "jmetest";
   
    /**
     * The desired frame rate
     */
    private int desiredFrameRate = 30;
    
    /**
     * The width and height of our 3D window
     */
    private int width = 800;
    private int height = 600;
    
    private static WorldManager worldManager;
    
    public JmeClientMain(String[] args) {
        props = loadProperties("run-client.properties");
   
        String serverName = props.getProperty(SERVER_NAME_PROP,
                                              SERVER_NAME_DEFAULT);
        String serverPort = props.getProperty(SERVER_PORT_PROP,
                                              SERVER_PORT_DEFAULT);
        String userName   = props.getProperty(USER_NAME_PROP,
                                              USER_NAME_DEFAULT);
        
        
        worldManager = new WorldManager("Wonderland");
        
        ClientManager clientManager = new ClientManager(serverName, Integer.parseInt(serverPort), userName);
        
        // Low level Federation testing
//        ClientManager clientManager2 = new ClientManager(serverName, Integer.parseInt(serverPort), userName+"2");
        
        processArgs(args);
        worldManager.getRenderManager().setDesiredFrameRate(desiredFrameRate);
        
        createUI(worldManager);  
    }
    
    static WorldManager getWorldManager() {
        return worldManager;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JmeClientMain worldTest = new JmeClientMain(args);
        
    }
    
    /**
     * Process any command line args
     */
    private void processArgs(String[] args) {
        for (int i=0; i<args.length;i++) {
            if (args[i].equals("-fps")) {
                desiredFrameRate = Integer.parseInt(args[i+1]);
                System.out.println("DesiredFrameRate: " + desiredFrameRate);
                i++;
            }
        }
    }
    
    /**
     * Create all of the Swing windows - and the 3D window
     */
    private void createUI(WorldManager wm) {             
        MainFrame frame = new MainFrame(wm, width, height);
        // center the frame
        frame.setLocationRelativeTo(null);

	/* TODO: not yet
	// Initialize the input manager
	// TODO: CameraComponent cameraComp = ViewManager.getCameraComponent();
	CameraComponent cameraComp = null;
	InputManager3D.getInputManager().initialize(frame.getCanvas(), cameraComp);
	*/

        // show frame
        frame.setVisible(true);
    }
    
    private static Properties loadProperties(String fileName) {
        // start with the system properties
        Properties props = new Properties(System.getProperties());
    
        // load the given file
        if (fileName != null) {
            try {
                props.load(new FileInputStream(fileName));
            } catch (IOException ioe) {
                Logger.getLogger(JmeClientMain.class.getName()).log(Level.WARNING, "Error reading properties from " +
                           fileName, ioe);
            }
        }
        
        return props;
    }
    
//    class NodeMoveListener implements GeometricUpdateListener {
//
//        private ClientManager clientManager;
//        
//        public NodeMoveListener(ClientManager clientManager) {
//            this.clientManager = clientManager;
//        }
//        
//        public void geometricDataChanged(Spatial arg0) {
//            clientManager.nodeMoved(arg0);
//        }
//        
//    }
}