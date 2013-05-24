package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.HostConfigurationBean;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Configuration extends Remote {
    
    /**
     * update current host configuration
     * @param hosts
     * @return current host configuration including its own configuration
     * @throws RemoteException
     */
    public List<HostConfigurationBean> updateHosts(List<HostConfigurationBean> hosts, HostConfigurationBean newHost) throws RemoteException;
    
    
    public void onExit(String hostKey) throws RemoteException;
}
