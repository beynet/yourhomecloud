package info.yourhomecloud;

import info.yourhomecloud.configuration.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YourHomeCloud {
    public static void main(String...args) {
        if (args==null || args.length<1) {
            printHelp();
            throw new RuntimeException("missing argument");
        }
        Path confPath  = Paths.get(args[0]);
        if (!Files.exists(confPath))
            try {
                Files.createDirectories(confPath);
            } catch (IOException e) {
                throw new RuntimeException("unable to create configuration diretory",e);
            }
        Configuration.getConfiguration(confPath);
        
    }
    
    private static void printHelp() {
        System.out.println("Usage : [mandatory configuration directory ] options");
    }
}
