package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import info.yourhomecloud.hosts.File;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.hosts.TargetHostBuilder;
import info.yourhomecloud.network.rmi.FileUtils;
import info.yourhomecloud.utils.FileTools;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * in this view, files present on the remote side will be shown. Using this view user will be able
 * to restore files on the local side
 */
public class RemoteFiles extends DialogNotModal {
    public RemoteFiles(Stage parent, HostConfigurationBean host) {
        super(parent,200,600);
        this.host = host;
        root=new TreeItem<>(null);
        root.setExpanded(true);

        files = new TreeView<>(root);
        files.setCellFactory(fileCopiedTreeView -> new RemoteFilesCell());
        files.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue==null) return;
            TreeItem<RemoteFile> childClicked = newValue;
            final RemoteFile value = childClicked.getValue();
            if (value==null) return;
            File file = value.getRemoteFile();
            if (!childClicked.getChildren().isEmpty() ||!file.isDirectory()) return;

            final TargetHost targetHost;
            try {
                targetHost = TargetHostBuilder.createRMITargetHost(this.host.getHostKey(), this.host.getCurrentRMIAddress(), this.host.getCurrentRMIPort());
            } catch (IOException ex) {
                new Alert(this, "unable to obtain remote proxy error=" + ex.getMessage()).show();
                return;
            }
            if (file.isDirectory()) {
                logger.debug("search child files at level 1 of "+FileTools.getPathStringFromPathList(file.getPath()));
                try {
                    targetHost.listFilesAt(file);
                    for (File f : file.getChilds()) {
                        childClicked.getChildren().add(new TreeItem<>(new RemoteFile(value.getLocaleFile(),f)));
                    }
                } catch (IOException e) {
                    new Alert(this, "server error" + e.getMessage()).show();
                }
            }
        });


        // add root files to be backuped to tree view
        // ------------------------------------------
        final List<String> directoriesToBeSavedSnapshot = Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot();
        final TargetHost targetHost;

        try {
            targetHost = TargetHostBuilder.createRMITargetHost(host.getHostKey(), host.getCurrentRMIAddress(), host.getCurrentRMIPort());
        } catch (IOException ex) {
            new Alert(this,"unable to obtain remote proxy error=" + ex.getMessage()).show();
            return;
        }
        for (String localFile : directoriesToBeSavedSnapshot) {
            final RemoteFile remoteFile = new RemoteFile(Paths.get(localFile));
            root.getChildren().add(new TreeItem<>(remoteFile));
        }

        //files.setShowRoot(true);
        HBox hbox = new HBox();
        getRootGroup().getChildren().add(hbox);
        ScrollPane scrollPane = new ScrollPane();
        hbox.getChildren().add(scrollPane);
        scrollPane.setContent(files);

        // fill all the content
        scrollPane.prefWidthProperty().bind(widthProperty());
        scrollPane.prefHeightProperty().bind(heightProperty());
        files.prefHeightProperty().bind(scrollPane.heightProperty());
        files.prefWidthProperty().bind(scrollPane.widthProperty());
    }



    private TreeView<RemoteFile> files ;
    private TreeItem<RemoteFile> root ;
    private HostConfigurationBean host;
    private final static Logger logger = Logger.getLogger(RemoteFiles.class);
}
