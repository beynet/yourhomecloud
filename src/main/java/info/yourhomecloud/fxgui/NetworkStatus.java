/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import javafx.scene.control.TextArea;

/**
 *
 * @author beynet
 */
public class NetworkStatus extends TextArea {
    public NetworkStatus() {
//        getStyleClass().add("noborder");
        setEditable(false);
        updateInterface();
        updateOtherHosts();
        generateText();
    }

    public final void updateInterface() {
        interfaceLine = "Network interface used :" + Configuration.getConfiguration().getNetworkInterface();
    }

    public void updateMainHost() {
        if (Configuration.getConfiguration().isMainHost() == true) {
            mainHostLine = "Current instance is the master instance";
        } else {
            mainHostLine = "Master host ip=" + Configuration.getConfiguration().getMainHostRMIAddr();
        }
    }

    public final void updateOtherHosts() {
        otherHostsLine = "Connected hosts :\n";
        for (HostConfigurationBean host : Configuration.getConfiguration().getOtherHostsSnapshot()) {
            if (host.getCurrentRMIAddress() != null) {
                otherHostsLine = otherHostsLine.concat("\t");
                if (host.getHostName() != null) {
                    otherHostsLine = otherHostsLine.concat(host.getHostName() + " ");
                }
                otherHostsLine = otherHostsLine.concat(host.getCurrentRMIAddress()) + " " + host.getCurrentRMIPort() + "\n";
            }
        }
    }

    public final void generateText() {
        String result = interfaceLine;
        if (mainHostLine != null) {
            result = result.concat("\n");
            result = result.concat(mainHostLine);
        }
        result = result.concat("\n");
        result = result.concat(otherHostsLine);
        setText(result);
    }
    
    String mainHostLine = null;
    String interfaceLine = null;
    String otherHostsLine = null;
}
