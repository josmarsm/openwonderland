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

package org.jdesktop.wonderland.client;

import java.util.ServiceLoader;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.ServerManagerClient;
import org.jdesktop.wonderland.client.comms.WonderlandClient;

/**
 *
 * @author  paulby
 */
public class Main extends javax.swing.JFrame {
    private static final Logger logger =
            Logger.getLogger(Main.class.getName());
    
    /** Creates new form Main */
    public Main() {
        // create UI components
        initComponents();

        WonderlandServerInfo server = new WonderlandServerInfo("localhost", 1139);
        LoginParameters loginParams = new LoginParameters("foo", "test".toCharArray());
        WonderlandClient client = new WonderlandClient(server);
        ServerManagerClient mgrClient = new ServerManagerClient(server);

        // initialize plugins
        loadPlugins();
        
        try {
            client.login(loginParams);
            mgrClient.login(loginParams);
        } catch (LoginFailureException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Load client plugins.  Plugins implement the ClientPlugin interface
     * and register using the Java ServiceLoader mechanism.
     */
    private void loadPlugins() {
        // initialize plugins
        ServiceLoader<ClientPlugin> plugins =
                ServiceLoader.load(ClientPlugin.class);
        
        for (ClientPlugin plugin : plugins) {
            logger.info("Initializing plugin: " + plugin);
            plugin.initialize();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMI = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        fileMenu.setText("File");

        exitMI.setText("Exit");
        fileMenu.add(exitMI);

        mainMenuBar.add(fileMenu);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitMI;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar mainMenuBar;
    // End of variables declaration//GEN-END:variables
    
}
