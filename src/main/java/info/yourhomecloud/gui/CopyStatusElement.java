/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author beynet
 */
public class CopyStatusElement {
    
    public CopyStatusElement(Path path) {
        this.path = path;
    }
    
    public List<CopyStatusElement> childs = new ArrayList<>();
    private Path path;

    boolean isLeaf() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    CopyStatusElement getChild(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
