/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import info.yourhomecloud.configuration.Configuration;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * @author beynet
 */
public class DirectoriesToBeBackupedModel extends DefaultListModel<String> {
    public List<String> toBeSaved ;
    public DirectoriesToBeBackupedModel() {
        toBeSaved = Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot();
    }
    @Override
    public int getSize() {
        return toBeSaved.size();
    }

    @Override
    public String getElementAt(int index) {
        return toBeSaved.get(index);
    }
    
}
