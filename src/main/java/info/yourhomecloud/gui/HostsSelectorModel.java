/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.gui;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author beynet
 */
public class HostsSelectorModel extends AbstractListModel<HostConfigurationBean> {

    private List<HostConfigurationBean> hosts;
    private final boolean connectedOnly;

    /**
     * Hosts selector model
     *
     * @param connectedOnly : if true the model will only display connected
     * hosts
     */
    public HostsSelectorModel(boolean connectedOnly) {
        this.connectedOnly = connectedOnly;
        hosts = new ArrayList<>();
        for (HostConfigurationBean h : Configuration.getConfiguration().getOtherHosts()) {
            if (connectedOnly == true) {
                if (h.getCurrentRMIAddress() != null) {
                    hosts.add(h);
                }
            } else {
                hosts.add(h);
            }
        }
    }

    @Override
    public int getSize() {
        return hosts.size();
    }

    @Override
    public HostConfigurationBean getElementAt(int index) {
        return hosts.get(index);
    }
    
    public void deleteSelectedHost(int selected) throws IOException {
        if (selected==-1) return;
        Configuration.getConfiguration().removeHost(getElementAt(selected).getHostKey());
    }

    void refresh() {
        if (hosts.size()!=0) fireIntervalRemoved(this, 0, hosts.size()-1);
        hosts = new ArrayList<>();
        for (HostConfigurationBean h : Configuration.getConfiguration().getOtherHosts()) {
            if (connectedOnly == true) {
                if (h.getCurrentRMIAddress() != null) {
                    hosts.add(h);
                    fireIntervalAdded(this, hosts.size()-1, hosts.size()-1);
                }
            } else {
                hosts.add(h);
                fireIntervalAdded(this, hosts.size()-1, hosts.size()-1);
            }
        }
    }
}
