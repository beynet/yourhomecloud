package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.NetworkUtils;
import java.lang.reflect.Proxy;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

/**
 * this class provides :
 * <ul><li>a singleton (accessible using getRMIUtils method) which create RMI
 * registry and exports mandatory objects</li>
 * <li>two helper static methods getRemoteConfiguration and getRemoteFileUtils
 * to access to objects exported by another host</li>
 * </ul>
 *
 * @author beynet
 */
public class RMIUtils {

    private int rmiPort;
    private Registry registry;
    private String address;

    private RMIUtils() {
        try {
            logger.debug("try to compute local address");
            InetAddress address = NetworkUtils.getAddress(info.yourhomecloud.configuration.Configuration.getConfiguration().getNetworkInterface());
            if (address==null) {
                for (NetworkInterface networkInterface : NetworkUtils.getInterfaces()) {
                    address = NetworkUtils.getAddress(networkInterface.getName());
                    if (address!=null) {
                         Configuration.getConfiguration().setNetworkInterface(networkInterface.getName());
                    }
                }
                if (address==null) {
                    throw new RuntimeException("no active network interface found");
                }
            }
            this.address = address.getHostAddress();
            info.yourhomecloud.configuration.Configuration.getConfiguration().setCurrentRMIAddress(this.address);
            System.setProperty("java.rmi.server.hostname", address.getHostAddress());
            ServerSocket socket = new ServerSocket(0);
            rmiPort = socket.getLocalPort();
            info.yourhomecloud.configuration.Configuration.getConfiguration().setCurrentRMIPort(this.rmiPort);
            socket.close();
        } catch (Exception e) {
            throw new RuntimeException("error constructing rmi server", e);
        }
        try {
            logger.info("Creating rmi registry at port " + rmiPort);
            logger.info("Check if the registry already exists " + rmiPort);
            registry = LocateRegistry.getRegistry(rmiPort);
            try {
                if (registry != null) {
                    UnicastRemoteObject.unexportObject(registry, true);
                }
            } catch (Exception e) {
            }
            logger.info("create new registry " + rmiPort);
            registry = LocateRegistry.createRegistry(rmiPort);
            logger.info("registry created");
            storeObjects();
            logger.info("objects exported");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return this.rmiPort;
    }

    private void storeObjects() throws RemoteException {
        fUtils = new FileUtilsImpl();
        FileUtils fuStub = (FileUtils) UnicastRemoteObject.exportObject(fUtils, rmiPort);
        registry.rebind(FileUtils.class.getCanonicalName(), fuStub);

        rConfig = new RemoteConfigurationImpl();
        RemoteConfiguration confStub = (RemoteConfiguration) UnicastRemoteObject.exportObject(rConfig, rmiPort);
        registry.rebind(RemoteConfiguration.class.getCanonicalName(), confStub);
    }

    public static FileUtils getRemoteFileUtils(String hostKey, String host, int port) throws RemoteException, NotBoundException {
        String name = FileUtils.class.getCanonicalName();
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(host, port);
        } catch(RemoteException e) {
            Configuration.markHostAsDisconnected(hostKey);
            logger.error("unable to obtain registry",e);
            throw e;
        }

        FileUtils fu ;
        try {
            fu=(FileUtils) registry.lookup(name);
        }catch(Exception e ) {
            Configuration.markHostAsDisconnected(hostKey);
            logger.error("unable to perform lookup",e);
            throw e;
        }
        return (FileUtils) Proxy.newProxyInstance(FileUtils.class.getClassLoader(), new Class[]{FileUtils.class}, new RmiProxy(hostKey, fu));
    }

    public static RemoteConfiguration getRemoteConfiguration(String hostKey, String host, int port) throws RemoteException, NotBoundException {
        String name = RemoteConfiguration.class.getCanonicalName();
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(host, port);
        } catch(Exception e) {
            logger.error("unable to obtain registry",e);
            throw e;
        }
        RemoteConfiguration rc ;
        try {
            rc = (RemoteConfiguration) registry.lookup(name);
        }catch(Exception e ) {
            logger.error("unable to perform lookup",e);
            throw e;
        }
        return (RemoteConfiguration) Proxy.newProxyInstance(RemoteConfiguration.class.getClassLoader(), new Class[]{RemoteConfiguration.class}, new RmiProxy(hostKey, rc));
    }

    public static RMIUtils getRMIUtils() {
        if (_rmiUtils == null) {
            synchronized (RMIUtils.class) {
                if (_rmiUtils == null) {
                    _rmiUtils = new RMIUtils();
                }
            }
        }
        return _rmiUtils;
    }
    private final static Logger logger = Logger.getLogger(RMIUtils.class);
    private static RMIUtils _rmiUtils = null;
    private static FileUtils fUtils;
    private static RemoteConfiguration rConfig;
}
