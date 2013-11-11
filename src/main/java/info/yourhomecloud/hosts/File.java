package info.yourhomecloud.hosts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * helper class used to convey information about files found at the remote side
 */
public class File implements Serializable {

    public File(List<String> path,boolean isDirectory) {
        this.path        = path ;
        this.isDirectory = isDirectory ;
        this.childs = new ArrayList<File>();
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
        if (!childs.equals(file.childs)) return false;
        if (!path.equals(file.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isDirectory ? 1 : 0);
        result = 31 * result + path.hashCode();
        result = 31 * result + childs.hashCode();
        return result;
    }

    private boolean      isDirectory;
    private List<String> path;
    private List<File>   childs;
}
