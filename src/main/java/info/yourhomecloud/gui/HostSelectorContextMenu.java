/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author beynet
 */
public class HostSelectorContextMenu extends JPopupMenu {

    HostSelectorContextMenu(JList parent, MouseEvent evt) {
        this.parent = parent;
        int locationToIndex = parent.locationToIndex(new Point(evt.getX(),evt.getY()));
        int selected = parent.getSelectedIndex();
        if (selected!=locationToIndex || locationToIndex==-1) {
            return;
        }
        deleteCurrentHost = new JMenuItem("delete");
        add(deleteCurrentHost);
        deleteCurrentHost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedHost(evt);
            }
        });
        this.show(parent, evt.getX(), evt.getY());
    }

    private void deleteSelectedHost(ActionEvent evt) {
        try {
            ((HostsSelectorModel)this.parent.getModel()).deleteSelectedHost(parent.getSelectedIndex());
        } catch (IOException ex) {
           JOptionPane.showMessageDialog(parent, "error when removing host : "+ex.getMessage());
        }
    }
    
    private JList parent;
    private JMenuItem deleteCurrentHost;
    private JMenuItem showSelectedHostConfiguration;
}
