package info.yourhomecloud.network.broadcast;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.network.NetworkUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;

import org.apache.log4j.Logger;

public class BroadcasterListener extends Observable implements Runnable {

    private int port;

    public BroadcasterListener(int port) throws IOException {
        this.port = port;
        datagramSocket = new DatagramSocket(port);
        datagramSocket.setBroadcast(true);
        datagramSocket.setSoTimeout(10 * 1000);
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
                new Broadcaster(port).start();
            } catch (IOException e1) {
                throw new RuntimeException("unable to start broadcaster thread", e1);
            }
            this.setChanged();
            this.notifyObservers();
            return;
        }
        datagramSocket.close();
        String messageString = new String(buf, 0, p.getLength());
        logger.info("host from which broadcast was received " + p.getAddress().getHostAddress());
        String port = messageString.substring(NetworkUtils.BROADCAST_BEGIN.length());
        try {
            Configuration.getConfiguration().setMainHostAndUpdateHostsList(p.getAddress().getHostAddress(), Integer.valueOf(port).intValue());
            this.setChanged();
            this.notifyObservers();
        } catch (Exception e) {
            logger.error("error updating main host", e);
        }
    }
    private final static Logger logger = Logger.getLogger(BroadcasterListener.class);
    private DatagramSocket datagramSocket;
}
