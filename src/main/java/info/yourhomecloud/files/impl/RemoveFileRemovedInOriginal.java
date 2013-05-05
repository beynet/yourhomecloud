package info.yourhomecloud.files.impl;

import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.log4j.Logger;

public class RemoveFileRemovedInOriginal implements FileVisitor<Path>{

    private Path mirror;
    private TargetHost original;

    public RemoveFileRemovedInOriginal(Path mirror,TargetHost original) {
        this.mirror = mirror;
        this.original = original;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (Files.isRegularFile(file)) {
            Path rel = this.mirror.relativize(file);
            if (!original.isFileExisting(rel)) {
                logger.debug("removing file "+file+" removed on source side");
                Files.delete(file);
            }
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Path rel = this.mirror.relativize(dir);
        if (!original.isFileExisting(rel)) {
            logger.debug("removing directory "+dir+" removed on source side");
            Files.delete(dir);
        }
        return FileVisitResult.CONTINUE;
    }

    private final static Logger logger = Logger.getLogger(RemoveFileRemovedInOriginal.class);

}
