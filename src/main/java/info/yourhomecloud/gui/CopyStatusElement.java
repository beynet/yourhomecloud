/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author beynet
 */
public class CopyStatusElement {

    public CopyStatusElement(Path path, boolean completed) {
        this.path = path.toAbsolutePath().normalize();
        this.completed = completed;
    }

    public void setCompleted(boolean  completed) {
        this.completed = completed;
    }
    public boolean getCompleted() {
        return this.completed;
    }
    
    boolean isLeaf() {
        return !Files.isDirectory(this.path);
    }

    CopyStatusElement getChild(int index) {
        return childs.get(index);
    }
    

    int getChildCount() {
        return childs.size();
    }

    CopyStatusElement addElement(Path child,boolean completed) {
        child = child.toAbsolutePath().normalize();
        return _addElement(child,completed);
    }
    
    CopyStatusElement _addElement(Path child,boolean completed) {
        for (CopyStatusElement el : childs) {
            if (el.path.equals(child)) {
                return el;
            }
            if (child.toString().startsWith(el.path.toString())) {
                return el._addElement(child,completed);
            }
        }
        CopyStatusElement n = new CopyStatusElement(child, completed);
        childs.add(n);
        return n;
    }

    @Override
    public String toString() {
        return this.path.toString();
    }
    private List<CopyStatusElement> childs = new ArrayList<>();
    private Path path;
    private boolean completed;

    int getIndexOfChild(CopyStatusElement copyStatusElement) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
