package info.yourhomecloud;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.gui.MainWindow;
import info.yourhomecloud.network.rmi.RMIUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class YourHomeCloud {

    /**
     * if a directory is provided use this directory for configuration if not
     * use environment standard
     *
     * @param args
     */
    private static void initConfiguration(String... args) {
        final Path confPath;
        if (args == null || args.length < 1) {
            Path userHome = Paths.get((String)System.getProperty("user.home"));
            confPath=userHome.resolve("yourhomecloud");
        } else {
            confPath = Paths.get(args[0]);
        }
        if (!Files.exists(confPath)) {
            try {
                Files.createDirectories(confPath);
            } catch (IOException e) {
                throw new RuntimeException("unable to create configuration diretory", e);
            }
        }
        System.err.println(confPath.toString());
        Configuration.getConfiguration(confPath);
        RMIUtils.getRMIUtils();
    }

    public static void main(String... args) throws InterruptedException {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.TRACE);

        // generate the configuration
        initConfiguration(args);
       
        MainWindow.main(null);
    }

    private static void startMainLoop() throws InterruptedException {
        while (true) {
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
            logger.error("unable to send exit", ex);
        }
    }
    private final static Logger logger = Logger.getLogger(YourHomeCloud.class);
}
