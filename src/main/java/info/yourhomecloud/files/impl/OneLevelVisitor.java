package info.yourhomecloud.files.impl;

import info.yourhomecloud.hosts.File;
import info.yourhomecloud.utils.FileTools;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 09/11/2013
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class OneLevelVisitor implements FileVisitor<Path> {

    private Path toBeVisited;
    private List<File> result;
    public OneLevelVisitor(Path toBeVisited) {
        this.toBeVisited = toBeVisited ;
        this.result = new ArrayList<>();
    }

    public List<File> getResult() {
        return result;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (!dir.equals(toBeVisited)) {
            List<String> relPath = FileTools.getPathListFromPath(toBeVisited.relativize(dir));
            result.add(new File(relPath,true));
            return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        List<String> relPath = FileTools.getPathListFromPath(toBeVisited.relativize(file));
        result.add(new File(relPath, false));
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
