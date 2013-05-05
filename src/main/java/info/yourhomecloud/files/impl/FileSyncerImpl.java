package info.yourhomecloud.files.impl;

import info.yourhomecloud.files.FileSyncer;
import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSyncerImpl implements FileSyncer {
    
    @Override
    public void sync(Path source,TargetHost targetHost) throws IOException {
        final FileSyncerVisitor visitor = new FileSyncerVisitor(source,targetHost);
        Files.walkFileTree(source, visitor);
        targetHost.removeFilesRemovedOnSourceSide(source);
    }
}
