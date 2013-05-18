package info.yourhomecloud.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ConfigurationType")
@XmlRootElement(name="configuration")
public class ConfigurationBean {
    public ConfigurationBean() {
        
    }
    
    
    @XmlElement(name="localhost")
    public HostConfigurationBean getLocalhost() {
        return localhost;
    }
    public void setLocalhost(HostConfigurationBean localhost) {
        this.localhost = localhost;
    }
    
    @XmlElement(name="host")
    public List<HostConfigurationBean> getOtherHosts() {
        return otherHosts;
    }
    
    public static Map<String,HostConfigurationBean> getOtherHostsMap(List<HostConfigurationBean> otherHosts) {
        Map<String,HostConfigurationBean> map = new HashMap<String, HostConfigurationBean>();
        for (HostConfigurationBean host : otherHosts) {
            map.put(host.getHostKey(), host);
        }
        return map;
    }
    

    private HostConfigurationBean       localhost ;
    private List<HostConfigurationBean> otherHosts = new ArrayList<>();
}
