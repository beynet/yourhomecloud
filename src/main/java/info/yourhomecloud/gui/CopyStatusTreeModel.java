/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.nio.file.Paths;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author beynet
 */
public class CopyStatusTreeModel implements TreeModel {

    CopyStatusElement root;

    public CopyStatusTreeModel() {
        root = new CopyStatusElement(Paths.get("/"));
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((CopyStatusElement) parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((CopyStatusElement) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((CopyStatusElement) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
