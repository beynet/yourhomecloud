package info.yourhomecloud.hosts.impl;

import info.yourhomecloud.files.impl.OneLevelVisitor;
import info.yourhomecloud.files.impl.RemoveFileRemovedInOriginal;
import info.yourhomecloud.hosts.File;
import info.yourhomecloud.hosts.TargetHost;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.yourhomecloud.utils.FileTools;
import org.apache.log4j.Logger;

public class LocalTargetHost implements TargetHost {

    private Path target;


    public LocalTargetHost(Path target) {
        this.target = target;
    }

    @Override
    public void createDirectoryIfNotExist(Path rel) throws IOException {
        Path targetFile = target.resolve(rel);
        if (!Files.exists(targetFile)) {
            Files.createDirectories(targetFile);
        }
    }

    @Override
    public void listFilesAt(File file) throws IOException {
        if (!file.isDirectory()) throw new IllegalArgumentException("file to be visited must be a directory");
        Path targetFile = target.resolve(FileTools.getPathFromPathList(file.getPath()));
        OneLevelVisitor visitor = new OneLevelVisitor(target,targetFile);
        Files.walkFileTree(targetFile, visitor);
        file.getChilds().clear();
        file.getChilds().addAll(visitor.getResult());
    }

    @Override
    public boolean isFileExisting(Path rel) throws IOException {
        Path targetFile = target.resolve(rel);
        if (!Files.exists(targetFile)) {
            logger.debug(targetFile+" does not exist");
            return false;
        }
        return true;
    }

    @Override
    public boolean isFileExistingAndNotModifiedSince(Path rel,long millis) throws IOException {
        Path targetFile = target.resolve(rel);
        if (!Files.exists(targetFile)) {
            logger.debug(targetFile+" does not exist");
            return false;
        }
        else {
            BasicFileAttributes targetAttrs = Files.readAttributes(targetFile, BasicFileAttributes.class);
            if (targetAttrs.lastModifiedTime().toMillis()!=millis) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void copyFile(Path file, Path rel) throws IOException {
        Path newFile = target.resolve(rel);
        logger.debug("writing file "+newFile);
        Files.copy(file, newFile,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES);
    }

    @Override
    public void restoreFile(Path file,Path rel) throws IOException {
        Path toRestore = target.resolve(rel);
        logger.debug("restore file "+toRestore+" at "+file);
        Files.copy(toRestore, file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.COPY_ATTRIBUTES);
    }

    @Override
    public void removeFilesRemovedOnSourceSide(Path source) throws IOException {
        final RemoveFileRemovedInOriginal removeVisitor = new RemoveFileRemovedInOriginal(target, new LocalTargetHost(source));
        Files.walkFileTree(target, removeVisitor);
    }

    @Override
    public void removeFile(Path rel) throws IOException {
        Path newFile = target.resolve(rel);
        if (Files.exists(newFile)) {
            Files.delete(newFile);
        }
    }

    private final static Logger logger  = Logger.getLogger(LocalTargetHost.class);
}
