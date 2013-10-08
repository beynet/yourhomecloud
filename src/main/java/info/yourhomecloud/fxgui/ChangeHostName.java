/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author beynet
 */
public class ChangeHostName extends DialogModal {

    public ChangeHostName(Stage parent) {
        super(parent,250,100);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);
        getRootGroup().getChildren().add(grid);

        hostName = new TextField();
        Label hostNameLabel = new Label("Host Name :");

        Button confirm = new Button("confirm");
        confirm.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                if (hostName.getText()!=null && !"".equals(hostName.getText())) {
                    Configuration.getConfiguration().setCurrentHostName(hostName.getText());
                }
                exit();
            }
        });
        grid.add(hostNameLabel, 0, 0);
        grid.add(hostName, 1, 0);
        grid.add(confirm, 1, 1);
        GridPane.setHalignment(confirm, HPos.RIGHT);
        
        setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                exit();
            }
        });
    }
    
    private void exit() {
        getParentStage().setOpacity(1);
        close();
    }
    
    private final TextField hostName;
}
