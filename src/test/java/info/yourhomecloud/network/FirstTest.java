package info.yourhomecloud.network;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import info.yourhomecloud.RootTest;
import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.broadcast.Broadcaster;
import info.yourhomecloud.network.broadcast.BroadcasterListener;
import info.yourhomecloud.network.rmi.RMIUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class FirstTest extends RootTest{
    @Test
    public void testAdresses() {
        List<List<InetAddress>> listOfAdressesAndAssociatedBroadcastAdresses = NetworkUtils.getAdresses();
        for (List<InetAddress> adressAndBroadcastAdress : listOfAdressesAndAssociatedBroadcastAdresses ) {
            System.out.println("adress = "+adressAndBroadcastAdress.get(0).getHostAddress()+" broadcast "+adressAndBroadcastAdress.get(1).getHostAddress());
        }
    }
    
    @Test
    public void testBroadcast() throws IOException, InterruptedException {
        Broadcaster brThread = new Broadcaster(31003, 31004);
        brThread.start();
        DatagramSocket datagramSocket = new DatagramSocket(31003);
        byte[] buf = new byte[256];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        datagramSocket.receive(p);
        datagramSocket.close();
        
        String messageString = new String(buf,0,p.getLength());
        
        System.out.println("host from which broadcast was received "+p.getAddress().getHostAddress());
        //expectin message starting with Broadcaster.BROADCAST_BEGIN
        assertThat(Boolean.valueOf(messageString.startsWith(NetworkUtils.BROADCAST_BEGIN)), is(Boolean.TRUE));
       
        String port = messageString.substring(NetworkUtils.BROADCAST_BEGIN.length());
        System.out.println("port read = "+port);
        assertThat(port, is("31004"));
        brThread.interrupt();
        brThread.join();
    }
    
    @Test
    @Ignore
    public void testBroadcastListener() throws IOException, InterruptedException {
        Configuration.getConfiguration().addDirectoryToBeSaved(Paths.get("/home/beynet"));
        BroadcasterListener list = new BroadcasterListener(31003,31004);
        list.start();
        list.join();
    }
    
    @Test
    public void testBroadcast2() throws IOException, InterruptedException {
        Configuration.getConfiguration().addDirectoryToBeSaved(Paths.get("/Users/tata"));
        RMIUtils rmi = new RMIUtils();
        Broadcaster brThread = new Broadcaster(31003, rmi.getPort());
        brThread.start();
        brThread.join();
    }
}
