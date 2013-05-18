package info.yourhomecloud.network.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.Logger;

public class BroadcasterListener extends Thread{
    public BroadcasterListener(int port,int rmiPort) throws IOException {
        datagramSocket = new DatagramSocket(port);
        datagramSocket.setBroadcast(true);
        datagramSocket.setSoTimeout(30*1000);
    }
    
    @Override
    public void run() {
        
        byte[] buf = new byte[256];
        DatagramPacket p = new DatagramPacket(buf, buf.length);
        try {
            datagramSocket.receive(p);
        }catch(IOException e) {
            logger.info("no message received - starting broadcaster thread");
            return;
        }
        datagramSocket.close();
        String messageString = new String(buf,0,p.getLength());
        logger.info("host from which broadcast was received "+p.getAddress().getHostAddress());
        
    }
    
    private final static Logger logger = Logger.getLogger(BroadcasterListener.class);
    private DatagramSocket datagramSocket;
}
