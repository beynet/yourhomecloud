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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;

/**
 *
 * @author beynet
 */
public class NetworkInterfaces extends AbstractListModel<String> {

    private List<String> interfaces = new ArrayList<>();

    public NetworkInterfaces() {
        try {
            List<NetworkInterface> interfaces = NetworkUtils.getInterfaces();
            for (NetworkInterface i : interfaces) {
                this.interfaces.add(i.getDisplayName());
            }
        } catch (IOException ex) {
            throw new RuntimeException("unable to retrieve network interfaces", ex);
        }
    }

    @Override
    public int getSize() {
        return interfaces.size();
    }

    @Override
    public String getElementAt(int index) {
        return interfaces.get(index);
    }
    
    public int getIndexOfConfiguredInterface() {
        for (int i=0;i<interfaces.size();i++) {
            if (interfaces.get(i).equals(Configuration.getConfiguration().getNetworkInterface())) return i;
        }
        return -1;
    }
}
