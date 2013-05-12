package info.yourhomecloud.configuration;

import java.nio.file.Path;

public class Configuration {

    private Configuration(Path configDirectory) {
        this.configDirectory = configDirectory ;
        readConfiguration();
    }
    
    public void setMainHost(String hostAddr,int rmiPort) {
        this.mainHostAddr = hostAddr;
        this.mainHostRmiPort = rmiPort;
    }
    
    public static Configuration getConfiguration(Path...paths) {
        synchronized (Configuration.class) {
            if (_configuration==null) {
                if (paths.length==0) throw new RuntimeException("Directory where configuration files will be stored must be provide at initialization");
                _configuration = new Configuration(paths[0]);
            }
        }
        return _configuration;
    }
    
    
    
    private Path configDirectory;
    private String mainHostAddr ;
    private int mainHostRmiPort;
    
    private static Configuration _configuration = null ;
}
