package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * User: beynet
 * Date: 08/10/13
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public class NetworkShortStatus extends HBox {
    public NetworkShortStatus() {
        setAlignment(Pos.BASELINE_RIGHT);
        imv = new ImageView();
        statusLabel = new Label("");
        setDefaultStatus();
        getChildren().addAll(statusLabel, imv);
    }

    private void setDefaultStatus() {
        Image image = new Image("/Fix_R.gif");
        imv.setImage(image);
        imv.setFitHeight(10);
        imv.setFitWidth(10);
        statusLabel.setText(DEFAULT_STATUS);
    }

    private void setConnectedStatus() {
        statusLabel.setText(CONNECTED_STATUS.replace("%IF",Configuration.getConfiguration().getNetworkInterface()));
        Image image = new Image("/Fix_G.gif");
        imv.setImage(image);
        imv.setFitHeight(10);
        imv.setFitWidth(10);
    }


    public void updateNetworkStatus() {
        if (Configuration.getConfiguration().getMainHostRMIAddr()!=null || Configuration.getConfiguration().isMainHost()) {
            setConnectedStatus();
        }
        else {
            setDefaultStatus();
        }
    }

    private ImageView imv;
    private Label statusLabel;

    private final static String DEFAULT_STATUS = "network status :";
    private final static String CONNECTED_STATUS = "connected (interface %IF)";
}
