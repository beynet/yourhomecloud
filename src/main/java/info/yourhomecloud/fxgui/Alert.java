package info.yourhomecloud.fxgui;

import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
        grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.getStyleClass().addAll(Styles.ALERT);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(33);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(33);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(33);
        grid.getColumnConstraints().addAll(column1, column2,column3);

        grid.add(text,0,0,3,1);
        GridPane.setHalignment(text,HPos.CENTER);

        grid.add(confirm,1,1);
        GridPane.setHalignment(confirm,HPos.CENTER);

        getRootGroup().getChildren().add(grid);

        // resize window to fit the text
        // ------------------------------
        setWidth(forSize.getLayoutBounds().getWidth()+40);
        setHeight(forSize.getLayoutBounds().getHeight() + 80);

        setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                windowEvent.consume();
            }
        });

        confirm.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                close();
            }
        });
    }
}
