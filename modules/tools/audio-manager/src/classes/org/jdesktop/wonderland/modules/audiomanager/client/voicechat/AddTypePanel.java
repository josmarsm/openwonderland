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
package org.jdesktop.wonderland.modules.audiomanager.client.voicechat;

import java.awt.event.ActionListener;

/**
 *
 * @author nsimpson
 */
public class AddTypePanel extends javax.swing.JPanel {

    public AddTypePanel() {
        initComponents();
    }

    public void setPhoneType() {
	phoneUserRadioButton.setSelected(true);
    }

    public void addUserModeListener(ActionListener listener) {
        userRadioButton.addActionListener(listener);
    }

    public void removeUserModeListener(ActionListener listener) {
        userRadioButton.removeActionListener(listener);
    }

    public void addPhoneModeListener(ActionListener listener) {
        phoneUserRadioButton.addActionListener(listener);
    }

    public void removePhoneModeListener(ActionListener listener) {
        phoneUserRadioButton.removeActionListener(listener);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        addLabel = new javax.swing.JLabel();
        userRadioButton = new javax.swing.JRadioButton();
        phoneUserRadioButton = new javax.swing.JRadioButton();

        addLabel.setFont(addLabel.getFont().deriveFont(addLabel.getFont().getStyle() | java.awt.Font.BOLD));
        addLabel.setText("Add:");
        addLabel.setName("addLabel"); // NOI18N

        buttonGroup1.add(userRadioButton);
        userRadioButton.setFont(userRadioButton.getFont());
        userRadioButton.setSelected(true);
        userRadioButton.setText("Wonderland User");
        userRadioButton.setName("userRadioButton"); // NOI18N
        userRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(phoneUserRadioButton);
        phoneUserRadioButton.setFont(phoneUserRadioButton.getFont());
        phoneUserRadioButton.setText("Phone User");
        phoneUserRadioButton.setName("phoneUserRadioButton"); // NOI18N
        phoneUserRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneUserRadioButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(addLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(userRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(phoneUserRadioButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addLabel)
                    .add(userRadioButton)
                    .add(phoneUserRadioButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void userRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userRadioButtonActionPerformed
}//GEN-LAST:event_userRadioButtonActionPerformed

    private void phoneUserRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneUserRadioButtonActionPerformed
}//GEN-LAST:event_phoneUserRadioButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton phoneUserRadioButton;
    private javax.swing.JRadioButton userRadioButton;
    // End of variables declaration//GEN-END:variables
}