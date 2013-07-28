package info.yourhomecloud.network.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileUtils extends Remote {
    
    public void createDirectoryIfNotExist(String client,String rel) throws RemoteException,IOException ;
    
    boolean isFileExistingAndNotModifiedSince(String client,String rel, long millis) throws RemoteException,IOException;
//
    void copyFile(String client,byte[] file,long modified, String rel) throws RemoteException,IOException;
    
    void copyFileByChunk(String client,byte[] file,long offset,int length,boolean last,long modified, String rel) throws RemoteException,IOException;

    boolean isFileExisting(String client,String rel) throws RemoteException,IOException;

//    void removeFilesRemovedOnSourceSide(String client,Path source) throws IOException;
    
}
