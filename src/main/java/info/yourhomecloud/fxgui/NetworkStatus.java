/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.stream.Collectors;


/**
 *
 * @author beynet
 */
public class NetworkStatus extends GridPane {
    public NetworkStatus() {
        networkInterfaceBegin = new Text("Selected network interface :");
        networkInterfaceName = new Text("");
        networkInterfaceName.getStyleClass().addAll("enforced");
        HBox interfaceLine = new HBox();
        interfaceLine.getChildren().addAll(networkInterfaceBegin,networkInterfaceName);
        add(interfaceLine,0,0);

        HBox masterHostLine = new HBox();
        mainHostLine= new Text();
        masterHostLine.getChildren().addAll(mainHostLine);
        add(masterHostLine,0,1);
        updateInterface();
        updateMainHost();

        VBox connectedHosts = new VBox();
        connectedHosts.getChildren().add(new Text("connected hosts :"));

        hostsList = FXCollections.observableArrayList();
        hostsListView = new ListView<>(hostsList);
        hostsListView.setCellFactory(new Callback<ListView<HostConfigurationBean>, ListCell<HostConfigurationBean>>() {
            @Override
            public ListCell<HostConfigurationBean> call(ListView<HostConfigurationBean> hostConfigurationBeanListView) {
                return new HostCell();
            }
        });
        connectedHosts.getChildren().add(hostsListView);
        hostsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.isControlDown()==true) {
                    hostsListView.getSelectionModel().clearSelection();
                    mouseEvent.consume();
                }
            }
        });

        add(connectedHosts, 0, 2);

    }

    public final void updateInterface() {
        networkInterfaceName.setText(Configuration.getConfiguration().getNetworkInterface());
    }

    public void updateMainHost() {
        if (Configuration.getConfiguration().isMainHost() == true) {
            mainHostLine.setText("Current instance is the master instance");
        } else if (Configuration.getConfiguration().getMainHostRMIAddr()!=null) {
            mainHostLine.setText("Master host ip=" + Configuration.getConfiguration().getMainHostRMIAddr());
        }
        else {
            mainHostLine.setText("No master host defined yet.");
        }
    }

    public final void updateOtherHosts() {
        hostsList.clear();
        hostsList.addAll(Configuration.getConfiguration().getOtherHostsSnapshot().stream().filter(host -> host.getCurrentRMIAddress() != null).collect(Collectors.toList()));
    }

    /**
     * @return the selected host
     */
    public HostConfigurationBean getSelectedHost() {
        return hostsListView.getSelectionModel().getSelectedItem();
    }



    private Text networkInterfaceBegin ;
    private Text networkInterfaceName ;
    private Text mainHostLine ;
    private ListView<HostConfigurationBean> hostsListView ;
    private ObservableList<HostConfigurationBean> hostsList ;
}
