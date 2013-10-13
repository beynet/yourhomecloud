/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

import info.yourhomecloud.configuration.HostConfigurationBean;
import info.yourhomecloud.files.FileSyncer;
import info.yourhomecloud.files.FileSyncerBuilder;
import info.yourhomecloud.gui.HostsSelector;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.hosts.TargetHostBuilder;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.broadcast.BroadcasterListener;
import info.yourhomecloud.network.rmi.RMIUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.swing.*;

/**
 *
 * @author beynet
 */
public class YourHomeCloud extends Application {

    public YourHomeCloud() {
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
                }
            });
        }
        else if (Configuration.Change.OTHER_HOSTS.equals(change)) {
            /* Create and display the form */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ((NetworkStatus) networkStatus).updateOtherHosts();
                }
            });
        }
        else if (Configuration.Change.MAIN_HOST.equals(change)) {
            /* Create and display the form */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ((NetworkStatus) networkStatus).updateMainHost();
                    nst.updateNetworkStatus();
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
        quitApplication();
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
        
        // menu item to show network interface selector
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

        // start a sync
        
        // menu item to show known host list
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

        {
            MenuItem scanNetwork = new MenuItem("scan network");
            menu.getItems().add(scanNetwork);
            scanNetwork.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        final BroadcasterListener broadcasterListener = new BroadcasterListener(NetworkUtils.DEFAULT_BROADCAST_PORT);
                        final Thread thread = new Thread(broadcasterListener);
                        thread.start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        // start to sync files to selected hosts
        {
            MenuItem syncFiles = new MenuItem("start sync files");
            menu.getItems().add(syncFiles);
            syncFiles.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    HostConfigurationBean selectedHost = networkStatus.getSelectedHost();
//                    if (selectedHost !=null) {
                        startSync(selectedHost);
//                    }
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

    
    private void prepareNetworkStatus(BorderPane borderPane) {
        nst = new NetworkShortStatus();
        borderPane.setBottom(nst);
    }
    
    private void prepareContent(BorderPane borderPane) {
        // the hbox to contain the components
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20));
        vbox.setSpacing(5);
        vbox.getStyleClass().add(Styles.NO_BORDER);
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

    private void startSync(HostConfigurationBean host) {
        // start to sync local host directories on the selected target host
        // ----------------------------------------------------------------
        final FileSyncer fs = FileSyncerBuilder.createMonodirectionalFileSyncer();
        final TargetHost targetHost;
        try {
            targetHost = TargetHostBuilder.createRMITargetHost(host.getHostKey(), host.getCurrentRMIAddress(), host.getCurrentRMIPort());
        } catch (IOException ex) {
            new Alert(currentStage,"unable to obtain remote proxy error=" + ex.getMessage()).show();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                for (String dir : Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot()) {
                    try {
                        fs.sync(Paths.get(dir), targetHost,copyStatus);
                    } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        sw.append("Error during  copy \n");
                        ex.printStackTrace(new PrintWriter(sw));
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                new Alert(currentStage,sw.toString()).show();
                            }
                        });

                    }
                }
            }
        }.start();
        copyStatus.setVisible(true);
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
        
        // add network status
        prepareNetworkStatus(borderPane);
        
        currentScene = new Scene(group, 640, 480);
        currentScene.getStylesheets().add(getClass().getResource("/default.css").toExternalForm());
        primaryStage.setScene(currentScene);
        primaryStage.show();
        
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();

        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.TRACE);
        initConfiguration(args);
        launch(args);
    }

    public static void quitApplication() {
        try {
            Configuration.getConfiguration().onExit();
        } catch (Exception ex) {
            logger.error("unable to send exit", ex);
        }
        System.exit(0);
    }

    /**
     * if a directory is provided use this directory for configuration if not
     * use environment standard
     *
     * @param args
     */
    public static void initConfiguration(String... args) {
        final Path confPath;
        if (args == null || args.length < 1) {
            Path userHome = Paths.get((String) System.getProperty("user.home"));
            confPath=userHome.resolve("yourhomecloud");
        } else {
            confPath = Paths.get(args[0]);
        }
        if (!Files.exists(confPath)) {
            try {
                Files.createDirectories(confPath);
            } catch (IOException e) {
                throw new RuntimeException("unable to create configuration diretory", e);
            }
        }
        System.err.println(confPath.toString());
        Configuration.getConfiguration(confPath);
        RMIUtils.getRMIUtils();
    }

    private Scene currentScene;
    private Stage currentStage;
    private PathToBeSavedList pathToBeSaved;
    private NetworkStatus networkStatus;
    private NetworkShortStatus nst;

    private final static Logger logger = Logger.getLogger(YourHomeCloud.class);
}
