package info.yourhomecloud.hosts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class used to convey information about files found at the remote side.
 * Path used must be relative , for example if a folder name folder1 is backuped
 * with a sub directory named folder2, folder2 relative path will be folder1/folder2
 */
public class File implements Serializable {

    public File(List<String> path,boolean isDirectory) {
        this.path        = path ;
        this.isDirectory = isDirectory ;
        this.childs = new ArrayList<>();
        this.lastModified = 0;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public List<String> getPath() {
        return path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void addChild(File child) {
        this.childs.add(child);
    }

    public List<File> getChilds() {
        return childs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File file = (File) o;

        if (isDirectory != file.isDirectory) return false;
        if (lastModified != file.lastModified) return false;
        if (!childs.equals(file.childs)) return false;
        if (!path.equals(file.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isDirectory ? 1 : 0);
        result = 31 * result + path.hashCode();
        result = 31 * result + childs.hashCode();
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        return result;
    }

    private boolean      isDirectory;
    private List<String> path;
    private List<File>   childs;
    private long         lastModified;
}
