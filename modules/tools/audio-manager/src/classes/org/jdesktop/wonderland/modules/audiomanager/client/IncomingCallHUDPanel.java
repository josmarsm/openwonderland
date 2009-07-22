/*
 * IncomingCallHUDPanel.java
 *
 * Created on July 19, 2009, 11:53 AM
 */

package org.jdesktop.wonderland.modules.audiomanager.client;

import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent.ComponentEventType;
import org.jdesktop.wonderland.client.hud.HUDComponentListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatBusyMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinAcceptedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author  jp
 */
public class IncomingCallHUDPanel extends javax.swing.JPanel {
    
    private static final Logger logger =
            Logger.getLogger(IncomingCallHUDPanel.class.getName());
    private ChatType chatType = ChatType.PRIVATE;
    private AudioManagerClient client;
    private WonderlandSession session;
    private CellID cellID;
    private String group;
    private PresenceInfo caller;
    private PresenceInfo myPresenceInfo;

    private HUDComponent incomingCallHUDComponent;

    /** Creates new form IncomingCallHUDPanel */
    public IncomingCallHUDPanel(AudioManagerClient client, WonderlandSession session,
            CellID cellID, VoiceChatJoinRequestMessage message) {

        initComponents();

        this.client = client;
        this.cellID = cellID;
        this.session = session;

        initComponents();

        group = message.getGroup();

        caller = message.getCaller();

        callerText.setText(caller.usernameAlias);

        PresenceManager pm = PresenceManagerFactory.getPresenceManager(session);

        myPresenceInfo = pm.getPresenceInfo(cellID);

	privacyDescription.setText(VoiceChatMessage.PRIVATE_DESCRIPTION);
    }
    
    public void setHUDComponent(HUDComponent incomingCallHUDComponent) {
	this.incomingCallHUDComponent = incomingCallHUDComponent;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        secretRadioButton = new javax.swing.JRadioButton();
        privateRadioButton = new javax.swing.JRadioButton();
        speakerPhoneRadioButton = new javax.swing.JRadioButton();
        ignoreButton = new javax.swing.JButton();
        BusyButton = new javax.swing.JButton();
        AnswerButton = new javax.swing.JButton();
        callerText = new javax.swing.JLabel();
        privacyDescription = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        jLabel1.setText("Incoming call from:");

        buttonGroup1.add(secretRadioButton);
        secretRadioButton.setText("Secret");
        secretRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secretRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(privateRadioButton);
        privateRadioButton.setSelected(true);
        privateRadioButton.setText("Private");
        privateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(speakerPhoneRadioButton);
        speakerPhoneRadioButton.setText("speakerPhone");
        speakerPhoneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speakerPhoneRadioButtonActionPerformed(evt);
            }
        });

        ignoreButton.setText("Ignore");
        ignoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreButtonActionPerformed(evt);
            }
        });

        BusyButton.setText("Busy");
        BusyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BusyButtonActionPerformed(evt);
            }
        });

        AnswerButton.setText("Answer");
        AnswerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnswerButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(callerText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(ignoreButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(BusyButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(AnswerButton))
                            .add(layout.createSequentialGroup()
                                .add(secretRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(privateRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(speakerPhoneRadioButton))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, privacyDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(callerText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(secretRadioButton)
                    .add(privateRadioButton)
                    .add(speakerPhoneRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(privacyDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(AnswerButton)
                    .add(BusyButton)
                    .add(ignoreButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void secretRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secretRadioButtonActionPerformed
	chatType = ChatType.SECRET;
	privacyDescription.setText(VoiceChatMessage.SECRET_DESCRIPTION);
}//GEN-LAST:event_secretRadioButtonActionPerformed

    private void privateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateRadioButtonActionPerformed
	chatType = ChatType.PRIVATE;
	privacyDescription.setText(VoiceChatMessage.PUBLIC_DESCRIPTION);
}//GEN-LAST:event_privateRadioButtonActionPerformed

    private void speakerPhoneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speakerPhoneRadioButtonActionPerformed
	chatType = ChatType.PUBLIC;
}//GEN-LAST:event_speakerPhoneRadioButtonActionPerformed

    private void ignoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreButtonActionPerformed
        incomingCallHUDComponent.setVisible(false);
    }//GEN-LAST:event_ignoreButtonActionPerformed

    private void BusyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BusyButtonActionPerformed
        session.send(client, new VoiceChatBusyMessage(group, caller, myPresenceInfo, chatType));

        incomingCallHUDComponent.setVisible(false);
    }//GEN-LAST:event_BusyButtonActionPerformed

    private HUDComponent inCallHUDComponent;

    private void AnswerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnswerButtonActionPerformed
        logger.info("Sent join message");

        InCallHUDPanel inCallHUDPanel = InCallHUDPanel.getInCallHUDPanel(group);

        if (inCallHUDPanel == null) {
            inCallHUDPanel = new InCallHUDPanel(client, session, myPresenceInfo, caller, group);

            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            inCallHUDComponent = mainHUD.createComponent(inCallHUDPanel);

            inCallHUDPanel.setHUDComponent(inCallHUDComponent);

            inCallHUDComponent.setPreferredLocation(Layout.NORTHWEST);

            mainHUD.addComponent(inCallHUDComponent);

            inCallHUDComponent.addComponentListener(new HUDComponentListener() {
                public void HUDComponentChanged(HUDComponentEvent e) {
                    if (e.getEventType().equals(ComponentEventType.CLOSED)) {
			inCallHUDComponent = null;
                    }
                }
            });
        }

        inCallHUDComponent.setVisible(true);

        session.send(client, new VoiceChatJoinAcceptedMessage(group, myPresenceInfo, chatType));

        incomingCallHUDComponent.setVisible(false);
    }//GEN-LAST:event_AnswerButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AnswerButton;
    private javax.swing.JButton BusyButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel callerText;
    private javax.swing.JButton ignoreButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel privacyDescription;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JRadioButton secretRadioButton;
    private javax.swing.JRadioButton speakerPhoneRadioButton;
    // End of variables declaration//GEN-END:variables
    
}
