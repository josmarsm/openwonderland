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

import java.util.StringTokenizer;

/**
 *
 * @author dj
 */
public class RunXAppDialog extends javax.swing.JDialog {

    private boolean cancelled;
    private String command;
    private String appName;
    private boolean addToPalette = false;

    /** Creates new form CellFactoryRunApp */
    public RunXAppDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

        appNameLabel = new javax.swing.JLabel();
        appNameTextField = new javax.swing.JTextField();
        commandLabel = new javax.swing.JLabel();
        commandTextField = new javax.swing.JTextField();
        rememberCheckBox = new javax.swing.JCheckBox();
        runButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        appNameLabel.setFont(appNameLabel.getFont());
        appNameLabel.setText("App Name:");

        commandLabel.setFont(commandLabel.getFont());
        commandLabel.setText("Command:");

        rememberCheckBox.setFont(rememberCheckBox.getFont());
        rememberCheckBox.setText("Add entry to the Cell Palette");

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, commandLabel)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, appNameLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(appNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                            .add(commandTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                            .add(rememberCheckBox)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(runButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(appNameLabel)
                    .add(appNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(commandLabel)
                    .add(commandTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rememberCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(runButton)
                    .add(cancelButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
                                        
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cancelled = true;
        appName = null;
        command = null;
        addToPalette = false;
        dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        cancelled = false;
        appName = fetchAppName();
        command = appNameTextField.getText();
        addToPalette = rememberCheckBox.isSelected();
        dispose();
    }//GEN-LAST:event_runButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appNameLabel;
    private javax.swing.JTextField appNameTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel commandLabel;
    private javax.swing.JTextField commandTextField;
    private javax.swing.JCheckBox rememberCheckBox;
    private javax.swing.JButton runButton;
    // End of variables declaration//GEN-END:variables

    public boolean succeeded () {
        if (cancelled) return false;
        command = commandTextField.getText();
        return command != null && command.trim().length() > 0;
    }

    /**
     * Returns the app name entered into the dialog.
     *
     * @return The String app name to run
     */
    public String getAppName() {
        return appName;
    }
    
    /**
     * Returns the command entered into the dialog.
     * 
     * @return The String command to run
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns true if the app should be added to the Cell Palette for future
     * use.
     *
     * @return True if the app should be added to the Cell Palette
     */
    public boolean isAddToCellPalette() {
        return addToPalette;
    }
    
    /**
     * Figures out what the app name should be based upon a combination of the
     * app name text field and the command name (if the former is null).
     */
    private String fetchAppName () {
        String appNameStr = appNameTextField.getText();
        if (appNameStr != null && appNameStr.trim().length() > 0) {
            return appNameStr;
        } else {
            if (command == null || command.trim().length() <= 0) {
                return "Unknown";
            }
            
            StringTokenizer tok = new StringTokenizer(command);
            if (tok.countTokens() <= 0) {
                return "Unknown";
            }

            // Return the first token
            return tok.nextToken();
        }
    }
}
