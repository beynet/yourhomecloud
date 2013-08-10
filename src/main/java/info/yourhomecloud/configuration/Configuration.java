package info.yourhomecloud.configuration;

import info.yourhomecloud.files.impl.DeleteFilesVisitor;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.broadcast.Broadcaster;
import info.yourhomecloud.network.rmi.RMIUtils;
import info.yourhomecloud.network.rmi.RemoteConfiguration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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

    public static void markHostAsDisconnected(final String hostKey) {
        if (hostKey != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Configuration configuration = Configuration.getConfiguration();
                    // main host detect an host error
                    if (configuration.isMainHost()) {
                        configuration.onExit(hostKey);
                    } else {
                        if (hostKey.equals(configuration.mainHostKey)) {
                            try {
                                configuration.becomeMainHostAndMarkPreviousAsDisconnected();
                            } catch (IOException ex) {
                                logger.error("unable to become a main host",ex);
                            }
                        }
                        else {
                            try {
                                RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(configuration.mainHostKey, configuration.mainHostRMIAddr, configuration.mainHostRmiPort);
                                remoteConfiguration.onExit(hostKey);
                            } catch (Exception e) {
                                logger.error("unable to propagate host terminaison to main host");
                            }
                        }
                    }
                }
            };
            new Thread(r).start();
        }
    }

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
        rwLock = new ReentrantReadWriteLock();
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
        mainHostKey = null;
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
        rwLock.writeLock().lock();
        try {
            configuration.getLocalhost().setNetworkInterface(i);
            configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
            // FIXME : restart services
            saveConfiguration(Change.NETWORK_INTERFACE);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * add dir to the list of directories to be backuped
     *
     * @param dir
     */
    public void addDirectoryToBeSaved(Path dir) {
        rwLock.writeLock().lock();
        try {
            dir = dir.toAbsolutePath().normalize();
            if (!configuration.getLocalhost().getDirectoriesToBeSaved().contains(dir.toString())) {
                configuration.getLocalhost().getDirectoriesToBeSaved().add(dir.toString());
                configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
                saveConfiguration(Change.DIRECTORIES_TO_BE_SAVED);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * remove dir from the list of directories to be backuped
     *
     * @param dir
     */
    public void removeDirectoryToBeSaved(Path dir) {
        rwLock.writeLock().lock();
        try {
            dir = dir.toAbsolutePath().normalize();
            String toString = dir.toString();
            if (configuration.getLocalhost().getDirectoriesToBeSaved().contains(toString)) {
                configuration.getLocalhost().getDirectoriesToBeSaved().remove(toString);
                configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
                saveConfiguration(Change.DIRECTORIES_TO_BE_SAVED);
            }
        } finally {
            rwLock.writeLock().unlock();
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

        logger.info("register main host " + mainHostRMIAddr + " port = " + this.mainHostRmiPort);

        // prepare list of know hosts
        // including current host
        // ---------------------------
        List<HostConfigurationBean> hosts = getOtherHostsSnapshot();
        hosts.add(configuration.getLocalhost().clone());
        // retrieve remote object
        info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(null, hostAddr, rmiPort);
        // send hosts to main host - in result receive the host list from main host
        List<HostConfigurationBean> updateHosts = remoteConfiguration.updateHosts(hosts, configuration.getLocalhost());
        logger.info("local config send to main host");
        updateOtherHostsConfiguration(updateHosts, configuration.getLocalhost());
        List<HostConfigurationBean> otherHostsSnapshot = getOtherHostsSnapshot();
        for (HostConfigurationBean host : otherHostsSnapshot) {
            if (this.mainHostRMIAddr.equals(host.getCurrentRMIAddress())
                    && this.mainHostRmiPort == host.getCurrentRMIPort()) {
                this.mainHostKey = host.getHostKey();
                break;
            }
        }
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
            List<HostConfigurationBean> hosts = getOtherHostsSnapshot();

            for (HostConfigurationBean host : hosts) {
                if (host.getCurrentRMIAddress() != null) {
                    info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(host.getHostKey(), host.getCurrentRMIAddress(), host.getCurrentRMIPort());
                    try {
                        remoteConfiguration.becomeMainHostAndMarkPreviousAsDisconnected();
                        logger.info("command to switch as main host send");
                        break;
                    } catch (Exception e) {
                        logger.error("unable to send command to host=" + host.getHostName() + " addr=" + host.getCurrentRMIAddress() + " port=" + host.getCurrentRMIPort(), e);
                    }
                }
            }
        } else if (this.mainHostRMIAddr != null) {
            info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration = RMIUtils.getRemoteConfiguration(this.mainHostKey, this.mainHostRMIAddr, this.mainHostRmiPort);
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

    /**
     * @return other hosts list : modifications done on this list will be
     * reflected on the configuration
     */
    private List<HostConfigurationBean> getOtherHosts() {
        return configuration.getOtherHosts();
    }

    /**
     * @return a copy of configured host list
     */
    public List<HostConfigurationBean> getOtherHostsSnapshot() {
        List<HostConfigurationBean> result = new ArrayList<>();
        rwLock.readLock().lock();
        try {
            for (HostConfigurationBean h : getOtherHosts()) {
                result.add(h.clone());
            }
            return result;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * @return current host key
     */
    public String getCurrentHostKey() {
        return configuration.getLocalhost().getHostKey();
    }

    /**
     * @return current host name
     */
    public String getCurrentHostName() {
        return configuration.getLocalhost().getHostName();
    }

    /**
     * change current host name
     *
     * @param name
     */
    public void setCurrentHostName(String name) {
        rwLock.writeLock().lock();
        try {
            configuration.getLocalhost().setHostName(name);
            configuration.getLocalhost().setLastUpdateDate(System.currentTimeMillis());
            saveConfiguration(Change.HOSTNAME);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * @return the list of directories configured to be backuped
     */
    public List<String> getDirectoriesToBeSavedSnapshot() {
        List<String> directories = new ArrayList<>();
        rwLock.readLock().lock();
        try {
            directories.addAll(configuration.getLocalhost().getDirectoriesToBeSaved());
            return directories;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * update host list with a list received from another host (updater)
     *
     * @param update
     * @param updater : host from which the modification was received
     * @return
     */
    public List<HostConfigurationBean> updateOtherHostsConfiguration(List<HostConfigurationBean> update, HostConfigurationBean updater) {
        String currentHostKey = null;
        List<HostConfigurationBean> results = new ArrayList<>();
        rwLock.writeLock().lock();
        try {
            currentHostKey = configuration.getLocalhost().getHostKey();
            configuration.updateHostList(update, updater);
            saveConfiguration(Change.OTHER_HOSTS);
            results.add(configuration.getLocalhost().clone());
            for (HostConfigurationBean h : getOtherHosts()) {
                results.add(h.clone());
            }
        } finally {
            rwLock.writeLock().unlock();
        }
        // main host  forward the configuration received to all hosts
        // except to the host from which the modification was received
        // -----------------------------------------------------------
        if (isMainHost() == true) {
            // notify other hosts
            for (HostConfigurationBean bean : results) {
                if (!updater.getHostKey().equals(bean.getHostKey()) && !currentHostKey.equals(bean.getHostKey())) {
                    if (bean.getCurrentRMIAddress() != null) {
                        info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration;
                        try {
                            remoteConfiguration = RMIUtils.getRemoteConfiguration(bean.getHostKey(), bean.getCurrentRMIAddress(), bean.getCurrentRMIPort());
                            remoteConfiguration.updateHosts(results, updater);
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
        rwLock.writeLock().lock();
        try {
            getOtherHosts().clear();
        } finally {
            rwLock.writeLock().unlock();
        }
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
        rwLock.writeLock().lock();
        try {
            HostConfigurationBean found = configuration.removeHost(hostKey);
            if (found == null) {
                return;
            }
//        
            Path resolve = getConfigurationPath().resolve(found.getHostKey());
            if (Files.exists(resolve)) {
                System.err.println(resolve);
                Files.walkFileTree(resolve, new DeleteFilesVisitor());
            }
            saveConfiguration(Change.OTHER_HOSTS);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

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
        List<HostConfigurationBean> hosts = null;
        rwLock.writeLock().lock();
        try {
            for (HostConfigurationBean bean : getOtherHosts()) {
                if (hostKey.equals(bean.getHostKey())) {
                    bean.setCurrentRMIAddress(null);
                    bean.setLastUpdateDate(System.currentTimeMillis());
                    saveConfiguration(Change.OTHER_HOSTS);
                    break;
                }
            }
            hosts = getOtherHostsSnapshot();
        } finally {
            rwLock.writeLock().unlock();
        }
        // exit if current host is not the main host
        // -----------------------------------------
        if (isMainHost() != true) {
            return;
        }

        // we notify other hosts if we are the main host
        // ---------------------------------------------
        for (HostConfigurationBean bean : hosts) {
            if (!hostKey.equals(bean.getHostKey())) {
                if (bean.getCurrentRMIAddress() != null) {
                    info.yourhomecloud.network.rmi.RemoteConfiguration remoteConfiguration;
                    try {
                        remoteConfiguration = RMIUtils.getRemoteConfiguration(bean.getHostKey(), bean.getCurrentRMIAddress(), bean.getCurrentRMIPort());
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
        rwLock.writeLock().lock();
        try {
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
            logger.debug("notify hosts that I'm the new main host");
            for (HostConfigurationBean host : getOtherHosts()) {
                if (host.getCurrentRMIAddress() != null) {
                    logger.debug("notify host " + host.getHostKey() + " that I'm the new main host");
                    try {
                        RMIUtils.getRemoteConfiguration(host.getHostKey(), host.getCurrentRMIAddress(), host.getCurrentRMIPort()).mainHostAsChanged();
                    } catch (NotBoundException e) {
                        logger.error("not bound exception when sending manHostAsChanged to host", e);
                    } catch (IOException e) {
                        logger.error("error sending host changed - port=" + host.getCurrentRMIPort() + " address " + host.getCurrentRMIAddress(), e);
                    }
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public enum Change {

        CREATION,
        NETWORK_INTERFACE,
        MAIN_HOST,
        DIRECTORIES_TO_BE_SAVED,
        OTHER_HOSTS, HOSTNAME,
    }
    //    public void add
    private Path configDirectory;
    private String mainHostRMIAddr;
    private int mainHostRmiPort;
    private String mainHostKey;
    private ConfigurationBean configuration;
    private ReadWriteLock rwLock;
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
}
