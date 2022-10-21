package info.yourhomecloud.fxgui;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 13/10/13
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class Alert extends DialogModal {
    public Alert(Stage parent,String message) {
        super(parent,150,50);
        Label text = new Label(message);
        Text forSize = new Text(message);
        Button confirm = new Button("ok");

        GridPane grid = new GridPane();
        grid.prefWidthProperty().bind(getCurrentScene().widthProperty());
        /*grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);*/
        grid.getStyleClass().addAll(Styles.ALERT);



        getRootGroup().getChildren().add(grid);

        grid.add(text,0,0,3,1);
        GridPane.setHalignment(text,HPos.CENTER);

        grid.add(confirm,1,1,1,1);
        GridPane.setHalignment(confirm,HPos.CENTER);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(33);
        grid.getColumnConstraints().add(c1);


        // resize window to fit the text
        // ------------------------------
        setWidth(forSize.getLayoutBounds().getWidth());

        setOnCloseRequest(windowEvent -> windowEvent.consume());

        confirm.setOnMouseClicked(mouseEvent -> close());
    }
}
