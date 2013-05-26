package info.yourhomecloud.configuration;

import info.yourhomecloud.files.impl.FileSyncerImpl;
import info.yourhomecloud.hosts.impl.RMITargetHost;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.rmi.RMIUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class Configuration extends Observable {
    private boolean mainHost;

    /**
     * @param configDirectory : must be a directory
     */
    private Configuration(Path configDirectory) {
        mainHost = false ;
        this.configDirectory = configDirectory;
        if (!Files.exists(configDirectory)) {
            throw new IllegalArgumentException("Directory " + configDirectory + " does not exist");
        }
        if (!Files.isDirectory(configDirectory)) {
            throw new IllegalArgumentException("File " + configDirectory + " is not a directory");
        }
        readConfiguration();
    }
    /**
     * @return the path of the directory where the configuration file
     * will be stored and the backups from the other hosts.
     */
    public Path getConfigurationPath() {
        return this.configDirectory;
    }
    /**
     * declare current host as the main host
     */
    public void setMainHost( ) {
        mainHost = true;
    } 
    
    /**
     * @return true if current host is the main host
     */
    public boolean isMainHost() {
        return mainHost;
    }

    /**
     * try to read XML configuration file - create a default new file if this
     * file does not exist.
     */
    private void readConfiguration() {
        Path configFile = this.configDirectory.resolve("yourhomecloud.xml");
        if (Files.exists(configFile)) {
            Unmarshaller unm;
            try {
                unm = context.createUnmarshaller();
            } catch (JAXBException e) {
                throw new RuntimeException("unable to create an unmarshaller", e);
            }
            try {
                configuration = (ConfigurationBean) unm.unmarshal(configFile.toFile());
                return;
            } catch (JAXBException e) {
                logger.error("unable to read configuration file - a new configuration file will be generated");
                // TODO : create a procedure to re-instantiate an host
            }
        }
        generateNewConfiguration(configFile);
    }

    /**
     * generate a new configuration File -
     */
    private void generateNewConfiguration(Path configFile) {
        configuration = new ConfigurationBean();
        HostConfigurationBean localHost = new HostConfigurationBean();
        localHost.setHostKey(UUID.randomUUID().toString());
        localHost.setLastUpdateDate(new Long(System.currentTimeMillis()));
        try {
            localHost.setNetworkInterface(NetworkUtils.getFirstInterface().getDisplayName());
        } catch (IOException e) {
            throw new RuntimeException("unable to retrieve network interfaces", e);
        }
        configuration.setLocalhost(localHost);
        saveConfiguration(Change.CREATION);
    }

    private void saveConfiguration(Change c) {
        Path configFile = this.configDirectory.resolve("yourhomecloud.xml");
        try {
            context.createMarshaller().marshal(configuration, configFile.toFile());
            this.setChanged();
            this.notifyObservers(c);
        } catch (JAXBException e) {
            logger.error("unable to create new configuration file", e);
            throw new RuntimeException(e);
        }
    }

    public String getNetworkInterface() {
        return configuration.getLocalhost().getNetworkInterface();
    }

    public void setNetworkInterface(String i) {
        configuration.getLocalhost().setNetworkInterface(i);
        configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
        //FIXME : restart services
        saveConfiguration(Change.NETWORK_INTERFACE);
    }

    /**
     * add dir to the list of directories to be backuped
     *
     * @param dir
     */
    public void addDirectoryToBeSaved(Path dir) {
        dir = dir.toAbsolutePath().normalize();
        if (!configuration.getLocalhost().getDirectoriesToBeSaved().contains(dir.toString())) {
            configuration.getLocalhost().getDirectoriesToBeSaved().add(dir.toString());
            configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
            saveConfiguration(Change.DIRECTORIES_TO_BE_SAVED);
        }
    }

    /**
     * remove dir from the list of directories to be backuped
     *
     * @param dir
     */
    public void removeDirectoryToBeSaved(Path dir) {
        dir = dir.toAbsolutePath().normalize();
        String toString = dir.toString();
        if (configuration.getLocalhost().getDirectoriesToBeSaved().contains(toString)) {
            configuration.getLocalhost().getDirectoriesToBeSaved().remove(toString);
            configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
            saveConfiguration(Change.DIRECTORIES_TO_BE_SAVED);
        }
    }

    /**
     * define the main host - sync the known hosts with this main host
     *
     * @param hostAddr
     * @param rmiPort
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void setMainHostAndUpdateHostsList(String hostAddr, int rmiPort) throws RemoteException, NotBoundException {
        this.setChanged();
        this.notifyObservers(Change.MAIN_HOST);
        if (hostAddr == null) {
            setMainHost();
            return;//current host is the main host
        }
        this.mainHostAddr = hostAddr;
        this.mainHostRmiPort = rmiPort;
        RMIUtils.getRMIUtils();
        logger.info("register main host " + mainHostAddr + " port = " + this.mainHostRmiPort);
        List<HostConfigurationBean> hosts = new ArrayList<>();
        hosts.addAll(configuration.getOtherHosts());
        hosts.add(configuration.getLocalhost());
        info.yourhomecloud.network.rmi.Configuration remoteConfiguration = RMIUtils.getRemoteConfiguration(hostAddr, rmiPort);
        List<HostConfigurationBean> updateHosts = remoteConfiguration.updateHosts(hosts, configuration.getLocalhost());
        updateOtherHostsConfiguration(updateHosts, configuration.getLocalhost());
    }

    /**
     * method called before current exiting.
     * @throws RemoteException
     * @throws NotBoundException 
     */
    public void onExit() throws RemoteException, NotBoundException {
        if (this.mainHostAddr != null) {
            info.yourhomecloud.network.rmi.Configuration remoteConfiguration = RMIUtils.getRemoteConfiguration(this.mainHostAddr, this.mainHostRmiPort);
            remoteConfiguration.onExit(getCurrentHostKey());
        }
        else if (isMainHost()==true) {
            
        }
    }

    /**
     * @return the main host ip or null if current host is the main host
     */
    public String getMainHost() {
        return this.mainHostAddr;
    }
    
    /**
     * @param paths : optional except for the first call to initiate the
     * configuration
     * @return the configuration singleton
     */
    public static Configuration getConfiguration(Path... paths) {
        synchronized (Configuration.class) {
            if (_configuration == null) {
                if (paths.length == 0) {
                    throw new RuntimeException("Directory where configuration files will be stored must be provide at initialization");
                }
                _configuration = new Configuration(paths[0]);
            }
        }
        return _configuration;
    }

    public List<HostConfigurationBean> getOtherHosts() {
        return configuration.getOtherHosts();
    }

    /**
     * @return current host key
     */
    public String getCurrentHostKey() {
        return configuration.getLocalhost().getHostKey();
    }
    
    public String getCurrentHostName() {
        return configuration.getLocalhost().getHostName();
    }
    
    public void setCurrentHostName(String name) {
        configuration.getLocalhost().setHostName(name);
        configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
        saveConfiguration(Change.HOSTNAME);
    }

    public List<String> getDirectoriesToBeSaved() {
        return configuration.getLocalhost().getDirectoriesToBeSaved();
    }

    /**
     * update host list of host known by current host.
     *
     * @param update
     * @return
     */
    public List<HostConfigurationBean> updateOtherHostsConfiguration(List<HostConfigurationBean> update, HostConfigurationBean newHost) {
        Map<String, HostConfigurationBean> newHostsMap = ConfigurationBean.getOtherHostsMap(update);
        Map<String, HostConfigurationBean> currentHostsMap = ConfigurationBean.getOtherHostsMap(configuration.getOtherHosts());

        for (Entry<String, HostConfigurationBean> entry : newHostsMap.entrySet()) {
            HostConfigurationBean newConf = entry.getValue();
            if (configuration.getLocalhost().getHostKey().equals(newConf.getHostKey())) {
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
                if (currentConf.getLastUpdateDate().compareTo(newConf.getLastUpdateDate()) == 0) {
                    if (currentConf.getCurrentRMIAddress() == null) {
                        currentConf.setCurrentRMIAddress(newConf.getCurrentRMIAddress());
                        currentConf.setCurrentRMIPort(newConf.getCurrentRMIPort());
                    }
                }
                if (currentConf.getLastUpdateDate().compareTo(newConf.getLastUpdateDate()) < 0) {
                    currentHostsMap.put(entry.getKey(), newConf);
                }
            }
        }
        configuration.getOtherHosts().clear();
        configuration.getOtherHosts().addAll(currentHostsMap.values());
        saveConfiguration(Change.OTHER_HOSTS);
        List<HostConfigurationBean> results = new ArrayList<>();
        results.add(configuration.getLocalhost());
        results.addAll(configuration.getOtherHosts());

        if (isMainHost() == true) {
            // notify other hosts
            for (HostConfigurationBean bean : configuration.getOtherHosts()) {
                if (!newHost.getHostKey().equals(bean.getHostKey())) {
                    if (bean.getCurrentRMIAddress() != null) {
                        info.yourhomecloud.network.rmi.Configuration remoteConfiguration;
                        try {
                            remoteConfiguration = RMIUtils.getRemoteConfiguration(bean.getCurrentRMIAddress(), bean.getCurrentRMIPort());
                            remoteConfiguration.updateHosts(results, newHost);
                        } catch (Exception ex) {
                            logger.error("unable to send update to host", ex);
                        }
                    }
                }
            }
        }

        return results;
    }

    /**
     * clear the list of know hosts
     */
    protected void clearOtherHostsConfiguration() {
        configuration.getOtherHosts().clear();
    }

    protected ConfigurationBean getConfigurationBean() {
        return configuration;
    }
    //    public void add
    private Path configDirectory;
    private String mainHostAddr;
    private int mainHostRmiPort;
    private ConfigurationBean configuration;
    // JAXB configuration
    private final static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(ConfigurationBean.class);
        } catch (JAXBException e) {
            throw new RuntimeException("unable to generate jaxb context for configuration", e);
        }
    }
    private static Configuration _configuration = null;
    private static final Logger logger = Logger.getLogger(Configuration.class);

    public void setCurrentRMIAddress(String address) {
        configuration.getLocalhost().setCurrentRMIAddress(address);
        configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
    }

    public void setCurrentRMIPort(int rmiPort) {
        configuration.getLocalhost().setCurrentRMIPort(rmiPort);
        configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
    }

    /**
     * update configuration to reflect that host with provided key is no more
     * connected.
     * @param hostKey 
     */
    public void onExit(String hostKey) {

        for (HostConfigurationBean bean : configuration.getOtherHosts()) {
            if (hostKey.equals(bean.getHostKey())) {
                bean.setCurrentRMIAddress(null);
                saveConfiguration(Change.OTHER_HOSTS);
                break;
            }
        }
        // exit if current host is not the main host
        // -----------------------------------------
        if (isMainHost() !=true) {
            return;
        }
        // we notify other hosts if we are the main host
        // ---------------------------------------------
        for (HostConfigurationBean bean : configuration.getOtherHosts()) {
            if (!hostKey.equals(bean.getHostKey())) {
                if (bean.getCurrentRMIAddress() != null) {
                    info.yourhomecloud.network.rmi.Configuration remoteConfiguration;
                    try {
                        remoteConfiguration = RMIUtils.getRemoteConfiguration(bean.getCurrentRMIAddress(), bean.getCurrentRMIPort());
                        remoteConfiguration.onExit(hostKey);
                    } catch (RemoteException | NotBoundException ex) {
                        logger.error("unable to send update to host", ex);
                    }
                }
            }
        }
    }

    public enum Change {

        CREATION,
        NETWORK_INTERFACE,
        MAIN_HOST,
        DIRECTORIES_TO_BE_SAVED,
        OTHER_HOSTS, HOSTNAME,
    }
}
