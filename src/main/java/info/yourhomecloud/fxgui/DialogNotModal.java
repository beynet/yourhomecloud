/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author beynet
 */
public abstract class DialogNotModal extends Dialog {
    public DialogNotModal(Stage parent,double with,double height) {
        super(parent, with, height);
        initModality(Modality.NONE);
    }
}
