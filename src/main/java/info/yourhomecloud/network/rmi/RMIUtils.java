package info.yourhomecloud.network.rmi;

import info.yourhomecloud.network.NetworkUtils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

/**
 * this class provides :
 *  <ul><li>a singleton (accessible using getRMIUtils method) which create RMI registry and exports mandatory objects</li>
 *  <li>two helper static methods getRemoteConfiguration and getRemoteFileUtils to access to objects exported by another host</li>
 *  </ul>
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
            this.address = address.getHostAddress();
            info.yourhomecloud.configuration.Configuration.getConfiguration().setCurrentRMIAddress(this.address);
            System.setProperty("java.rmi.server.hostname", address.getHostAddress());
            ServerSocket socket= new ServerSocket(0);
            rmiPort = socket.getLocalPort();
            info.yourhomecloud.configuration.Configuration.getConfiguration().setCurrentRMIPort(this.rmiPort);
            socket.close(); 
        } catch (Exception e) { 
            throw new RuntimeException("error constructing rmi server",e) ;
        }
        try {
            logger.info("Creating rmi registry at port "+rmiPort);
            registry = LocateRegistry.getRegistry(rmiPort);
            try {
                if (registry!=null) UnicastRemoteObject.unexportObject(registry,true);
            }catch(Exception e) {

            }
            registry = LocateRegistry.createRegistry(rmiPort);
            logger.info("registry created");
            storeObjects();
            logger.info("objects exported");
        }catch(RemoteException e) {
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
        FileUtils fuStub = (FileUtils) UnicastRemoteObject.exportObject(new FileUtilsImpl(),rmiPort);
        registry.rebind(FileUtils.class.getCanonicalName(), fuStub);
        
        Configuration confStub = (Configuration) UnicastRemoteObject.exportObject(new ConfigurationImpl(),rmiPort);
        registry.rebind(Configuration.class.getCanonicalName(), confStub);
    }

    public static FileUtils getRemoteFileUtils(String host,int port) throws RemoteException, NotBoundException {
        String name = FileUtils.class.getCanonicalName();
        Registry registry = LocateRegistry.getRegistry(host, port);
        return((FileUtils) registry.lookup(name));
    }

    public static Configuration getRemoteConfiguration(String host,int port) throws RemoteException, NotBoundException {
        String name = Configuration.class.getCanonicalName();
        Registry registry = LocateRegistry.getRegistry(host, port);
        return((Configuration) registry.lookup(name));
    }
    
    public static RMIUtils getRMIUtils() {
        if (_rmiUtils==null) {
            synchronized (RMIUtils.class) {
                if (_rmiUtils==null) {
                    _rmiUtils = new RMIUtils();
                }
            }
        }
        return _rmiUtils;
    }

    private final static Logger logger = Logger.getLogger(RMIUtils.class);
    private  static RMIUtils _rmiUtils = null;
}
