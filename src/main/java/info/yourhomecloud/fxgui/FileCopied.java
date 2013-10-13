package info.yourhomecloud.fxgui;

import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 12/10/13
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class FileCopied {
    public FileCopied(Path path,boolean completed) {
        this.path = path.toAbsolutePath().normalize();
        this.completed = completed;
    }

    public Path getPath() {
        return path;
    }

    private Path path ;
    private boolean completed ;
}
