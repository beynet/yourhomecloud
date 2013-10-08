/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.apache.log4j.Logger;

/**
 *
 * @author beynet
 */
public class HostSelector extends DialogModal {
    public HostSelector(Stage parent,boolean allowRemove) {
        super(parent,200,300);
        GridPane bp = new GridPane();
        bp.setPadding(new Insets(5));
        bp.setHgap(5);
        Label hostsLabel = new Label("host list");
        bp.add(hostsLabel, 0, 0,1,1);
        
        setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                exit();
            }
        });
        
        hosts = FXCollections.observableArrayList();
        hosts.addAll(Configuration.getConfiguration().getOtherHostsSnapshot());
        hostsListView  = new ListView<>(hosts);
        hostsListView.setCellFactory(new Callback<ListView<HostConfigurationBean>, 
            ListCell<HostConfigurationBean>>() {
                @Override
                public ListCell<HostConfigurationBean> call(ListView<HostConfigurationBean> list) {
                    return new HostCell();
                }
            }
        );
        hostsListView.setPrefWidth(130);
        hostsListView.setPrefHeight(270);
        bp.add(hostsListView, 1, 0,2,5);
        
        getRootGroup().getChildren().add(bp);
        this.allowRemove = allowRemove;
        if (this.allowRemove==true) {
            hostsListView.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (KeyCode.DELETE.equals(t.getCode())) {
                        int selectedIndex = hostsListView.getSelectionModel().getSelectedIndex();
                        if (selectedIndex>=0) {
                            HostConfigurationBean selectedHost = hosts.get(selectedIndex);
                            try {
                                Configuration.getConfiguration().removeHost(selectedHost.getHostKey());
                            } catch (IOException ex) {
                                logger.error("unable to remove host name="+selectedHost.getHostName()+" key="+selectedHost.getHostKey());
                            }
                        }
                    }
                }
            });
        }
        
        obs = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                configurationChanged((Configuration) o, (Configuration.Change) arg);
            }
            
        };
        Configuration.getConfiguration().addObserver(obs);
    }

    private void configurationChanged(Configuration configuration, Configuration.Change change) {
        if (Configuration.Change.OTHER_HOSTS.equals(change)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    hosts.clear();
                    hosts.addAll(Configuration.getConfiguration().getOtherHostsSnapshot());
                }
            });
            
        }
    }
    
    
    private void exit() {
        getParentStage().setOpacity(1);
        Configuration.getConfiguration().deleteObserver(obs);
    }
    
    private ListView<HostConfigurationBean> hostsListView;
    private ObservableList<HostConfigurationBean> hosts ;
    private boolean allowRemove;
    private Logger logger = Logger.getLogger(HostSelector.class);
    private Observer obs;
}
