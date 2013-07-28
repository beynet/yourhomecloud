package info.yourhomecloud.hosts.impl;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.network.rmi.FileUtils;
import info.yourhomecloud.network.rmi.RMIUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import org.apache.log4j.Logger;

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

    
    protected void copyByChunk(Path file,BasicFileAttributes attrs,Path rel) throws IOException {
        logger.debug("copy by chunk "+file.toString());
        long size = attrs.size();
        long done = 0 ;
        byte[] bytes = new byte[1024*1024];
        InputStream is = Files.newInputStream(file);
        final String currentHostKey = Configuration.getConfiguration().getCurrentHostKey();
        while (done!=size) {
            int read = is.read(bytes);
            if (read==-1) break;
            long offset = done;
            done+=read;
            final boolean last;
            if (done==size) last = true;
            else last = false;
            fileUtils.copyFileByChunk(currentHostKey, bytes,offset,read, last,attrs.lastModifiedTime().toMillis(), rel.toString());
        }
    }
    
    @Override
    public void copyFile(Path file, Path rel) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        if (attrs.size()>(1024L*1014L)) {
            copyByChunk(file, attrs, rel);
        } else {
            fileUtils.copyFile(Configuration.getConfiguration().getCurrentHostKey(), Files.readAllBytes(file), attrs.lastModifiedTime().toMillis(), rel.toString());
        }
    }

    @Override
    public boolean isFileExisting(Path rel) throws IOException {
        return fileUtils.isFileExisting(Configuration.getConfiguration().getCurrentHostKey(), rel.toString());
    }

    @Override
    public void removeFilesRemovedOnSourceSide(Path source) throws IOException {
        // TODO Auto-generated method stub

    }

    Logger logger = Logger.getLogger(RMITargetHost.class);
}
