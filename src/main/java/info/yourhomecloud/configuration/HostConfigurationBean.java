package info.yourhomecloud.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="HostConfigurationType")
public class HostConfigurationBean implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 456537284424417563L;
    public HostConfigurationBean() {
    }

    @XmlElement(name="hostKey")
    public String getHostKey() {
        return hostKey;
    }
    public void setHostKey(String hostKey) {
        this.hostKey = hostKey;
    }
    
    
    
    @XmlAttribute(name="lastUdateDate")
    public Long getLastUpdateDate() {
        if (lastUpdateDate==null) {
            lastUpdateDate = Long.valueOf(0);
        }
        return lastUpdateDate;
    }
    public void setLastUpdateDate(Long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    
    
    @XmlElement(name="hostName")
    public String getHostName() {
        return hostName;
    }
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    @XmlElement(name="directory")
    public List<String> getDirectoriesToBeSaved() {
        return directoriesToBeSaved;
    }
    
    @XmlElement(name="networkInterface")
    public String getNetworkInterface() {
        return networkInterface;
    }
    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }
    
    
    @XmlTransient
    public String getCurrentAddress() {
        return currentAddress;
    }
    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
    
    @XmlTransient
    public int getCurrentRmiPort() {
        return currentRmiPort;
    }
    public void setCurrentRmiPort(int currentRmiPort) {
        this.currentRmiPort = currentRmiPort;
    }
    
    private String currentAddress;
    private int currentRmiPort ;
    private String networkInterface ;
    private String hostKey ;
    private String hostName;
    private Long   lastUpdateDate = null;
    private List<String> directoriesToBeSaved = new ArrayList<>();

    

    
}
