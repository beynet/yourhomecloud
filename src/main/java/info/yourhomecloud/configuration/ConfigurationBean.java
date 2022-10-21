package info.yourhomecloud.configuration;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * bean used to store the XML representation of the configuration
 *
 * @author beynet
 */
@XmlType(name = "ConfigurationType")
@XmlRootElement(name = "configuration")
public class ConfigurationBean {

    public ConfigurationBean() {
    }

    @XmlElement(name = "localhost")
    public HostConfigurationBean getLocalhost() {
        return localhost;
    }

    public void setLocalhost(HostConfigurationBean localhost) {
        this.localhost = localhost;
    }

    @XmlElement(name = "host")
    public List<HostConfigurationBean> getOtherHosts() {
        return otherHosts;
    }

    /**
     * remove host which key match provided key
     * @param hostKey
     * @return 
     */
    public HostConfigurationBean removeHost(String hostKey) {
        if (hostKey == null || "".equals(hostKey)) {
            throw new IllegalArgumentException("hostkey must not be null nor empty");
        }
        HostConfigurationBean found = null;
        for (HostConfigurationBean toRemove : otherHosts) {
            if (hostKey.equals(toRemove.getHostKey())) {
                found = toRemove;
                break;
            }
        }
        if (found != null) {
            otherHosts.remove(found);
        }
        return found;
    }

    static Map<String, HostConfigurationBean> getHostsMapFromHostsList(List<HostConfigurationBean> otherHosts) {
        Map<String, HostConfigurationBean> map = new HashMap<>();
        for (HostConfigurationBean host : otherHosts) {
            map.put(host.getHostKey(), host);
        }
        return map;
    }

    /**
     * update configuration using the configuration received from updater
     *
     * @param update : the configuration update
     * @param updater : the host who sent this update
     */
    void updateHostList(List<HostConfigurationBean> update, HostConfigurationBean updater) {
        Map<String, HostConfigurationBean> newHostsMap = ConfigurationBean.getHostsMapFromHostsList(update);
        Map<String, HostConfigurationBean> currentHostsMap = ConfigurationBean.getHostsMapFromHostsList(otherHosts);

        for (Map.Entry<String, HostConfigurationBean> entry : newHostsMap.entrySet()) {
            HostConfigurationBean newConf = entry.getValue();
            // we nether update current host configuration
            if (getLocalhost().getHostKey().equals(newConf.getHostKey())) {
                continue;
            }
            // we always update updater
            if (updater.getHostKey().equals(newConf.getHostKey())) {
                currentHostsMap.put(entry.getKey(), newConf);
                continue;
            }
            HostConfigurationBean currentConf = currentHostsMap.get(entry.getKey());
            if (currentConf == null) {
                currentHostsMap.put(entry.getKey(), newConf);
            } else {
                if (currentConf.getLastUpdateDate() == null) {
                    currentConf.setLastUpdateDate(Long.valueOf(0));
                }
                if (newConf.getLastUpdateDate() == null) {
                    newConf.setLastUpdateDate(Long.valueOf(0));
                }
                int compare = currentConf.getLastUpdateDate().compareTo(newConf.getLastUpdateDate());
                if ( compare == 0) {
                    if (currentConf.getCurrentRMIAddress()==null && newConf.getCurrentRMIAddress()!=null) {
                        currentHostsMap.put(entry.getKey(), newConf);
                    }
                }
                else if ( compare < 0 ) {
                    currentHostsMap.put(entry.getKey(), newConf);
                }
            }
        }
        otherHosts.clear();
        otherHosts.addAll(currentHostsMap.values());
    }
    private HostConfigurationBean localhost;
    private final List<HostConfigurationBean> otherHosts = new ArrayList<>();
}
