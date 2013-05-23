package info.yourhomecloud.network;

import java.io.IOException;
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
     * @return a list of adress current computer is binded with their associated
     * broadcast adresses
     */
    private static List<List<InetAddress>> getAdresses() {
        List<List<InetAddress>> adresses = new ArrayList<List<InetAddress>>();
        Enumeration<NetworkInterface> list;
        try {
            list = NetworkInterface.getNetworkInterfaces();

            while (list.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) list.nextElement();

                if (iface == null) {
                    continue;
                }
                if (!iface.isLoopback() && iface.isUp() && !iface.isVirtual() && !iface.isPointToPoint()) {
                    Iterator<InterfaceAddress> it = iface.getInterfaceAddresses().iterator();
                    while (it.hasNext()) {
                        InterfaceAddress interfaceAddress = it.next();

                        if (interfaceAddress == null) {
                            continue;
                        }
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        InetAddress address = interfaceAddress.getAddress();
                        if (broadcast != null && address != null) {
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
        return (adresses);
    }
    
    public static List<NetworkInterface> getInterfaces() throws IOException {
        List <NetworkInterface> result = new ArrayList<>();
        Enumeration<NetworkInterface> list;
        list = NetworkInterface.getNetworkInterfaces();

        while (list.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface) list.nextElement();
            if (!iface.isLoopback() && iface.isUp() && !iface.isVirtual() && !iface.isPointToPoint()) {
                result.add(iface);
            }
        }
        return result;
    }
    
    public static NetworkInterface getFirstInterface() throws IOException {
        NetworkInterface result = null;
        Enumeration<NetworkInterface> list;
        list = NetworkInterface.getNetworkInterfaces();

        while (list.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface) list.nextElement();
            if (!iface.isLoopback() && iface.isUp() && !iface.isVirtual() && !iface.isPointToPoint()) {
                result = iface;
                break;
            }
        }
        return result;
    }
    
    private static NetworkInterface getInterface(String interfaceName) throws IOException {
        NetworkInterface result = null;
        Enumeration<NetworkInterface> list;
        list = NetworkInterface.getNetworkInterfaces();

        while (list.hasMoreElements()) {
            NetworkInterface iface = (NetworkInterface) list.nextElement();
            if (interfaceName.equals(iface.getName())) {
                result = iface;
                break;
            }
        }
        return result;
    }

    public static InetAddress getBroadcastAddress(String interfaceName) throws IOException {
        NetworkInterface aInterface = getInterface(interfaceName);
        InetAddress result = null;
        if (aInterface == null) {
            return null;
        }
        Iterator<InterfaceAddress> it = aInterface.getInterfaceAddresses().iterator();
        while (it.hasNext()) {
            InterfaceAddress interfaceAddress = it.next();

            if (interfaceAddress == null) {
                continue;
            }
            InetAddress broadcast = interfaceAddress.getBroadcast();
            if (broadcast!=null) {
                result = broadcast;
                break;
            }
        }
        return result;
    }

    public static InetAddress getAddress(String interfaceName) throws IOException {
         NetworkInterface aInterface = getInterface(interfaceName);
        InetAddress result = null;
        if (aInterface == null) {
            return null;
        }
        Iterator<InterfaceAddress> it = aInterface.getInterfaceAddresses().iterator();
        while (it.hasNext()) {
            InterfaceAddress interfaceAddress = it.next();

            if (interfaceAddress == null) {
                continue;
            }
            InetAddress add = interfaceAddress.getAddress();
            if (add!=null) {
                result = add;
                break;
            }
        }
        return result;
    }

    public static InetAddress getFirstBroadcastAddress() throws IOException {
        List<List<InetAddress>> adresses = NetworkUtils.getAdresses();
        if (adresses == null || adresses.isEmpty()) {
            throw new IOException("unable to list current computer network interfaces");
        }
        return adresses.get(0).get(1);
    }

    public static List<InetAddress> getBroadcastAddresses() throws IOException {
        List<InetAddress> result = new ArrayList<>();
        List<List<InetAddress>> adresses = NetworkUtils.getAdresses();
        if (adresses == null || adresses.size() == 0) {
            throw new IOException("unable to list current computer network interfaces");
        }
        for (List<InetAddress> l : adresses) {
            result.add(l.get(1));
        }
        return result;
    }

    public static InetAddress getFirstAddress() throws IOException {
        List<List<InetAddress>> adresses = NetworkUtils.getAdresses();
        if (adresses == null || adresses.size() == 0) {
            throw new IOException("unable to list current computer network interfaces");
        }
        return adresses.get(0).get(0);
    }
    public static final int DEFAULT_BROADCAST_PORT = 31003;
    public final static String BROADCAST_BEGIN = "yourhomecloud=";
}
