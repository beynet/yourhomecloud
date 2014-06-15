package info.yourhomecloud.fxgui;

import info.yourhomecloud.hosts.File;
import info.yourhomecloud.utils.FileTools;
import javafx.scene.control.TreeCell;

/**
 * Created by beynet on 15/06/2014.
 */
public class RemoteFilesCell extends TreeCell<File> {
    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item,empty);
        if (empty==true) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (item != null) {
                setText(FileTools.getPathStringFromPathList(item.getPath()));
            } else {
                setText("...");
            }
        }
    }
}
