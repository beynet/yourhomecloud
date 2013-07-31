package info.yourhomecloud.network.rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileUtils extends Remote {
    /**
     * ask to remote host to create directory specified if not already existing
     * @param client
     * @param rel
     * @throws RemoteException
     * @throws IOException 
     */
    public void createDirectoryIfNotExist(String client,String rel) throws RemoteException,IOException ;
    /**
     * ask to remote server if remote file exist and was not modified since specified date
     * @param client
     * @param rel
     * @param millis
     * @return
     * @throws RemoteException
     * @throws IOException 
     */
    boolean isFileExistingAndNotModifiedSince(String client,String rel, long millis) throws RemoteException,IOException;

    /**
     * copy specified file on the remote host
     * @param client
     * @param file
     * @param modified
     * @param rel
     * @throws RemoteException
     * @throws IOException 
     */
    void copyFile(String client,byte[] file,long modified, String rel) throws RemoteException,IOException;
    
    /**
     * copy by chunk specified file
     * @param client
     * @param file
     * @param offset
     * @param length
     * @param last
     * @param modified
     * @param rel
     * @throws RemoteException
     * @throws IOException 
     */
    void copyFileByChunk(String client,byte[] file,long offset,int length,boolean last,long modified, String rel) throws RemoteException,IOException;

    /**
     * ask to remote host if specified file existsil faut 
     * @param client
     * @param rel
     * @return
     * @throws RemoteException
     * @throws IOException 
     */
    boolean isFileExisting(String client,String rel) throws RemoteException,IOException;

//    void removeFilesRemovedOnSourceSide(String client,Path source) throws IOException;
    
}
