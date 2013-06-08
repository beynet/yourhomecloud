package info.yourhomecloud.files.impl;

import info.yourhomecloud.files.FileSyncer;
import info.yourhomecloud.files.events.EndOfSync;
import info.yourhomecloud.files.events.StartOfCopy;
import info.yourhomecloud.files.events.StartOfSync;
import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

public class FileSyncerImpl extends Observable implements FileSyncer {
    
    @Override
    public void sync(Path source,TargetHost targetHost,Observer ...observers) throws IOException {
        final FileSyncerVisitor visitor = new FileSyncerVisitor(source,targetHost);
        if (observers!=null) {
            for (int i=0;i<observers.length;i++) {
                visitor.addObserver(observers[i]);
                this.addObserver(observers[i]);
            }
        }
        setChanged();
        notifyObservers(new StartOfSync(source));
        Files.walkFileTree(source, visitor);
        targetHost.removeFilesRemovedOnSourceSide(source);
        setChanged();
        notifyObservers(new EndOfSync(source));
    }
}
