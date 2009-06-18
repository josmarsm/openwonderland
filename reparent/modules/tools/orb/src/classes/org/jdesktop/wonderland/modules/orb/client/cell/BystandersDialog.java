/*
 * BystandersDialog.java
 *
 * Created on June 11, 2009, 11:08 AM
 */

package org.jdesktop.wonderland.modules.orb.client.cell;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener.ChangeType;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 *
 * @author  jp
 */
public class BystandersDialog extends javax.swing.JFrame implements BystandersListener,
	PresenceManagerListener {

    private OrbCell orbCell;
    private PresenceManager pm;

    public BystandersDialog() {
    }

    /** Creates new form BystandersDialog */
    public BystandersDialog(OrbCell orbCell, PresenceManager pm) {
	this.orbCell = orbCell;
	this.pm = pm;

        initComponents();

	pm.addPresenceManagerListener(this);

	setTitle("Bystanders for " + orbCell.getUsername());

	orbCell.setBystandersListener(this);

	setBystanders(orbCell.getBystanders());
    }

    private ArrayList<PresenceInfo> bystanders = new ArrayList();

    public void setBystanders(String[] bystanderCallIDArray) {
	ArrayList<PresenceInfo> bystanders = new ArrayList();

	for (int i = 0; i < bystanderCallIDArray.length; i++) {
	    PresenceInfo info = pm.getPresenceInfo(bystanderCallIDArray[i]);

	    if (info == null) {
		System.out.println("No presence info for callID " + bystanderCallIDArray[i]);
		continue;
	    }

	    bystanders.add(info);
	}

	this.bystanders = bystanders;
	setBystanders();
    }

    private void setBystanders() {
	ArrayList<String> bystandersNames = new ArrayList();

	for (PresenceInfo info : bystanders) {
	    if (info.callID == null) {
                // It's a virtual player, skip it.
                continue;
            }

	    String usernameAlias = NameTagNode.getDisplayName(info.usernameAlias, info.isSpeaking,
                info.isMuted);

	    bystandersNames.add(usernameAlias);
	}

	String[] bystandersArray = bystandersNames.toArray(new String[0]);

        Arrays.sort(bystandersArray, new Comparator<String>() {
            public int compare(String s1, String s2) {
                if (s1.startsWith(NameTagNode.LEFT_MUTE)) {
                    s1 = s1.substring(1);
                }

                if (s2.startsWith(NameTagNode.LEFT_MUTE)) {
                    s2 = s2.substring(1);
                }

                return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
            }
        });

	bystandersList.setListData(bystandersArray);
    }

    public void presenceInfoChanged(PresenceInfo info, ChangeType type) {
	if (bystanders.contains(info) == false) {
	    return;
	}

	bystanders.remove(info);
	bystanders.add(info);
        setBystanders();
    }

    public void usernameAliasChanged(PresenceInfo info) {
	if (bystanders.contains(info) == false) {
	    return;
	}

	bystanders.remove(info);
	bystanders.add(info);
        setBystanders();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        bystandersList = new javax.swing.JList();

        bystandersList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(bystandersList);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BystandersDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList bystandersList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
