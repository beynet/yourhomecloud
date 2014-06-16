package info.yourhomecloud.fxgui;

import info.yourhomecloud.hosts.File;
import info.yourhomecloud.utils.FileTools;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;

import java.nio.file.Files;

/**
 * Created by beynet on 15/06/2014.
 */
public class RemoteFilesCell extends TreeCell<File> {
    private static final Image folder = new Image(FileCopiedCell.class.getResourceAsStream("/Folder.png"));
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
                if (item.isDirectory()) {
                    logger.debug(FileTools.getPathFromPathList(item.getPath()) + " is a directory");
                    ImageView imageView = new ImageView(folder);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    setGraphic(imageView);
                }
                else {
                    setGraphic(null);
                }
            } else {
                setText("...");
            }
        }
    }

    private final static Logger logger = Logger.getLogger(RemoteFilesCell.class);
}
