package info.yourhomecloud.fxgui;

import info.yourhomecloud.hosts.File;
import info.yourhomecloud.utils.FileTools;

import java.nio.file.Path;

/**
 * Created by beynet on 16/06/2014.
 */
public class RemoteFile {
    public RemoteFile(Path localeFile) {
        this.localeFile = localeFile;
        this.remoteFile = new File(FileTools.getPathListFromPath(localeFile.getFileName()),true);
    }
    public RemoteFile(Path parent,File f) {
        this.localeFile= parent.resolve(f.getPath().get(f.getPath().size()-1));
        this.remoteFile = f;
    }

    public Path getLocaleFile() {
        return localeFile;
    }

    public File getRemoteFile() {
        return remoteFile;
    }

    private Path localeFile;
    private File remoteFile;
}
