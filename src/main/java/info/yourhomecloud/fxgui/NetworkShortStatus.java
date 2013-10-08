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
        Image image = new Image("/Fix_R.gif");
        imv.setImage(image);
        imv.setFitHeight(10);
        imv.setFitWidth(10);
        Label l = new Label("network status");
        getChildren().addAll(l, imv);
    }

    public void updateNetworkStatus() {
        if (Configuration.getConfiguration().getMainHostRMIAddr()!=null || Configuration.getConfiguration().isMainHost()) {
            imv.setImage(new Image("/Fix_G.gif"));
        }
        else {
            imv.setImage(new Image("/Fix_R.gif"));
        }
    }

    private ImageView imv;
}
