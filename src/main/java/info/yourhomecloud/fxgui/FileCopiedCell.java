package info.yourhomecloud.fxgui;

import javafx.scene.control.TreeCell;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 12/10/13
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class FileCopiedCell extends TreeCell<FileCopied> {
    @Override
    protected void updateItem(FileCopied fileCopied, boolean b) {
        super.updateItem(fileCopied, b);
        if (fileCopied!=null) {
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
