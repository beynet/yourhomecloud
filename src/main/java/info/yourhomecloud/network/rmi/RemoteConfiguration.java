package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.HostConfigurationBean;
import java.io.IOException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
 * provides methos to update a remote host configuration.
 * @author beynet
 */
public interface RemoteConfiguration extends Remote {
    
    /**
     * update remote host configuration.This method is called folowing this sequence :
     * <ul>
     * <li>after connection a new host called this method on the main host exported object</li>
     * <li>Just after, the main host will call this method on the hosts already connected to propagate changes from this new host</li>
     * </ul>
     * @param hosts
     * @return current host configuration including its own configuration
     * @throws RemoteException
     */
    public List<HostConfigurationBean> updateHosts(List<HostConfigurationBean> hosts, HostConfigurationBean newHost) throws RemoteException;
    
    /**
     * declare that the host with the provided key is exiting.
     * @param hostKey
     * @throws RemoteException 
     */
    public void onExit(String hostKey) throws RemoteException;
    
    /**
     * ask to the remote host to become the master host
     * @throws RemoteException 
     */
    public void becomeMainHostAndMarkPreviousAsDisconnected() throws IOException,RemoteException;
    
    /**
     * ask to peer to recompute the main host because
     * main host is disconnected
     * @throws IOException
     * @throws RemoteException 
     */
    public void mainHostAsChanged() throws IOException,RemoteException;
}
