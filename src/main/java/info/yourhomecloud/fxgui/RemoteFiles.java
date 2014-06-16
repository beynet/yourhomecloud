package info.yourhomecloud.fxgui;

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
            TreeItem<File> childClicked = newValue;
            File file = childClicked.getValue();
            if (!childClicked.getChildren().isEmpty() ||!file.isDirectory()) return;

            final TargetHost targetHost;
            try {
                targetHost = TargetHostBuilder.createRMITargetHost(host.getHostKey(), host.getCurrentRMIAddress(), host.getCurrentRMIPort());
            } catch (IOException ex) {
                new Alert(this, "unable to obtain remote proxy error=" + ex.getMessage()).show();
                return;
            }
            if (file.isDirectory()) {
                logger.debug("search child files at level 1 of "+FileTools.getPathStringFromPathList(file.getPath()));
                try {
                    targetHost.listFilesAt(file);
                    for (File f : file.getChilds()) {
                        addFile(f, childClicked);
                    }
                } catch (IOException e) {
                    new Alert(this, "server error" + e.getMessage()).show();
                }
            }
        });

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



    public void addRootFile(File file) {
        addFile(file,files.getRoot());
    }

    public void addFile(File file,TreeItem<File> parent) {
        TreeItem<File> newChild = new TreeItem<>(file);
        newChild.setExpanded(false);
        parent.getChildren().add(newChild);
    }


    private TreeView<File> files ;
    private TreeItem<File> root ;
    private HostConfigurationBean host;
    private final static Logger logger = Logger.getLogger(RemoteFiles.class);
}
