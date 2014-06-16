package info.yourhomecloud.fxgui;

import info.yourhomecloud.hosts.File;
import info.yourhomecloud.utils.FileTools;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Created by beynet on 15/06/2014.
 */
public class RemoteFilesCell extends TreeCell<RemoteFile> {
    private static final Image folder = new Image(FileCopiedCell.class.getResourceAsStream("/Folder.png"));
    @Override
    protected void updateItem(RemoteFile item, boolean empty) {
        super.updateItem(item,empty);
        if (empty==true) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (item != null) {

                final File remoteFile = item.getRemoteFile();
                final Path localeFile = item.getLocaleFile();
                final List<String> path = remoteFile.getPath();
                BasicFileAttributes targetAttrs = null;


                // remove all styles
                getStyleClass().remove(Styles.LOCALE_FILE_REMOVED);
                getStyleClass().remove(Styles.REMOTE_FILE_OBSOLETE);

                if (!Files.exists(localeFile)) {
                    getStyleClass().add(Styles.LOCALE_FILE_REMOVED);
                }
                else {
                    if (!remoteFile.isDirectory()) {
                        long localModified = 0;
                        try {
                            targetAttrs = Files.readAttributes(item.getLocaleFile(), BasicFileAttributes.class);
                            localModified = targetAttrs.lastModifiedTime().toMillis();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (localModified > remoteFile.getLastModified()) {
                            getStyleClass().add(Styles.REMOTE_FILE_OBSOLETE);
                        }
                    }
                }
                setText(path.get(path.size() - 1));
                if (remoteFile.isDirectory()) {
                    logger.debug(FileTools.getPathFromPathList(remoteFile.getPath()) + " is a directory");
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
