/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.YourHomeCloud;
import info.yourhomecloud.configuration.Configuration;
import java.util.Observable;
import java.util.Observer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
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
//        } else  else if (Configuration.Change.NETWORK_INTERFACE.equals(change)) {
//            /* Create and display the form */
//            java.awt.EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    ((NetworkStatus) networkStatus).updateInterface();
//                    ((NetworkStatus) networkStatus).generateText();
//                }
//            });
//        } else if (Configuration.Change.OTHER_HOSTS.equals(change)) {
//            /* Create and display the form */
//            java.awt.EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    ((NetworkStatus) networkStatus).updateOtherHosts();
//                    ((NetworkStatus) networkStatus).generateText();
//                }
//            });
//        } else if (Configuration.Change.DIRECTORIES_TO_BE_SAVED.equals(change)) {
//            /* Create and display the form */
//            java.awt.EventQueue.invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    pathsToBeSaved.setModel(new DirectoriesToBeBackupedModel());
//                }
//            });
//
//        }
//    }
    private void quitApp() {
        System.out.println("exiting !!!!!!!!!!");
        currentStage.close();
        YourHomeCloud.quitApplication();
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

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu menu = new Menu("yourhomecloud");
        menuBar.getMenus().add(menu);


        // menu item to quit application
        MenuItem quit = MenuItemBuilder.create().text("Quit").accelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN)).build();
        menu.getItems().add(quit);

        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                quitApp();
            }
        });

        // menu item to change current host name
        MenuItem changeHostName = new MenuItem("change host name");
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



        GridPane gridPane = new GridPane();

        group.getChildren().add(menuBar);
        group.getChildren().add(gridPane);

        currentScene = new Scene(group, 640, 480);

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
}
