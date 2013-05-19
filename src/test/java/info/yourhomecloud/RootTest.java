package info.yourhomecloud;

import info.yourhomecloud.configuration.Configuration;

import java.nio.file.Paths;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class RootTest {
    static {
        String tmp = System.getProperty("java.io.tmpdir");
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        Configuration configuration = 
                Configuration.getConfiguration(Paths.get(tmp).resolve("yourhomecloud"));
    }


}
