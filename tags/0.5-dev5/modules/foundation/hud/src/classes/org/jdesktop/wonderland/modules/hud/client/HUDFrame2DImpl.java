/*
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

/*
 * HUDFrame2DImpl.java
 *
 * Created on Feb 25, 2009, 2:22:20 PM
 */
package org.jdesktop.wonderland.modules.hud.client;

/**
 *
 * @author nsimpson
 */
public class HUDFrame2DImpl extends javax.swing.JPanel {

    /** Creates new form HUDFrame2DImpl */
    public HUDFrame2DImpl() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlsPanel = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        minimizeButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        grabPanel = new javax.swing.JPanel();
        grabButton = new javax.swing.JButton();

        controlsPanel.setMinimumSize(new java.awt.Dimension(100, 70));

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/close16x16.png"))); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setIconTextGap(0);
        closeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        closeButton.setMaximumSize(new java.awt.Dimension(16, 16));
        closeButton.setMinimumSize(new java.awt.Dimension(2, 2));

        minimizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/minimize16x16.png"))); // NOI18N
        minimizeButton.setBorderPainted(false);
        minimizeButton.setIconTextGap(0);
        minimizeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        minimizeButton.setMaximumSize(new java.awt.Dimension(16, 16));
        minimizeButton.setMinimumSize(new java.awt.Dimension(2, 2));

        org.jdesktop.layout.GroupLayout controlsPanelLayout = new org.jdesktop.layout.GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
            controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlsPanelLayout.createSequentialGroup()
                .add(controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(minimizeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        controlsPanelLayout.setVerticalGroup(
            controlsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlsPanelLayout.createSequentialGroup()
                .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(minimizeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(11, 11, 11))
        );

        contentPanel.setBackground(new java.awt.Color(255, 255, 255));
        contentPanel.setMinimumSize(new java.awt.Dimension(100, 70));

        org.jdesktop.layout.GroupLayout contentPanelLayout = new org.jdesktop.layout.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 373, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 51, Short.MAX_VALUE)
        );

        grabButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/grab19x500.png"))); // NOI18N
        grabButton.setBorderPainted(false);
        grabButton.setIconTextGap(0);
        grabButton.setMaximumSize(new java.awt.Dimension(30, 512));
        grabButton.setMinimumSize(new java.awt.Dimension(30, 30));
        grabButton.setPreferredSize(new java.awt.Dimension(30, 35));
        grabButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grabButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout grabPanelLayout = new org.jdesktop.layout.GroupLayout(grabPanel);
        grabPanel.setLayout(grabPanelLayout);
        grabPanelLayout.setHorizontalGroup(
            grabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(grabButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
        );
        grabPanelLayout.setVerticalGroup(
            grabPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(grabButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(grabPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(contentPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(controlsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(grabPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(contentPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, Short.MAX_VALUE)
            .add(controlsPanel, 0, 51, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void grabButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grabButtonActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_grabButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JButton grabButton;
    private javax.swing.JPanel grabPanel;
    private javax.swing.JButton minimizeButton;
    // End of variables declaration//GEN-END:variables
}