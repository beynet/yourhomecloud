package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.configuration.HostConfigurationBean;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.hosts.impl.LocalTargetHost;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;

public class FileUtilsImpl implements FileUtils {

    private Path getTargetPathFromClient(String client) {
        return Paths.get("/tmp/DesktopRemoteCP");
    }
    
    private TargetHost getTargetHostFromCient(String client) {
        return new LocalTargetHost(getTargetPathFromClient(client));
    }
    
    @Override
    public void createDirectoryIfNotExist(String client, String rel) throws IOException {
        
        TargetHost target = getTargetHostFromCient(client);
        Path relPath = Paths.get(rel);
        target.createDirectoryIfNotExist(relPath);
    }
    
    @Override
    public boolean isFileExisting(String client, String rel) throws IOException {
        TargetHost target = getTargetHostFromCient(client);
        Path relPath = Paths.get(rel);
        return target.isFileExisting(relPath);
    }
    
    @Override
    public boolean isFileExistingAndNotModifiedSince(String client, String rel, long millis) throws IOException {
        TargetHost target = getTargetHostFromCient(client);
        Path relPath = Paths.get(rel);
        return target.isFileExistingAndNotModifiedSince(relPath, millis);
    }

    @Override
    public void copyFile(String client, byte[] file, long modified, String rel) throws IOException {
        Path target = getTargetPathFromClient(client);
        Path relPath = Paths.get(rel);
        Path filePath = target.resolve(relPath);
        logger.debug("writing file "+filePath);
        Files.write(filePath, file);
        Files.setLastModifiedTime(filePath, FileTime.fromMillis(modified));
    }
    
    private final static Logger logger = Logger.getLogger(FileUtilsImpl.class);
}
