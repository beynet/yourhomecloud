package info.yourhomecloud.files.impl;

import info.yourhomecloud.hosts.File;
import info.yourhomecloud.utils.FileTools;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * visitor used to construct level 1 childs of a directory
 */
public class OneLevelVisitor implements FileVisitor<Path> {

    private Path toBeVisited;
    private Path toRelativize;
    private List<File> result;

    /**
     * construct the visotor
     * @param toBeVisited : path of the child from which all level 1 childs
     * will be listed
     */
    public OneLevelVisitor(Path toRelativize,Path toBeVisited) throws IllegalArgumentException {
        if (!Files.isDirectory(toBeVisited)) throw new IllegalArgumentException("given path must be a directory");
        this.toBeVisited = toBeVisited ;
        this.toRelativize =toRelativize;
        this.result = new ArrayList<>();
    }

    /**
     *@return all the level 1 childs of the visited directory
     */
    public List<File> getResult() {
        return result;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (!dir.equals(toBeVisited)) {
            List<String> relPath = FileTools.getPathListFromPath(toRelativize.relativize(dir));
            result.add(new File(relPath,true));
            // we only visite level 1 childs
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        List<String> relPath = FileTools.getPathListFromPath(toRelativize.relativize(file));
        final File f = new File(relPath, false);
        f.setLastModified(attrs.lastModifiedTime().toMillis());
        result.add(f);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
