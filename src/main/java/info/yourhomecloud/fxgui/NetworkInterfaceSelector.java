/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.NetworkUtils;
import java.io.IOException;
import java.net.NetworkInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author beynet
 */
public class NetworkInterfaceSelector extends DialogModal {
    public NetworkInterfaceSelector(Stage parent) {
        super(parent,160,120);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setVgap(5);
        grid.setVgap(5);
        grid.prefWidthProperty().bind(getCurrentScene().widthProperty());
        getRootGroup().getChildren().add(grid);
        setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                exit();
            }
        });
        try {
            networkInterfaces = FXCollections.observableArrayList();
            networkInterfaces.addAll(NetworkUtils.getInterfaces());
        } catch (IOException ex) {
            throw new RuntimeException("unable to retrieve network interfaces", ex);
        }
        networkInterfaceList = new ListView<>(networkInterfaces);
        networkInterfaceList.setPrefHeight(80);
        networkInterfaceList.prefWidthProperty().bind(grid.widthProperty());
        grid.add(networkInterfaceList, 0, 0,3,1);
        
        Button select = new Button("select");
        select.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                int index = networkInterfaceList.getSelectionModel().getSelectedIndex();
                if (index>=0) {
                    NetworkInterface selected = networkInterfaces.get(index);
                    Configuration.getConfiguration().setNetworkInterface(selected.getName());
                }
                exit();
            }
        });
        GridPane.setHalignment(select, HPos.CENTER);
        grid.add(select, 2,1);
    }
    
    private void exit() {
        getParentStage().setOpacity(1);
        close();
    }
    
    private ObservableList<NetworkInterface> networkInterfaces ;
    private ListView<NetworkInterface> networkInterfaceList;
}
