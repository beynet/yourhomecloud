package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import info.yourhomecloud.hosts.File;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.hosts.TargetHostBuilder;
import info.yourhomecloud.utils.FileTools;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * in this view, files present on the remote side will be shown. Using this view user will be able
 * to restore files on the local side
 */
public class RemoteFiles extends DialogNotModal {


    private TargetHost getTargetHost() throws IOException {
        return TargetHostBuilder.createRMITargetHost(this.host.getHostKey(), this.host.getCurrentRMIAddress(), this.host.getCurrentRMIPort());
    }
    /**
     * show files backuped on remote host
     * @param parent
     * @param host
     */
    public RemoteFiles(Stage parent, HostConfigurationBean host) {
        super(parent,200,600);
        this.executor = Executors.newCachedThreadPool();
        this.host = host;
        root=new TreeItem<>(null);
        root.setExpanded(true);

        files = new TreeView<>(root);
        files.setCellFactory(fileCopiedTreeView -> new RemoteFilesCell(this));

        // when selection change, if selected item is a directory
        // we obtain child files if child files list is empty
        // ------------------------------------------------------
        files.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            executor.execute(()-> {
                if (newValue == null) return;
                TreeItem<RemoteFile> childClicked = newValue;
                final RemoteFile value = childClicked.getValue();
                if (value == null) return;
                File file = value.getRemoteFile();
                if (!childClicked.getChildren().isEmpty() || !file.isDirectory()) return;

                final TargetHost targetHost;
                try {
                    targetHost = getTargetHost();
                } catch (IOException ex) {
                    new Alert(this, "unable to obtain remote proxy error=" + ex.getMessage()).show();
                    return;
                }
                if (file.isDirectory()) {
                    logger.debug("search child files at level 1 of " + FileTools.getPathStringFromPathList(file.getPath()));
                    try {
                        targetHost.listFilesAt(file);
                        //Platform.runLater(()-> {
                            for (File f : file.getChilds()) {
                                childClicked.getChildren().add(new TreeItem<>(new RemoteFile(value.getLocaleFile(), f)));
                            }
                        //});
                    } catch (IOException e) {
                        Platform.runLater(()->new Alert(this, "server error" + e.getMessage()).show());
                    }
                }
            });
        });



        // add root files to be backuped to tree view
        // ------------------------------------------
        final List<String> directoriesToBeSavedSnapshot = Configuration.getConfiguration().getDirectoriesToBeSavedSnapshot();

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

    public void removeFile(TreeItem<RemoteFile> selected) {
        executor.execute(()->{
            final TargetHost targetHost ;
            try {
                targetHost = getTargetHost();
                targetHost.removeFile(FileTools.getPathFromPathList(selected.getValue().getRemoteFile().getPath()));
            } catch (IOException ex) {
                Platform.runLater(() -> new Alert(this, "unable to obtain remote proxy error=" + ex.getMessage()).show());
            }
        });
    }


    private TreeView<RemoteFile>   files ;
    private TreeItem<RemoteFile>   root ;
    private HostConfigurationBean  host;
    private Executor executor;

    private final static Logger logger = Logger.getLogger(RemoteFiles.class);


}
