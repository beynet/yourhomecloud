/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author beynet
 */
public abstract class DialogModal extends Dialog {
    public DialogModal(Stage parent,double with,double height) {
        super(parent, with, height);
        initModality(Modality.APPLICATION_MODAL);
    }

}
