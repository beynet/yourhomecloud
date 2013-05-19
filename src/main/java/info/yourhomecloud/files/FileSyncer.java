package info.yourhomecloud.files;

import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Observer;

public interface FileSyncer {
    /**
     * sync all files in source into target
     * @param source 
     * @param target
     * @throws IOException 
     */
    public void sync(Path source,TargetHost target,Observer ...observers) throws IOException;
}
