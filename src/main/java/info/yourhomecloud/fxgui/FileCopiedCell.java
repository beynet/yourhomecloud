package info.yourhomecloud.fxgui;

import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Files;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 12/10/13
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class FileCopiedCell extends TreeCell<FileCopied> {

    private static final Image folder = new Image(FileCopiedCell.class.getResourceAsStream("/Folder.png"));


    @Override
    protected void updateItem(FileCopied fileCopied, boolean b) {
        super.updateItem(fileCopied, b);
        if (fileCopied!=null) {
            if (Files.isDirectory(fileCopied.getPath())) {
                ImageView imageView = new ImageView(folder);
                imageView.setFitWidth(24);
                imageView.setFitHeight(24);
                setGraphic(imageView);
            }
            if (fileCopied.isCompleted()==false) {
                getStyleClass().add(Styles.COPY_IN_PROCESS);
            }
            else {
                getStyleClass().add(Styles.COPY_TERMINATED);
            }
            setText(fileCopied.toString());
        }
    }
}
