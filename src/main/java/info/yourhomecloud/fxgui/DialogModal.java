/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
