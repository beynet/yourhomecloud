package info.yourhomecloud.network.broadcast;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.NetworkUtils;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import org.apache.log4j.Logger;

public class BroadcasterListener implements Runnable {

    private int port;

    public BroadcasterListener(int port) throws IOException {
        this.port = port;
        datagramSocket = new DatagramSocket(null);
        datagramSocket.setReuseAddress(true);
        datagramSocket.bind(new InetSocketAddress(port));
        datagramSocket.setBroadcast(true);
        datagramSocket.setSoTimeout(3 * 1000);
    }

    @Override
    public void run() {

        byte[] buf = new byte[256];
        logger.info("check if a broadcaster is online");
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        try {
            datagramSocket.receive(p);
        } catch (IOException e) {
            datagramSocket.close();
            logger.info("no message received - starting broadcaster thread");
            try {
                Broadcaster.startBroadcaster(port);
                try {
                    Configuration.getConfiguration().setMainHostAndUpdateHostsList(null,0);
                } catch (Exception ex) {
                    throw new RuntimeException("programatic error detected - no exception should be thrown here",ex);
                } 
            } catch (IOException e1) {
                throw new RuntimeException("unable to start broadcaster thread", e1);
            }
            return;
        }
        datagramSocket.close();
        String messageString = new String(buf, 0, p.getLength());
        final String hostAddress = p.getAddress().getHostAddress();
        logger.info("host from which broadcast was received " + hostAddress);
        String port = messageString.substring(NetworkUtils.BROADCAST_BEGIN.length());
        try {
            Configuration.getConfiguration().setMainHostAndUpdateHostsList(hostAddress, Integer.valueOf(port).intValue());
        } catch (Exception e) {
            logger.error("error updating main host", e);
        }
    }
    private final static Logger logger = Logger.getLogger(BroadcasterListener.class);
    private DatagramSocket datagramSocket;
}
