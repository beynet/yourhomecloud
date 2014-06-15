package info.yourhomecloud.fxgui;

import java.nio.file.Path;

/**
 * Helper class used to represent a file copied on a remote host
 */
public class FileCopied {
    public FileCopied(Path path,boolean completed) {
        this.path = path.toAbsolutePath().normalize();
        this.completed = completed;
    }

    public Path getPath() {
        return path;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public String toString() {
        Path fileName = this.path.getFileName();
        if (fileName==null) return "/";
        return fileName.toString();
    }

    private Path path ;
    private boolean completed ;
}
