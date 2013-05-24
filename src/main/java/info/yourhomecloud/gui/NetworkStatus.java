/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import javax.swing.JTextArea;

/**
 *
 * @author beynet
 */
public final class NetworkStatus extends JTextArea {

    String mainHostLine = null;
    String interfaceLine = null;
    String otherHostsLine = null;

    public NetworkStatus() {
        updateInterface();
        updateOtherHosts();
        generateText();
    }

    public void updateInterface() {
        interfaceLine = "Network interface used :" + Configuration.getConfiguration().getNetworkInterface();
    }

    public void updateMainHost() {
        if (Configuration.getConfiguration().getMainHost() == null) {
            mainHostLine = "Current instance is the master instance";
        } else {
            mainHostLine = "Master host ip=" + Configuration.getConfiguration().getMainHost();
        }
    }

    public void updateOtherHosts() {
        otherHostsLine = "Connected hosts :\n";
        if (Configuration.getConfiguration().getOtherHosts() != null) {
            for (HostConfigurationBean host : Configuration.getConfiguration().getOtherHosts()) {
                if (host.getCurrentAddress() != null) {
                    otherHostsLine = otherHostsLine.concat("\t" + host.getCurrentAddress() + " " + host.getCurrentRmiPort()+"\n");
                }
            }
        }
    }

    public void generateText() {
        String result = interfaceLine;
        if (mainHostLine != null) {
            result = result.concat("\n");
            result = result.concat(mainHostLine);
        }
        result = result.concat("\n");
        result = result.concat(otherHostsLine);
        setText(result);
    }
}
