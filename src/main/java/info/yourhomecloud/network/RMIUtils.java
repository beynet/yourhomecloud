package info.yourhomecloud.network;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;


public class RMIUtils {
    private int rmiPort;
    private Registry registry;

    public RMIUtils(int rmiPort) {
        this.rmiPort = rmiPort;
        try {
            logger.info("Creating rmi rgistry at port "+rmiPort);
            registry = LocateRegistry.getRegistry(rmiPort);
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
    
    public void storeObject() throws RemoteException {
        FileUtils fuStub = (FileUtils) UnicastRemoteObject.exportObject(new FileUtilsImpl(),rmiPort);
        registry.rebind(FileUtils.class.getCanonicalName(), fuStub);
//        ShellExecutor _executorStub =(ShellExecutor)UnicastRemoteObject.exportObject(this,rmiPort);
//        registry.rebind("ShellExecutor", _executorStub);
//        System.out.println("Shell executor bound");
    }
    
    private final static Logger logger = Logger.getLogger(RMIUtils.class);
}
