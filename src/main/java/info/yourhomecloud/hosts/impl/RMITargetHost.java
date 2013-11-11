package info.yourhomecloud.hosts.impl;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.network.rmi.FileUtils;
import info.yourhomecloud.network.rmi.RMIUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.yourhomecloud.utils.FileTools;
import org.apache.log4j.Logger;

public class RMITargetHost implements TargetHost {

    FileUtils fileUtils;

    public RMITargetHost(String hostKey,String host, int port) throws IOException {
        try {
            fileUtils = RMIUtils.getRemoteFileUtils(hostKey,host, port);
        } catch (RemoteException | NotBoundException ex) {
            throw new IOException("unable to obtain remote object", ex);
        }
    }

    @Override
    public void createDirectoryIfNotExist(Path rel) throws IOException {
        fileUtils.createDirectoryIfNotExist(Configuration.getConfiguration().getCurrentHostKey(), FileTools.getPathListFromPath(rel));
    }

    @Override
    public boolean isFileExistingAndNotModifiedSince(Path rel, long millis) throws IOException {
        return fileUtils.isFileExistingAndNotModifiedSince(Configuration.getConfiguration().getCurrentHostKey(), FileTools.getPathListFromPath(rel), millis);
    }

    protected void copyByChunk(Path file, BasicFileAttributes attrs, Path rel) throws IOException {
        logger.debug("copy by chunk " + file.toString());
        long size = attrs.size();
        long done = 0;
        byte[] bytes = new byte[1024 * 1024];
        InputStream is = Files.newInputStream(file);
        final String currentHostKey = Configuration.getConfiguration().getCurrentHostKey();
        while (done != size) {
            int read = is.read(bytes);
            if (read == -1) {
                break;
            }
            long offset = done;
            done += read;
            final boolean last;
            if (done == size) {
                last = true;
            } else {
                last = false;
            }
            fileUtils.copyFileByChunk(currentHostKey, bytes, offset, read, last, attrs.lastModifiedTime().toMillis(), FileTools.getPathListFromPath(rel));
        }
    }

    @Override
    public void copyFile(Path file, Path rel) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
        if (attrs.size() > (1024L * 1014L)) {
            copyByChunk(file, attrs, rel);
        } else {
            fileUtils.copyFile(Configuration.getConfiguration().getCurrentHostKey(), Files.readAllBytes(file), attrs.lastModifiedTime().toMillis(), FileTools.getPathListFromPath(rel));
        }
    }

    @Override
    public boolean isFileExisting(Path rel) throws IOException {
        return fileUtils.isFileExisting(Configuration.getConfiguration().getCurrentHostKey(), FileTools.getPathListFromPath(rel));
    }

    @Override
    public void removeFilesRemovedOnSourceSide(Path source) throws IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public void listFilesAt(info.yourhomecloud.hosts.File file) throws IOException {
        if (!file.isDirectory()) throw new IllegalArgumentException("file to be visited must be a directory");
        info.yourhomecloud.hosts.File result = fileUtils.listFilesAt(Configuration.getConfiguration().getCurrentHostKey(),file);
        file.getChilds().clear();
        file.getChilds().addAll(result.getChilds());
    }

    Logger logger = Logger.getLogger(RMITargetHost.class);
}
