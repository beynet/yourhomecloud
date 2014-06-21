package info.yourhomecloud.hosts;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * represent a host on which remote commands may be send
 */
public interface TargetHost {

    /**
     * create remote directory if not existing
     * @param rel
     * @throws IOException
     */
    void createDirectoryIfNotExist(Path rel) throws IOException;

    /**
     * check if given file exists on the remote side
     * @param rel
     * @param millis
     * @return
     * @throws IOException
     */
    boolean isFileExistingAndNotModifiedSince(Path rel, long millis) throws IOException;

    /**
     * copy file on remote side.
     * @param file
     * @param rel : the relative path of the file to be copied. For example,
     * if rel="a/b", the file a/b will be created under the directory where the remote
     * server store all the data relative to current host
     * @throws IOException
     */
    void copyFile(Path file, Path rel) throws IOException;

    /**
     * @param rel
     * @return true if current file exist on remote side
     * @throws IOException
     */
    boolean isFileExisting(Path rel) throws IOException;


    /**
     *
     * @param source
     * @throws IOException
     */
    void removeFilesRemovedOnSourceSide(Path source) throws IOException;

    /**
     * remove a file on remote side
     * @param rel : the rel path
     * @throws IOException
     */
    void removeFile(Path rel) throws IOException;

    /**
     * list child files of the given file. This method is
     * not recursive, child directories will not be traversed. Given file path must be a
     * relative file path (relative to the directory where files will be backuped)
     * @param file
     * @throws IOException
     */
    void listFilesAt(File file) throws IOException;

}
