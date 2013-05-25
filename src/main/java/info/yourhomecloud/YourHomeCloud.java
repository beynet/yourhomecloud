package info.yourhomecloud;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.gui.MainWindow;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class YourHomeCloud {
    public static void main(String...args) throws InterruptedException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        if (args==null || args.length<1) {
            printHelp();
            throw new RuntimeException("missing argument");
        }
        Path confPath  = Paths.get(args[0]);
        if (!Files.exists(confPath)) {
            try {
                Files.createDirectories(confPath);
            } catch (IOException e) {
                throw new RuntimeException("unable to create configuration diretory",e);
            }
        }
        Configuration.getConfiguration(confPath);
//        BroadcasterListener broadcasterListener = null ;
//        try {
//            broadcasterListener = new BroadcasterListener(NetworkUtils.DEFAULT_BROADCAST_PORT);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        broadcasterListener.start();
//        startMainLoop();
        MainWindow.main(null);
    }
    
    private static void startMainLoop() throws InterruptedException {
        while(true) {
            Thread.sleep(1000);
        }
    }
    
    private static void printHelp() {
        System.out.println("Usage : [mandatory configuration directory ] options");
    }
    
    public static void quitApplication() {
        try {
            Configuration.getConfiguration().onExit();
        } catch (Exception ex) {
            logger.error("unable to send exit",ex);
        }
    }
    
    private final static Logger logger = Logger.getLogger(YourHomeCloud.class);
}
