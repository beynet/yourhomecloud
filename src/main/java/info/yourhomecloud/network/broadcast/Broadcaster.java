package info.yourhomecloud.network.broadcast;

import info.yourhomecloud.network.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

public class Broadcaster extends Thread {
    
    public Broadcaster(int port,int rmiPort) throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setBroadcast(true);
        this.port = port;
        this.rmiPort = rmiPort;
        this.broadCastAdress = NetworkUtils.getFirstBroadcastAddress();
    }
    
    @Override
    public void run() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder(NetworkUtils.BROADCAST_BEGIN);
        sb.append(rmiPort);
        try {
            os.write(sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("should not happen on a ByteArrayOutputStream",e);
        }
        byte[] buf = os.toByteArray();
        while(true) {
            logger.debug("sending broadcast packet to "+broadCastAdress.getHostAddress()+" port="+port);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, broadCastAdress, port);
            try {
                this.socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException("Error sending to network broadcast packet");
            }
            if (this.isInterrupted()) {
                logger.info("interruption detected");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.info("interruption detected");
                break;
            }
        }
        logger.info("end of thread");
    }
    
    private int rmiPort;
    private DatagramSocket socket;
    private int port ;
    private InetAddress broadCastAdress ;
    
    private final static Logger logger = Logger.getLogger(Broadcaster.class);
    
}
