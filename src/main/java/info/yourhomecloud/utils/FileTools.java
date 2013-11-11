package info.yourhomecloud.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: beynet
 * Date: 11/11/2013
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class FileTools {

    /**
     * convert a path in a list of string
     * @param path
     * @return
     */
    public static List<String> getPathListFromPath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }
        List<String> result = new ArrayList<>();
        Iterator<Path> iterator = path.iterator();
        while(iterator.hasNext()) {
            result.add(iterator.next().toString());
        }
        return result;
    }

    /**
     * convert a path in a list of string
     * @param pathList
     * @return
     */
    public static Path getPathFromPathList(List<String> pathList) {
        return Paths.get(getPathStringFromPathList(pathList));
    }

    public static String getPathStringFromPathList(List<String> pathList) {
        if (pathList == null || pathList.isEmpty()) {
            throw new IllegalArgumentException("pathList must not be null nor empty");
        }
        StringBuilder sb = null;
        for (String p : pathList) {
            if (sb == null) {
                sb = new StringBuilder(p);
            } else {
                sb.append(File.separator);
                sb.append(p);
            }
        }
        return sb.toString();
    }
}
