package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.HostConfigurationBean;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileUtils extends Remote {
    
    public void createDirectoryIfNotExist(String client,String rel) throws RemoteException,IOException ;
    
    boolean isFileExistingAndNotModifiedSince(String client,String rel, long millis) throws IOException;
//
    void copyFile(String client,byte[] file,long modified, String rel) throws IOException;

    boolean isFileExisting(String client,String rel) throws RemoteException,IOException;
    
    public List<HostConfigurationBean> updateHosts(List<HostConfigurationBean> hosts) throws RemoteException;

//    void removeFilesRemovedOnSourceSide(String client,Path source) throws IOException;
    
}
