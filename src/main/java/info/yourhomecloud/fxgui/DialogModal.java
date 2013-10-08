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
public abstract class DialogModal extends Stage {
    public DialogModal(Stage parent,double with,double height) {
        initModality(Modality.APPLICATION_MODAL);
        this.parent = parent;
        root = new Group();
        scene = new Scene(root, with, height);
        setScene(scene);
    }
    
    protected final Scene getCurrentScene() {
        return scene;
    }
    
    protected final Group getRootGroup() {
        return root;
    }
    
    protected final Stage getParentStage() {
        return parent;
    }
    
    private Group root;
    private Stage parent ;
    private Scene scene ;
}
