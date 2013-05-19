package info.yourhomecloud.files.impl;

import info.yourhomecloud.files.FileSyncer;
import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observer;

public class FileSyncerImpl implements FileSyncer {
    
    @Override
    public void sync(Path source,TargetHost targetHost,Observer ...observers) throws IOException {
        final FileSyncerVisitor visitor = new FileSyncerVisitor(source,targetHost);
        if (observers!=null) {
            for (int i=0;i<observers.length;i++) {
                visitor.addObserver(observers[i]);
            }
        }
        Files.walkFileTree(source, visitor);
        targetHost.removeFilesRemovedOnSourceSide(source);
    }
}
