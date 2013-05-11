package info.yourhomecloud.network.broadcast;

import info.yourhomecloud.network.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import org.apache.log4j.Logger;

public class Broadcaster extends Thread {
    
    public Broadcaster(int port,int rmiPort) throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setBroadcast(true);
        this.port = port;
        this.rmiPort = rmiPort;
        List<List<InetAddress>> adresses = NetworkUtils.getAdresses();
        if (adresses==null || adresses.size()==0) throw new IOException("unable to list current computer network interfaces");
        this.broadCastAdress = adresses.get(0).get(0);
    }
    
    @Override
    public void run() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder(BROADCAST_BEGIN);
        sb.append(rmiPort);
        try {
            os.write(sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("should not happen on a ByteArrayOutputStream",e);
        }
        byte[] buf = os.toByteArray();
        while(true) {
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
    private InetAddress sourceAdress ;
    
    private final static Logger logger = Logger.getLogger(Broadcaster.class);
    
    public final static String BROADCAST_BEGIN = "yourhomecloud=";
}
