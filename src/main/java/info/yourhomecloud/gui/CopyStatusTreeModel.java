/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
        root = new CopyStatusElement(Paths.get("/"),true,null);
    }

    @Override
    public Object getRoot() {
        return root;
    }
    
    public void addFileToBeCopied(Path file,boolean completed) {
        CopyStatusElement addElement = root.addElement(file,completed);
        addElement.setCompleted(completed);
        fireEvent(addElement);
    }
    
    
    private void createPath(List<CopyStatusElement> paths,CopyStatusElement element) {
        
        if (element.getFather()!=null) createPath(paths, element.getFather());
        paths.add(element);
    }
    
    protected void fireEvent(CopyStatusElement element) {
        List<CopyStatusElement> paths = new ArrayList<>();
        createPath(paths,element.getFather());
        TreeModelEvent evt = new TreeModelEvent(this,paths.toArray());
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
        throw new UnsupportedOperationException("not yet");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent==null||child==null) return -1;
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
