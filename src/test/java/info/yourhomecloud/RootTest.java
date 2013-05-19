package info.yourhomecloud;

import info.yourhomecloud.configuration.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class RootTest {
    static {
        String tmp = System.getProperty("java.io.tmpdir");
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        Path conf = Paths.get(tmp).resolve("yourhomecloud");
        try {
            Files.createDirectories(conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Configuration.getConfiguration(conf);
    }


}
