/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.YourHomeCloud;
import info.yourhomecloud.configuration.Configuration;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.BasicConfigurator;

/**
 *
 * @author beynet
 */
public class MainFXApp extends Application {

    public MainFXApp() {
        Configuration.getConfiguration().addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                configurationChanged((Configuration) o, (Configuration.Change) arg);
            }
        });
    }
    
    protected void setTitle() {
        currentStage.setTitle("yourhomecloud (" + Configuration.getConfiguration().getCurrentHostName() + ")");
    }
    
    protected void configurationChanged(Configuration conf, Configuration.Change change) {
        if (Configuration.Change.HOSTNAME.equals(change)) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setTitle();
                }
            });
        }
        else if (Configuration.Change.DIRECTORIES_TO_BE_SAVED.equals(change)) {
            /* Create and display the form */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pathToBeSaved.setListContent(Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot());
                }
            });

        }
        else if (Configuration.Change.NETWORK_INTERFACE.equals(change)) {
            /* Create and display the form */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ((NetworkStatus) networkStatus).updateInterface();
                    ((NetworkStatus) networkStatus).generateText();
                }
            });
        }
        else if (Configuration.Change.OTHER_HOSTS.equals(change)) {
            /* Create and display the form */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
//                    ((NetworkStatus) networkStatus).updateOtherHosts();
//                    ((NetworkStatus) networkStatus).generateText();
                }
            });
        } 
        
    }

//    protected void configurationChanged(Configuration conf, Configuration.Change change) {
//        if (Configuration.Change.MAIN_HOST.equals(change)) {
//            /* Create and display the form */
//            java.awt.EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    ((NetworkStatus) networkStatus).updateMainHost();
//                    ((NetworkStatus) networkStatus).generateText();
//                }
//            });
//        } else   else if (Configuration.Change.OTHER_HOSTS.equals(change)) {
//            /* Create and display the form */
//            java.awt.EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    ((NetworkStatus) networkStatus).updateOtherHosts();
//                    ((NetworkStatus) networkStatus).generateText();
//                }
//            });
//        } 
//    }
    private void quitApp() {
        System.out.println("exiting !!!!!!!!!!");
        currentStage.close();
        YourHomeCloud.quitApplication();
    }
    
    
    private void prepareMenuBar(BorderPane borderPane) {
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(currentStage.widthProperty());

        Menu menu = new Menu("yourhomecloud");
        menuBar.getMenus().add(menu);

        // menu item to change current host name
        MenuItem changeHostName = new MenuItem("change current host name");
        menu.getItems().add(changeHostName);
        changeHostName.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                currentStage.setOpacity(0.5);
                ChangeHostName ch = new ChangeHostName(currentStage);
                ch.sizeToScene();
                ch.show();
            }
        });
        
        
        // menu item to show directory chooser
        MenuItem showDirectoryChooser = new MenuItem("select new directory to be backuped");
        menu.getItems().add(showDirectoryChooser);
        showDirectoryChooser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                currentStage.setOpacity(0.5);
                DirectoryChooser d = new DirectoryChooser();
                File showDialog = d.showDialog(currentStage);
                currentStage.setOpacity(1);
                if (showDialog!=null) {
                    Configuration.getConfiguration().addDirectoryToBeSaved(showDialog.toPath());
                }
            }
        });
        
        // menu item to show directory chooser
        {
            MenuItem networkInteface = new MenuItem("select network interface");
            menu.getItems().add(networkInteface);
            networkInteface.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    currentStage.setOpacity(0.5);
                    NetworkInterfaceSelector networkInterfaceSelector = new NetworkInterfaceSelector(currentStage);
                    networkInterfaceSelector.show();
                }
            });
        }
        
        // menu item to show directory chooser
        {
            MenuItem hostSelector = new MenuItem("show known hosts list");
            menu.getItems().add(hostSelector);
            hostSelector.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    currentStage.setOpacity(0.5);
                    HostSelector hostsList = new HostSelector(currentStage,true);
                    hostsList.show();
                }
            });
        }
        
        
        // menu item to quit application
        MenuItem quit = MenuItemBuilder.create().text("Quit").accelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN)).build();
        menu.getItems().add(quit);

        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                quitApp();
            }
        });
        
        borderPane.setTop(menuBar);
    }

    private void prepareContent(BorderPane borderPane) {
        // the hbox to contain the components
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(5);
        vbox.getStyleClass().add("noborder");
        borderPane.setCenter(vbox);
        
        
        // network status
        networkStatus = new NetworkStatus();
        PaneBorderedWithTitle networkStatusPane = new PaneBorderedWithTitle(networkStatus, "network status");        
        networkStatusPane.setPrefHeight(200);
        
        // Directories backuped
        pathToBeSaved = new PathToBeSavedList(FXCollections.observableArrayList(Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot()));
        PaneBorderedWithTitle directories = new PaneBorderedWithTitle(pathToBeSaved, "directories to be backuped");
        directories.setPrefHeight(200);
        
        
        vbox.getChildren().addAll(networkStatusPane,directories);
    }
    
    @Override
    public void start(final Stage primaryStage) throws Exception {
        currentStage = primaryStage;
        currentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                quitApp();
            }
        });
        setTitle();
        
        Group group = new Group();
        final BorderPane borderPane = new BorderPane();
        group.getChildren().add(borderPane);
        
        // setup the menu bar
        prepareMenuBar(borderPane);

        // setup content
        prepareContent(borderPane);
        currentScene = new Scene(group, 640, 480);
        currentScene.getStylesheets().add(getClass().getResource("/default.css").toExternalForm());
        primaryStage.setScene(currentScene);
        primaryStage.show();
        
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.TRACE);
        YourHomeCloud.initConfiguration();
        launch(args);
    }
    private Scene currentScene;
    private Stage currentStage;
    private PathToBeSavedList pathToBeSaved;
    private NetworkStatus networkStatus;
}
