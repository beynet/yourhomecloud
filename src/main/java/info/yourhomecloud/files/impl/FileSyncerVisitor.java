package info.yourhomecloud.files.impl;

import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Observable;

import org.apache.log4j.Logger;

public class FileSyncerVisitor extends Observable implements FileVisitor<Path>  {
    protected FileSyncerVisitor(Path source,TargetHost targetHost) {
        this.source = source ;
        this.targetHost = targetHost;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path rel = this.source.relativize(dir);
        this.targetHost.createDirectoryIfNotExist(rel);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            Path rel = this.source.relativize(file);
            if (!this.targetHost.isFileExistingAndNotModifiedSince(rel,attrs.lastModifiedTime().toMillis())) {
                notifyObservers("start of copy :"+file.toString());
                this.targetHost.copyFile(file,rel);
                notifyObservers("end of copy :"+file.toString());
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.TERMINATE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private Path       source;
    private TargetHost targetHost ;

    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(FileSyncerVisitor.class);
}
