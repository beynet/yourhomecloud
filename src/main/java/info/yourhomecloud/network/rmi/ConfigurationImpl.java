package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.HostConfigurationBean;

import java.rmi.RemoteException;
import java.util.List;

public class ConfigurationImpl implements Configuration {

    @Override
    public List<HostConfigurationBean> updateHosts(List<HostConfigurationBean> hosts) throws RemoteException {
        return(info.yourhomecloud.configuration.Configuration.getConfiguration().updateOtherHostsConfiguration(hosts));
    }

}
