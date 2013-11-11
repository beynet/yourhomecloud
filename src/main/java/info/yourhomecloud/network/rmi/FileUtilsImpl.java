package info.yourhomecloud.network.rmi;

import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.hosts.TargetHost;
import info.yourhomecloud.hosts.impl.LocalTargetHost;
import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.List;

import info.yourhomecloud.utils.FileTools;
import org.apache.log4j.Logger;

public class FileUtilsImpl implements FileUtils {

    private Path getTargetPathFromClient(String client) {
        return Configuration.getConfiguration().getConfigurationPath().resolve(Paths.get(client));
    }

    private TargetHost getTargetHostFromCient(String client) {
        return new LocalTargetHost(getTargetPathFromClient(client));
    }

    @Override
    public void createDirectoryIfNotExist(String client, List<String> rel) throws IOException {
        try {
            TargetHost target = getTargetHostFromCient(client);
            Path relPath = FileTools.getPathFromPathList(rel);
            target.createDirectoryIfNotExist(relPath);
        } catch (Exception e) {
            logger.error("error", e);
        }
    }

    @Override
    public boolean isFileExisting(String client, List<String> rel) throws IOException {
        TargetHost target = getTargetHostFromCient(client);
        Path relPath = FileTools.getPathFromPathList(rel);
        return target.isFileExisting(relPath);
    }

    @Override
    public info.yourhomecloud.hosts.File listFilesAt(String client,info.yourhomecloud.hosts.File file) throws IOException{
        TargetHost target = getTargetHostFromCient(client);
        target.listFilesAt(file);
        return file;
    }

    @Override
    public boolean isFileExistingAndNotModifiedSince(String client, List<String> rel, long millis) throws IOException {
        TargetHost target = getTargetHostFromCient(client);
        Path relPath = FileTools.getPathFromPathList(rel);
        return target.isFileExistingAndNotModifiedSince(relPath, millis);
    }

    @Override
    public void copyFile(String client, byte[] file, long modified, List<String> rel) throws IOException {
        Path target = getTargetPathFromClient(client);
        Path relPath = FileTools.getPathFromPathList(rel);
        Path filePath = target.resolve(relPath);
        logger.debug("writing file " + filePath);
        Files.write(filePath, file);
        Files.setLastModifiedTime(filePath, FileTime.fromMillis(modified));
    }

    @Override
    public void copyFileByChunk(String client, byte[] file, long offset, int length, boolean last, long modified, List<String> rel) throws IOException {
        try {
            Path target = getTargetPathFromClient(client);
            Path relPath = FileTools.getPathFromPathList(rel);
            Path filePath = target.resolve(relPath);
            logger.debug("writing by chunk file " + filePath.toString() + " from " + offset + " nb bytes " + length);
            final OpenOption[] options;

            if (offset == 0) {
                options = new OpenOption[3];
                options[0] = StandardOpenOption.WRITE;
                options[1] = StandardOpenOption.CREATE;
                options[2] = StandardOpenOption.TRUNCATE_EXISTING;
            } else {
                options = new OpenOption[2];
                options[0] = StandardOpenOption.WRITE;
                options[1] = StandardOpenOption.APPEND;
            }

            try (OutputStream outputStream = Files.newOutputStream(filePath, options)) {
                outputStream.write(file, 0, length);
            }
            if (last == true) {
                Files.setLastModifiedTime(filePath, FileTime.fromMillis(modified));
            }
        } catch (Exception e) {
            logger.error("ERROR :" + e, e);
            throw e;
        }
    }
    private final static Logger logger = Logger.getLogger(FileUtilsImpl.class);
}
