package info.yourhomecloud.hosts;

import java.io.IOException;
import java.nio.file.Path;

public interface TargetHost {

    void createDirectoryIfNotExist(Path rel) throws IOException;

    boolean isFileExistingAndNotModifiedSince(Path rel, long millis) throws IOException;

    void copyFile(Path file, Path rel) throws IOException;

    boolean isFileExisting(Path rel) throws IOException;

    void removeFilesRemovedOnSourceSide(Path source) throws IOException;

}
