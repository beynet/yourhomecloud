/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author beynet
 */
public class CopyStatusTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (((CopyStatusElement) value).getCompleted() == true) {
            setTextSelectionColor(Color.GREEN);
            setTextNonSelectionColor(Color.GREEN);
        }
        else {
            setTextSelectionColor(Color.RED);
            setTextNonSelectionColor(Color.RED);
        }
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        return this;
    }
}
