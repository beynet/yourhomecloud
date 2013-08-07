package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.HostConfigurationBean;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.broadcast.BroadcasterListener;
import java.io.IOException;

import java.rmi.RemoteException;
import java.util.List;

public class RemoteConfigurationImpl implements RemoteConfiguration {

    @Override
    public List<HostConfigurationBean> updateHosts(List<HostConfigurationBean> hosts, HostConfigurationBean newHost) throws RemoteException {
        return (info.yourhomecloud.configuration.Configuration.getConfiguration().updateOtherHostsConfiguration(hosts, newHost));
    }

    @Override
    public void onExit(String hostKey) {
        final info.yourhomecloud.configuration.Configuration configuration = info.yourhomecloud.configuration.Configuration.getConfiguration();
//        info.yourhomecloud.configuration.Configuration.getHostsMapFromHostsList(configuration.getOtherHosts());
//        configuration.
        configuration.onExit(hostKey);
    }

    @Override
    public void becomeMainHostAndMarkPreviousAsDisconnected() throws IOException, RemoteException {
        final info.yourhomecloud.configuration.Configuration configuration = info.yourhomecloud.configuration.Configuration.getConfiguration();
        configuration.becomeMainHostAndMarkPreviousAsDisconnected();
    }

    @Override
    public void mainHostAsChanged() throws RemoteException, IOException {
        final BroadcasterListener broadcasterListener = new BroadcasterListener(NetworkUtils.DEFAULT_BROADCAST_PORT);
        final Thread thread = new Thread(broadcasterListener);
        thread.start();
    }
}
