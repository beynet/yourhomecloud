package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.HostConfigurationBean;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Configuration extends Remote {
    
    /**
     * update current host configuration.This method is called folowing this sequence :
     * <ul>
     * <li>A new host called this method on the main host</li>
     * <li>Just after, the main host will call this method on the hosts already connected</li>
     * </ul>
     * @param hosts
     * @return current host configuration including its own configuration
     * @throws RemoteException
     */
    public List<HostConfigurationBean> updateHosts(List<HostConfigurationBean> hosts, HostConfigurationBean newHost) throws RemoteException;
    
    /**
     * declare that the host with provided key is exiting.
     * @param hostKey
     * @throws RemoteException 
     */
    public void onExit(String hostKey) throws RemoteException;
}
