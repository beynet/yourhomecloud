package info.yourhomecloud.hosts.impl;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.network.rmi.FileUtils;
import info.yourhomecloud.network.rmi.RMIUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMITargetHost implements TargetHost {
    
    FileUtils fileUtils;
    
    public RMITargetHost(String host,int port) throws IOException {
        try {
            fileUtils = RMIUtils.getRemoteFileUtils(host, port);
        } catch (RemoteException | NotBoundException ex) {
            throw new IOException("unable to obtain remote object",ex);
        }
    }

    @Override
    public void createDirectoryIfNotExist(Path rel) throws IOException {
        fileUtils.createDirectoryIfNotExist(Configuration.getConfiguration().getCurrentHostKey(), rel.toString());
    }

    @Override
    public boolean isFileExistingAndNotModifiedSince(Path rel, long millis) throws IOException {
        return fileUtils.isFileExistingAndNotModifiedSince(Configuration.getConfiguration().getCurrentHostKey(), rel.toString(), millis);
    }

    @Override
    public void copyFile(Path file, Path rel) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        fileUtils.copyFile(Configuration.getConfiguration().getCurrentHostKey(), Files.readAllBytes(file), attrs.lastModifiedTime().toMillis(), rel.toString());
    }

    @Override
    public boolean isFileExisting(Path rel) throws IOException {
        return fileUtils.isFileExisting(Configuration.getConfiguration().getCurrentHostKey(), rel.toString());
    }

    @Override
    public void removeFilesRemovedOnSourceSide(Path source) throws IOException {
        // TODO Auto-generated method stub

    }

}
