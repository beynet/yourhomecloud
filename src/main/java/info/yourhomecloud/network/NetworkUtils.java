package info.yourhomecloud.network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class NetworkUtils {
    /**
     * @return a list of adress current computer is binded with their associated broadcast adresses
     */
    public static List<List<InetAddress>> getAdresses() {
        List<List<InetAddress>> adresses = new ArrayList<List<InetAddress>>();
        Enumeration<NetworkInterface> list;
        try {
            list = NetworkInterface.getNetworkInterfaces();

            while(list.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) list.nextElement();

                if(iface == null) continue;
                if(!iface.isLoopback() && iface.isUp() && !iface.isVirtual()) {
                    Iterator<InterfaceAddress> it = iface.getInterfaceAddresses().iterator();
                    while (it.hasNext()) {
                        InterfaceAddress interfaceAddress = it.next();
                        
                        if(interfaceAddress == null) continue;
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        InetAddress address = interfaceAddress.getAddress();
                        if(broadcast != null && address!=null) {
                            List<InetAddress> found = new ArrayList<InetAddress>();
                            found.add(address);
                            found.add(broadcast);
                            adresses.add(found);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            //            return new ArrayList<InetAddress>();
        }
        return(adresses);
    }
}
