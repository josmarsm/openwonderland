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
package org.jdesktop.wonderland.modules.contentrepo.client.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.content.ContentBrowserManager;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;

/**
 *
 * @author jkaplan
 */
@Plugin
public class BrowserPlugin implements ClientPlugin {
    private BrowserFrame frame;

    public void initialize(final ServerSessionManager loginInfo) {
        // Fetch the system content repository based upon the server we are
        // currently connected to.
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        final ContentRepository repo = registry.getRepository(loginInfo);

        Action launchAction = new AbstractAction("Content Browser...") {
            public synchronized void actionPerformed(ActionEvent e) {
                if (frame == null) {
                    frame = new BrowserFrame(repo);
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        frame.setVisible(true);
                    }
                });
            }
        };

        JmeClientMain.getFrame().addToToolMenu(new JMenuItem(launchAction));

        // Registery the content browser frame with the registry of such panels
        ContentBrowserManager manager = ContentBrowserManager.getContentBrowserManager();
        manager.setDefaultContentBrowser(new ContentBrowserJDialog(repo));
    }

}