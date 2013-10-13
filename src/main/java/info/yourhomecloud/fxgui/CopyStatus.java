package info.yourhomecloud.fxgui;

import com.sun.glass.ui.Application;
import info.yourhomecloud.files.events.EndOfCopy;
import info.yourhomecloud.files.events.EndOfSync;
import info.yourhomecloud.files.events.FileSyncerEvent;
import info.yourhomecloud.files.events.StartOfSync;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 12/10/13
 * Time: 15:48
 * To change this template use File | Settings | File Templates.
 */
public class CopyStatus extends DialogNotModal implements Observer{
    public CopyStatus(Stage parent) {
        super(parent,300,300);
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

        HBox hbox = new HBox();
        getRootGroup().getChildren().add(hbox);
        hbox.getChildren().add(files);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof FileSyncerEvent) {
            final FileSyncerEvent evt = (FileSyncerEvent) arg;
            Application.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (evt instanceof StartOfSync) {
                        //markSyncInProcess();
                    } else if (evt instanceof EndOfSync) {
                        //markSyncCompleted();
                    } else {
                        boolean completed = false;
                        if (evt instanceof EndOfCopy) {
                            completed = true;
                        }
                        FileCopied file = new FileCopied(evt.getFile(), completed);

                    }
                }
            });

        }
    }

    void addElement(FileCopied file,TreeItem<FileCopied> parent) {

        for (TreeItem<FileCopied> item : rootTreeItem.getChildren()) {
            FileCopied el = item.getValue();
            if (el.getPath().equals(file.getPath())) {
                parent.getChildren().remove(el);
                parent.getChildren().add(new TreeItem<FileCopied>(file));
            }
            if (el.getPath().toString().startsWith(el.getPath().toString())) {
                addElement(file, item);
                break;
            }
        }
    }

    private TreeView<FileCopied> files;
    private TreeItem<FileCopied> rootTreeItem;
}
