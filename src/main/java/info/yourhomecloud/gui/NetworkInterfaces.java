/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.NetworkUtils;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author beynet
 */
public class NetworkInterfaces extends AbstractListModel<NetworkInterface> {

    private List<NetworkInterface> interfaces = new ArrayList<>();

    public NetworkInterfaces() {
        try {
            this.interfaces = NetworkUtils.getInterfaces();
        } catch (IOException ex) {
            throw new RuntimeException("unable to retrieve network interfaces", ex);
        }
    }

    @Override
    public int getSize() {
        return interfaces.size();
    }

    @Override
    public NetworkInterface getElementAt(int index) {
        return interfaces.get(index);
    }
    
    public int getIndexOfConfiguredInterface() {
        for (int i=0;i<interfaces.size();i++) {
            if (interfaces.get(i).getName().equals(Configuration.getConfiguration().getNetworkInterface())) return i;
        }
        return -1;
    }
}
