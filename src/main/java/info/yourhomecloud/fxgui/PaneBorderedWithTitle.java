/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author beynet
 */
public class PaneBorderedWithTitle extends VBox {

    public PaneBorderedWithTitle(Node content, String title) {
        Label label = new Label(title);
        label.getStyleClass().add("pane-bordered-title");
        
        content.getStyleClass().add("pane-bordered-content");

        getStyleClass().add("pane-bordered-border");
        
        getChildren().addAll(label,content);
    }
}
