/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.HostConfigurationBean;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 *
 * @author beynet
 */
public class HostCell extends ListCell<HostConfigurationBean>{

    @Override
    protected void updateItem(HostConfigurationBean t, boolean bln) {
        super.updateItem(t, bln);
        if (t!=null) {
            Label label = new Label(t.getHostName()+" "+t.getHostKey());
            setGraphic(label);
        }
    }
    
}
