package info.yourhomecloud.fxgui;

import info.yourhomecloud.hosts.File;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * in this view, files present on the remote side will be shown. Using this view user will be able
 * to restore files on the local side
 */
public class RemoteFiles extends DialogNotModal {
    public RemoteFiles(Stage parent) {
        super(parent,200,600);

        root=new TreeItem<>(null);

        files = new TreeView<>(root);
        files.setCellFactory(fileCopiedTreeView -> new RemoteFilesCell());
        root.setExpanded(true);
        //files.setShowRoot(true);
        HBox hbox = new HBox();


        getRootGroup().getChildren().add(hbox);
        ScrollPane scrollPane = new ScrollPane();
        hbox.getChildren().add(scrollPane);
        scrollPane.setContent(files);

        // fill all the content
        scrollPane.prefWidthProperty().bind(widthProperty());
        scrollPane.prefHeightProperty().bind(heightProperty());
    }



    public void addRootFile(File file) {
        TreeItem<File> newChild = new TreeItem<>(file);
        newChild.setExpanded(true);
        files.getRoot().getChildren().add(newChild);
    }


    private TreeView<File> files ;
    private TreeItem<File> root ;
}
