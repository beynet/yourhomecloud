package info.yourhomecloud.configuration;

import info.yourhomecloud.files.impl.DeleteFilesVisitor;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.broadcast.Broadcaster;
import info.yourhomecloud.network.rmi.RMIUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.UUID;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

/**
 * the aim of this class is to reflect the configuration of the application.
 * This class is a singleton constructed by calling method getConfiguration
 * Local configuration and configuration of the remotes hosts are saved.
 *
 * @author beynet
 */
public class Configuration extends Observable {

    private boolean mainHost;

    /**
     * @param configDirectory : must be a directory
     */
    private Configuration(Path configDirectory) {
        mainHost = false;
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
     * @return the path of the directory where the configuration file will be
     * stored and the backups from the other hosts.
     */
    public Path getConfigurationPath() {
        return this.configDirectory;
    }

    /**
     * declare current host as the main host
     */
    public void setMainHost() {
        mainHost = true;
        mainHostRMIAddr = null;
        mainHostRmiPort = 0;
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
        try {
            localHost.setHostName(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException ex) {
        }
        localHost.setLastUpdateDate(new Long(System.currentTimeMillis()));
        try {
            localHost.setNetworkInterface(NetworkUtils.getFirstInterface().getDisplayName());
        } catch (IOException e) {
            throw new RuntimeException("unable to retrieve network interfaces", e);
        }
        configuration.setLocalhost(localHost);
        saveConfiguration(Change.CREATION);
    }

    /**
     * sync the configuration in the associated XML file
     *
     * @param c
     */
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

    /**
     * @return the name of the network interface selected to communicate with
     * other hosts
     */
    public String getNetworkInterface() {
        return configuration.getLocalhost().getNetworkInterface();
    }

    /**
     * change the network interface selected to communicate with other hosts
     *
     * @param i
     */
    public void setNetworkInterface(String i) {
        configuration.getLocalhost().setNetworkInterface(i);
        configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
        // FIXME : restart services
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
     * define the main host - send hosts list known by this host to main host
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
        this.mainHostRMIAddr = hostAddr;
        this.mainHostRmiPort = rmiPort;
        RMIUtils.getRMIUtils();
        logger.info("register main host " + mainHostRMIAddr + " port = " + this.mainHostRmiPort);

        // prepare list of know hosts
        // including current host
        // ---------------------------
        List<HostConfigurationBean> hosts = new ArrayList<>();
        hosts.addAll(configuration.getOtherHosts());
        hosts.add(configuration.getLocalhost());
        // retrieve remote object
        info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(hostAddr, rmiPort);
        // send hosts to main host - in result receive the host list from main host
        List<HostConfigurationBean> updateHosts = remoteConfiguration.updateHosts(hosts, configuration.getLocalhost());

        updateOtherHostsConfiguration(updateHosts, configuration.getLocalhost());
    }

    /**
     * method called before current host stop : the main host is notified that
     * current host is exiting
     *
     * @throws RemoteException
     * @throws NotBoundException
     */
    public void onExit() throws RemoteException, NotBoundException {
        if (isMainHost() == true) {
            logger.info("main host is exiting");
            // stop the broadcaster service
            // ----------------------------
            Broadcaster.stopBroadcaster();
            for (HostConfigurationBean host : Configuration.getConfiguration().getOtherHosts()) {
                if (host.getCurrentRMIAddress() != null) {
                    info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(host.getCurrentRMIAddress(), host.getCurrentRMIPort());
                    try {
                        remoteConfiguration.becomeMainHostAndMarkPreviousAsDisconnected();
                        logger.info("command to switch as main host send");
                        break;
                    } catch (Exception e) {
                        logger.error("unable to send command",e);
                    }
                }
            }
        } else if (this.mainHostRMIAddr != null) {
            info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(this.mainHostRMIAddr, this.mainHostRmiPort);
            remoteConfiguration.onExit(getCurrentHostKey());
        }
    }

    /**
     * @return the main host ip or null if current host is the main host
     */
    public String getMainHostRMIAddr() {
        return this.mainHostRMIAddr;
    }

    public int getMainHostRMIPort() {
        return this.mainHostRmiPort;
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
                    throw new RuntimeException("Directory where configuration files will be stored must be provided at initialization");
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

    public static Map<String, HostConfigurationBean> getHostsMapFromHostsList(List<HostConfigurationBean> otherHosts) {
        Map<String, HostConfigurationBean> map = new HashMap<>();
        for (HostConfigurationBean host : otherHosts) {
            map.put(host.getHostKey(), host);
        }
        return map;
    }

    /**
     * update host list with a list received from another host
     *
     * @param update
     * @param newHost : host from which the modification was received
     * @return
     */
    public synchronized List<HostConfigurationBean> updateOtherHostsConfiguration(List<HostConfigurationBean> update, HostConfigurationBean newHost) {
        Map<String, HostConfigurationBean> newHostsMap = Configuration.getHostsMapFromHostsList(update);
        Map<String, HostConfigurationBean> currentHostsMap = Configuration.getHostsMapFromHostsList(configuration.getOtherHosts());

        for (Entry<String, HostConfigurationBean> entry : newHostsMap.entrySet()) {
            HostConfigurationBean newConf = entry.getValue();
            // we nether update current host configuration
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

        // main host send host configuration modification all hosts
        // except to the host from which the modification was received
        // -----------------------------------------------------------
        if (isMainHost() == true) {
            // notify other hosts
            for (HostConfigurationBean bean : configuration.getOtherHosts()) {
                if (!newHost.getHostKey().equals(bean.getHostKey())) {
                    if (bean.getCurrentRMIAddress() != null) {
                        info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration;
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

    /**
     * remove this host from configuration and also backuped files associated
     * with this account
     *
     * @param hostKey
     */
    public void removeHost(String hostKey) throws IOException {
        if (hostKey == null) {
            return;
        }
        List<HostConfigurationBean> otherHosts = configuration.getOtherHosts();
        HostConfigurationBean found = null;
        for (HostConfigurationBean toRemove : otherHosts) {
            if (hostKey.equals(toRemove.getHostKey())) {
                found = toRemove;
                break;
            }
        }

//        
        Path resolve = getConfigurationPath().resolve(found.getHostKey());
        if (Files.exists(resolve)) {
            System.err.println(resolve);
            Files.walkFileTree(resolve, new DeleteFilesVisitor());
        }

        otherHosts.remove(found);
        saveConfiguration(Change.OTHER_HOSTS);
    }

    protected ConfigurationBean getConfigurationBean() {
        return configuration;
    }
    //    public void add
    private Path configDirectory;
    private String mainHostRMIAddr;
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
     *
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
        if (isMainHost() != true) {
            return;
        }
        // we notify other hosts if we are the main host
        // ---------------------------------------------
        for (HostConfigurationBean bean : configuration.getOtherHosts()) {
            if (!hostKey.equals(bean.getHostKey())) {
                if (bean.getCurrentRMIAddress() != null) {
                    info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration;
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

    /**
     * current host become the main host
     */
    public void becomeMainHostAndMarkPreviousAsDisconnected() throws IOException {
        String previousMainHost = getMainHostRMIAddr();
        int previousPort = getMainHostRMIPort();
        setMainHost();
        Broadcaster.startBroadcaster(NetworkUtils.DEFAULT_BROADCAST_PORT);
        saveConfiguration(Change.MAIN_HOST);
        
        // one loop to remove previous main host
        // ------------------------------------
        for (HostConfigurationBean host : getOtherHosts()) {
            if (previousMainHost.equals(host.getCurrentRMIAddress()) && previousPort == host.getCurrentRMIPort()) {
                host.setCurrentRMIAddress(null);
                host.setCurrentRMIPort(0);
                host.setLastUpdateDate(System.currentTimeMillis());
                saveConfiguration(Change.OTHER_HOSTS);
            }
        }
        // notify connected hosts
        // ----------------------
        logger.debug("notify hosts that main I'm the new main host");
        for (HostConfigurationBean host : getOtherHosts()) {
            if (host.getCurrentRMIAddress() != null) {
                logger.debug("notify host "+host.getHostKey()+" that main I'm the new main host");
                try {
                    RMIUtils.getRemoteConfiguration(host.getCurrentRMIAddress(), host.getCurrentRMIPort()).mainHostAsChanged();
                } catch (NotBoundException ex) {
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
