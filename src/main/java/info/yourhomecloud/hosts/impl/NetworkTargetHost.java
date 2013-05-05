package info.yourhomecloud.hosts.impl;

import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.network.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NetworkTargetHost implements TargetHost {
    
    FileUtils fileUtils;
    
    public NetworkTargetHost(String host,int port) throws RemoteException, NotBoundException {
        String name = FileUtils.class.getCanonicalName();
        Registry registry = LocateRegistry.getRegistry(host, port);
        fileUtils = (FileUtils) registry.lookup(name);
    }

    @Override
    public void createDirectoryIfNotExist(Path rel) throws IOException {
        fileUtils.createDirectoryIfNotExist("truc", rel.toString());
    }

    @Override
    public boolean isFileExistingAndNotModifiedSince(Path rel, long millis) throws IOException {
        return fileUtils.isFileExistingAndNotModifiedSince("truc", rel.toString(), millis);
    }

    @Override
    public void copyFile(Path file, Path rel) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        fileUtils.copyFile("truc", Files.readAllBytes(file), attrs.lastModifiedTime().toMillis(), rel.toString());
    }

    @Override
    public boolean isFileExisting(Path rel) throws IOException {
        return fileUtils.isFileExisting("truc", rel.toString());
    }

    @Override
    public void removeFilesRemovedOnSourceSide(Path source) throws IOException {
        // TODO Auto-generated method stub

    }

}
