package info.yourhomecloud.network.broadcast;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.NetworkUtils;
import info.yourhomecloud.network.rmi.RMIUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import org.apache.log4j.Logger;

public class Broadcaster extends Thread {

    private static Broadcaster _instance = null;

    public static Broadcaster startBroadcaster(int port) throws IOException {
        _instance = new Broadcaster(port);
        _instance.start();
        return _instance;
    }

    public static void stopBroadcaster() {
        _instance.interrupt();
    }

    private Broadcaster(int port) throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setBroadcast(true);
        this.port = port;
    }

    private void sendMessage(byte[] buf, InetAddress broadCastAdress) {
        logger.trace("sending broadcast packet to " + broadCastAdress.getHostAddress() + " port=" + port);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, broadCastAdress, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException("Error sending to network broadcast packet");
        }
    }

    @Override
    public void run() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder(NetworkUtils.BROADCAST_BEGIN);
        sb.append(RMIUtils.getRMIUtils().getPort());
        try {
            os.write(sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("should not happen on a ByteArrayOutputStream", e);
        }
        byte[] buf = os.toByteArray();
        InetAddress broadcastAddress;
        try {
            broadcastAddress = NetworkUtils.getBroadcastAddress(Configuration.getConfiguration().getNetworkInterface());
        } catch (IOException ex) {
            throw new RuntimeException("unable to retrieve network address from interface name", ex);
        }
        while (true) {
            sendMessage(buf, broadcastAddress);
            if (this.isInterrupted()) {
                logger.info("interruption detected");
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                logger.info("interruption detected");
                break;
            }
        }
        logger.info("end of thread");
    }
    private DatagramSocket socket;
    private int port;
    private final static Logger logger = Logger.getLogger(Broadcaster.class);
}
