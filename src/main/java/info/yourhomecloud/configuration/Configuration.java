package info.yourhomecloud.configuration;

import info.yourhomecloud.files.impl.FileSyncerImpl;
import info.yourhomecloud.hosts.impl.NetworkTargetHost;
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
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

public class Configuration {

    /**
     * @param configDirectory : must be a directory
     */
    private Configuration(Path configDirectory) {
        this.configDirectory = configDirectory ;
        if (!Files.exists(configDirectory)) throw new IllegalArgumentException("Directory "+configDirectory+" does not exist");
        if (!Files.isDirectory(configDirectory)) throw new IllegalArgumentException("File "+configDirectory+" is not a directory");
        readConfiguration();
    }
    
    public Path getConfigurationPath() {
        return this.configDirectory;
    }

    /**
     * try to read XML configuration file - create a default new file if this file does not exist.
     */
    private void readConfiguration() {
        Path configFile = this.configDirectory.resolve("yourhomecloud.xml");
        if (Files.exists(configFile)) {
            Unmarshaller unm;
            try {
                unm = context.createUnmarshaller();
            } catch (JAXBException e) {
                throw new RuntimeException("unable to create an unmarshaller",e);
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
        configuration.setLocalhost(localHost);
        saveConfiguration();
    }

    private void saveConfiguration() {
        Path configFile = this.configDirectory.resolve("yourhomecloud.xml");
        try {
            context.createMarshaller().marshal(configuration, configFile.toFile());
        } catch (JAXBException e) {
            logger.error("unable to create new configuration file",e);
            throw new RuntimeException(e);
        }
    }

    public void addDirectoryToBeSaved(Path dir) {
        if (!configuration.getLocalhost().getDirectoriesToBeSaved().contains(dir.toString())) {
            configuration.getLocalhost().getDirectoriesToBeSaved().add(dir.toString());
            saveConfiguration();
        }
    }

    public void setMainHost(String hostAddr,int rmiPort) throws RemoteException, NotBoundException {
        this.mainHostAddr = hostAddr;
        this.mainHostRmiPort = rmiPort;
        logger.info("register main host "+mainHostAddr+" port = "+this.mainHostRmiPort);
        List<HostConfigurationBean> hosts = new ArrayList<>();
        hosts.addAll(configuration.getOtherHosts());
        hosts.add(configuration.getLocalhost());
        info.yourhomecloud.network.rmi.Configuration remoteConfiguration = RMIUtils.getRemoteConfiguration(hostAddr, rmiPort);
        List<HostConfigurationBean> updateHosts = remoteConfiguration.updateHosts(hosts);
        updateOtherHostsConfiguration(updateHosts);
    }
    
    public void saveLocalFilesToMainHost() throws RemoteException, IOException, NotBoundException {
        FileSyncerImpl fs = new FileSyncerImpl();
        for (String dir : configuration.getLocalhost().getDirectoriesToBeSaved()) {
            fs.sync(Paths.get(dir), new NetworkTargetHost(this.mainHostAddr, this.mainHostRmiPort));
        }
    }

    /**
     * @param paths : optional except for the first call to initiate the configuration
     * @return the configuration singleton
     */
    public static Configuration getConfiguration(Path...paths) {
        synchronized (Configuration.class) {
            if (_configuration==null) {
                if (paths.length==0) throw new RuntimeException("Directory where configuration files will be stored must be provide at initialization");
                _configuration = new Configuration(paths[0]);
            }
        }
        return _configuration;
    }


    /**
     * @return current host key
     */
    public String getCurrentHostKey() {
        return configuration.getLocalhost().getHostKey();
    }

    /**
     * update host list of host known by current host.
     * @param update
     * @return
     */
    public List<HostConfigurationBean> updateOtherHostsConfiguration(List<HostConfigurationBean> update) {
        Map<String, HostConfigurationBean> newHostsMap     = ConfigurationBean.getOtherHostsMap(update);
        Map<String, HostConfigurationBean> currentHostsMap = ConfigurationBean.getOtherHostsMap(configuration.getOtherHosts());

        for (Entry<String, HostConfigurationBean> entry : newHostsMap.entrySet()) {
            HostConfigurationBean newConf     = entry.getValue();
            if (configuration.getLocalhost().getHostKey().equals(newConf.getHostKey())) continue;
            HostConfigurationBean currentConf = currentHostsMap.get(entry.getKey());
            if (currentConf==null) {
                currentHostsMap.put(entry.getKey(), newConf);
            }
            else {
                if (currentConf.getLastUpdateDate()==null) currentConf.setLastUpdateDate(Long.valueOf(0));
                if (newConf.getLastUpdateDate()==null) newConf.setLastUpdateDate(Long.valueOf(0));
                if (currentConf.getLastUpdateDate().compareTo(newConf.getLastUpdateDate())<0 ) {
                    currentHostsMap.put(entry.getKey(), newConf);
                }
            }
        }
        configuration.getOtherHosts().clear();
        configuration.getOtherHosts().addAll(currentHostsMap.values());
        saveConfiguration();
        List<HostConfigurationBean> results = new ArrayList<>();
        results.add(configuration.getLocalhost());
        results.addAll(configuration.getOtherHosts());
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
    private String mainHostAddr ;
    private int    mainHostRmiPort;
    private ConfigurationBean configuration ;

    // JAXB configuration
    private final static JAXBContext context;
    static {
        try {
            context = JAXBContext.newInstance(ConfigurationBean.class);
        } catch (JAXBException e) {
            throw new RuntimeException("unable to generate jaxb context for configuration",e);
        }
    }
    private static Configuration _configuration = null ;
    private static final Logger logger = Logger.getLogger(Configuration.class);
}
