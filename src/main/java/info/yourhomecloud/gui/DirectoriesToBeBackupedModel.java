/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import info.yourhomecloud.configuration.Configuration;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;

/**
 *
 * @author beynet
 */
public class DirectoriesToBeBackupedModel extends AbstractListModel<String> {
     
    public DirectoriesToBeBackupedModel() {
        
    }
    @Override
    public int getSize() {
        return Configuration.getConfiguration().getDirectoriesToBeSaved().size();
    }

    @Override
    public String getElementAt(int index) {
        return Configuration.getConfiguration().getDirectoriesToBeSaved().get(index);
    }
    
}
