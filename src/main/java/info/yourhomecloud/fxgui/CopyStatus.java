package info.yourhomecloud.fxgui;

import com.sun.glass.ui.Application;
import info.yourhomecloud.files.events.EndOfCopy;
import info.yourhomecloud.files.events.EndOfSync;
import info.yourhomecloud.files.events.FileSyncerEvent;
import info.yourhomecloud.files.events.StartOfSync;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 * stage which contain the tree view showing the copy in progress
 */
public class CopyStatus extends DialogNotModal implements Observer {
    public CopyStatus(Stage parent) {
        super(parent,250,400);
        files = new TreeView<>();

        files.setCellFactory(new Callback<TreeView<FileCopied>, TreeCell<FileCopied>>() {
            @Override
            public TreeCell<FileCopied> call(TreeView<FileCopied> fileCopiedTreeView) {
                return new FileCopiedCell();
            }
        });
        rootTreeItem = new TreeItem<FileCopied>(new FileCopied(Paths.get("/"),true));
        rootTreeItem.setExpanded(true);


        files.setRoot(rootTreeItem);
        files.setShowRoot(true);

        HBox hbox = new HBox();


        getRootGroup().getChildren().add(hbox);
        ScrollPane scrollPane = new ScrollPane();
        hbox.getChildren().add(scrollPane);
        scrollPane.setContent(files);

        // fill all the content
        scrollPane.prefWidthProperty().bind(widthProperty());
        scrollPane.prefHeightProperty().bind(heightProperty());
    }

    public void reset() {
        rootTreeItem.getChildren().clear();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof FileSyncerEvent) {
            final FileSyncerEvent evt = (FileSyncerEvent) arg;
            Application.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (evt instanceof StartOfSync) {
                        // TODO: markSyncInProcess();
                    } else if (evt instanceof EndOfSync) {
                        //TODO : markSyncCompleted();
                    } else {
                        boolean completed = false;
                        if (evt instanceof EndOfCopy) {
                            completed = true;
                        }
                        FileCopied file = new FileCopied(evt.getFile(), completed);
                        addElement(file,rootTreeItem);
                    }
                }
            });

        }
    }

    /**
     * recursive method used to add an element to the tree.
     * New or modified element is stored following its path.
     * @param file
     * @param parent
     */
    void addElement(FileCopied file,TreeItem<FileCopied> parent) {


        for (TreeItem<FileCopied> item : parent.getChildren()) {
            FileCopied el = item.getValue();
            // found a children in the tree with the same path
            if (el.getPath().equals(file.getPath())) {
                item.setValue(file);
                return;
            }
            // found a parent of file
            if (Files.isDirectory(el.getPath()) && file.getPath().toString().startsWith(el.getPath().toString())) {
                addElement(file, item);
                return;
            }
        }

        if (parent==rootTreeItem) {
            TreeItem<FileCopied> newChild = new TreeItem<>(file);
            newChild.setExpanded(true);
            parent.getChildren().add(newChild);
            return;
        }
        else {
            Path currentRootPath = parent.getValue().getPath();
            Path relativized = currentRootPath.relativize(file.getPath());

            Iterator<Path> iter = relativized.iterator();
            Path current = null ;
            TreeItem<FileCopied> lastChildCreated = null ;
            while (iter.hasNext()) {
                Path next = iter.next();
                if (current==null) {
                    current = currentRootPath.resolve(next) ;
                    lastChildCreated = new TreeItem<FileCopied>(new FileCopied(current,file.isCompleted()));
                    lastChildCreated.setExpanded(true);
                    parent.getChildren().add(lastChildCreated);
                }
                else {
                    current = current.resolve(next);
                    TreeItem<FileCopied> newChild = new TreeItem<FileCopied>(new FileCopied(current,file.isCompleted()));
                    newChild.setExpanded(true);
                    lastChildCreated.getChildren().add(newChild);
                    lastChildCreated = newChild;
                }
            }
        }
    }

    private TreeView<FileCopied> files;
    private TreeItem<FileCopied> rootTreeItem;
}
