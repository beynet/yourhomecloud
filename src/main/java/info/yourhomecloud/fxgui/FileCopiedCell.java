package info.yourhomecloud.fxgui;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Files;

/**
 * a cell representing a file copied (or in copy) on the remote side
 */
public class FileCopiedCell extends TreeCell<FileCopied> {

    private static final Image folder = new Image(FileCopiedCell.class.getResourceAsStream("/Folder.png"));


    @Override
    protected void updateItem(FileCopied fileCopied, boolean empty) {
        super.updateItem(fileCopied, empty);
        getStyleClass().remove(Styles.COPY_IN_PROCESS);
        getStyleClass().remove(Styles.COPY_TERMINATED);
        if (empty==true) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (fileCopied != null) {

                if (Files.isDirectory(fileCopied.getPath())) {
                    ImageView imageView = new ImageView(folder);
                    imageView.setFitWidth(24);
                    imageView.setFitHeight(24);
                    setGraphic(imageView);
                }
                if (fileCopied.isCompleted() == false) {
                    getStyleClass().add(Styles.COPY_IN_PROCESS);
                } else {
                    getStyleClass().add(Styles.COPY_TERMINATED);
                }
                setText(fileCopied.toString());
            }
        }
    }
}
