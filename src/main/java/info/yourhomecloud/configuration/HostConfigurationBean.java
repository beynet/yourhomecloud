package info.yourhomecloud.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="HostConfigurationType")
public class HostConfigurationBean implements Serializable{

    @Override
    public String toString() {
        return "name="+getHostName()+" key="+getHostKey();
    }
    
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
    public String getCurrentRMIAddress() {
        return currentRMIAddress;
    }
    public void setCurrentRMIAddress(String currentAddress) {
        this.currentRMIAddress = currentAddress;
    }
    
    @XmlTransient
    public int getCurrentRMIPort() {
        return currentRMIPort;
    }
    public void setCurrentRMIPort(int currentRmiPort) {
        this.currentRMIPort = currentRmiPort;
    }

    @Override
    protected HostConfigurationBean clone() {
        HostConfigurationBean result = new HostConfigurationBean();
        result.setCurrentRMIAddress(this.getCurrentRMIAddress());
        result.setCurrentRMIPort(this.getCurrentRMIPort());
        result.setNetworkInterface(this.getNetworkInterface());
        result.setHostKey(this.getHostKey());
        result.setHostName(this.getHostName());
        result.setLastUpdateDate(this.getLastUpdateDate());
        result.directoriesToBeSaved.addAll(this.getDirectoriesToBeSaved());
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.currentRMIAddress);
        hash = 17 * hash + this.currentRMIPort;
        hash = 17 * hash + Objects.hashCode(this.networkInterface);
        hash = 17 * hash + Objects.hashCode(this.hostKey);
        hash = 17 * hash + Objects.hashCode(this.hostName);
        hash = 17 * hash + Objects.hashCode(this.lastUpdateDate);
        hash = 17 * hash + Objects.hashCode(this.directoriesToBeSaved);
        return hash; 
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HostConfigurationBean other = (HostConfigurationBean) obj;
        if (!Objects.equals(this.currentRMIAddress, other.currentRMIAddress)) {
            return false;
        }
        if (this.currentRMIPort != other.currentRMIPort) {
            return false;
        }
        if (!Objects.equals(this.networkInterface, other.networkInterface)) {
            return false;
        }
        if (!Objects.equals(this.hostKey, other.hostKey)) {
            return false;
        }
        if (!Objects.equals(this.hostName, other.hostName)) {
            return false;
        }
        if (!Objects.equals(this.lastUpdateDate, other.lastUpdateDate)) {
            return false;
        }
        if (!Objects.equals(this.directoriesToBeSaved, other.directoriesToBeSaved)) {
            return false;
        }
        return true;
    }
    
    
    private String currentRMIAddress;
    private int currentRMIPort ;
    private String networkInterface ;
    private String hostKey ;
    private String hostName;
    private Long   lastUpdateDate = null;
    private List<String> directoriesToBeSaved = new ArrayList<>();

    

    
}
