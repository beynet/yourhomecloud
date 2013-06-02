/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelEvent;
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
        root = new CopyStatusElement(Paths.get("/"),true);
    }

    @Override
    public Object getRoot() {
        return root;
    }
    
    public void addFileToBeCopied(Path file,boolean completed) {
        CopyStatusElement addElement = root.addElement(file,completed);
        addElement.setCompleted(completed);
        fireEvent(root);
    }
    
    protected void fireEvent(CopyStatusElement element) {
        TreeModelEvent evt = new TreeModelEvent(this,new Object[] {element});
        for (TreeModelListener tml : listeners) {
            tml.treeStructureChanged(evt);
        }
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
        return ((CopyStatusElement)parent).getIndexOfChild((CopyStatusElement)child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
    
    
    List<TreeModelListener> listeners = new ArrayList<>();
}
