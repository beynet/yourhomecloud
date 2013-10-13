package info.yourhomecloud.fxgui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 12/10/13
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public abstract class Dialog  extends Stage {
    public Dialog(Stage parent,double with,double height) {
        this.parent = parent;
        root = new Group();
        root.getStyleClass().add(Styles.CHILD_WINDOW);

        scene = new Scene(root, with, height);
        setScene(scene);
        initOwner(parent);
    }
    protected final Scene getCurrentScene() {
        return scene;
    }

    protected final Group getRootGroup() {
        return root;
    }

    protected final Stage getParentStage() {
        return parent;
    }
    private Group root;
    private Stage parent ;
    private Scene scene ;
}
