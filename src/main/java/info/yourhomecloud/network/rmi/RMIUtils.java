package info.yourhomecloud.network.rmi;

import java.net.ServerSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;


public class RMIUtils {
    private int rmiPort;
    private Registry registry;

    
    public RMIUtils() {
        try {
          ServerSocket socket= new ServerSocket(0);
          rmiPort = socket.getLocalPort();
          socket.close(); 
        } catch (Exception e) { rmiPort = -1; }
        try {
            logger.info("Creating rmi rgistry at port "+rmiPort);
            registry = LocateRegistry.getRegistry(0);
            try {
                if (registry!=null) UnicastRemoteObject.unexportObject(registry,true);
            }catch(Exception e) {
                
            }
            registry = LocateRegistry.createRegistry(rmiPort);
            logger.info("registry created");
            storeObject();
        }catch(RemoteException e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getPort() {
        return this.rmiPort;
    }
    
    public void storeObject() throws RemoteException {
        FileUtils fuStub = (FileUtils) UnicastRemoteObject.exportObject(new FileUtilsImpl(),rmiPort);
        registry.rebind(FileUtils.class.getCanonicalName(), fuStub);
//        ShellExecutor _executorStub =(ShellExecutor)UnicastRemoteObject.exportObject(this,rmiPort);
//        registry.rebind("ShellExecutor", _executorStub);
//        System.out.println("Shell executor bound");
    }
    
    public static FileUtils getRemoteFileUtils(String host,int port) throws RemoteException, NotBoundException {
        String name = FileUtils.class.getCanonicalName();
        Registry registry = LocateRegistry.getRegistry(host, port);
        return((FileUtils) registry.lookup(name));
    }
    
    
    
    private final static Logger logger = Logger.getLogger(RMIUtils.class);
}
