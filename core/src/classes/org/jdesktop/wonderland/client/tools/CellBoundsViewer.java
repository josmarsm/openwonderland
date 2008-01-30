/*
 * CellBoundsViewer.java
 *
 * Created on January 29, 2008, 9:36 AM
 */

package org.jdesktop.wonderland.client.tools;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;

/**
 *
 * @author  paulby
 */
public class CellBoundsViewer extends javax.swing.JFrame {
    
    private static final Logger logger = Logger.getLogger(CellBoundsViewer.class.getName());
    
    private BoundsTestClientSession session;
    
    /** Creates new form CellBoundsViewer */
    public CellBoundsViewer() {
        initComponents();
        centerPanel.add(new BoundsPanel(), BorderLayout.CENTER);
        
        WonderlandServerInfo server = new WonderlandServerInfo("localhost", 1139);
        LoginParameters loginParams = new LoginParameters("foo", "test".toCharArray());
        
        // create a session
        session = new BoundsTestClientSession(server);
                
        try {
            session.login(loginParams);
        } catch (LoginFailureException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        centerPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        exitMI = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(640, 480));

        centerPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        exitMI.setText("Exit");
        exitMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIActionPerformed(evt);
            }
        });
        jMenu1.add(exitMI);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMIActionPerformed
    
    
    class BoundsPanel extends JPanel {
        private ArrayList<Cell> cells = new ArrayList<Cell>();
        private Vector3f center = new Vector3f();
        private Vector3f extent = new Vector3f();
        private float scale = 20f;
        private float panelTranslationX = 0f;
        private float panelTranslationY = 0f;
        
        private Point mousePress = null;
        
        public BoundsPanel() {
            cells.add(createTestCell(5,5,2,true));
            cells.add(createTestCell(10,10,3,false));
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    panelTranslationX = e.getX() - mousePress.x;
                    panelTranslationY = e.getY() - mousePress.y;
                    repaint();
                }
            });
            
            addMouseListener(new MouseAdapter() {
                @Override
               public void mousePressed(MouseEvent e) {
                   mousePress = e.getPoint();
                   mousePress.x -= panelTranslationX;
                   mousePress.y -= panelTranslationY;
               } 
            });
            
            addMouseWheelListener(new MouseWheelListener() {

                public void mouseWheelMoved(MouseWheelEvent e) {
                    int rot = e.getWheelRotation();
                    if (rot>0)
                        scale *= rot*1.2;
                    else
                        scale /= -rot*1.2;
                    repaint();
                }
                
            });
        }
        
        private Cell createTestCell(int x, int z, int size, boolean useBox) {
            Cell cell = new Cell();
            
            if (useBox) {
                BoundingBox box = new BoundingBox(new Vector3f(x,0,z), size, size, size);
                cell.setCachedVWBounds(box);
            } else {
                BoundingSphere sphere = new BoundingSphere(size, new Vector3f(x,0,z));
                cell.setCachedVWBounds(sphere);
            }
            
            return cell;
        }
        
        @Override
        public void paint(Graphics gr) {
            Graphics2D g = (Graphics2D)gr;
            g.translate(panelTranslationX, panelTranslationY);
            g.clearRect(0, 0, (int)(getWidth()*scale), (int)(getHeight()*scale));
            
            for(Cell c : cells)
                drawCell(c, g);
        }
        
        private void drawCell(Cell cell, Graphics2D g) {
            drawBounds(cell.getCachedVWBounds(), g);
        }
        
        private void drawBounds(BoundingVolume bounds, Graphics2D g) {
            if (bounds instanceof BoundingBox) {
                BoundingBox box = (BoundingBox)bounds;
                center = box.getCenter(center);
                extent = box.getExtent(extent);
                
                g.drawRect((int)((center.x-extent.x)*scale), 
                           (int)((center.z-extent.z)*scale), 
                           (int)((extent.x*2)*scale), 
                           (int)((extent.z*2)*scale));
            } else if (bounds instanceof BoundingSphere) {
                BoundingSphere sphere = (BoundingSphere)bounds;
                center = sphere.getCenter(center);
                int radius = (int)(sphere.getRadius()*scale);
                
                g.drawOval((int)((center.x)*scale), 
                           (int)((center.z)*scale),
                           radius,
                           radius);
            } else
                System.out.println("Bounds type not supported "+bounds.getClass().getName());
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CellBoundsViewer().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JMenuItem exitMI;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    // End of variables declaration//GEN-END:variables
    
}
