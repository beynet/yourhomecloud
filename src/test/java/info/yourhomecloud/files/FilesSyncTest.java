package info.yourhomecloud.files;

import info.yourhomecloud.RootTest;
import info.yourhomecloud.configuration.Configuration;
import info.yourhomecloud.files.impl.FileSyncerImpl;
import info.yourhomecloud.files.impl.OneLevelVisitor;
import info.yourhomecloud.hosts.File;
import info.yourhomecloud.hosts.impl.LocalTargetHost;
import info.yourhomecloud.hosts.impl.RMITargetHost;
import info.yourhomecloud.network.rmi.RMIUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.util.List;

import info.yourhomecloud.utils.FileTools;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FilesSyncTest extends RootTest{

    public final static String LOCAL_TARGET_DIR = "/tmp/DeskCP";
    @Test
    public void testRel() {
        Path local = Paths.get("/tmp/a/b/d");
        Path source = Paths.get("/tmp/a");
        
        Path destination = Paths.get("/truce/machin");
        Path r = source.relativize(local);
        Path fail = source.relativize(destination);
        System.out.println("!!!! failure="+fail);
        
        Path resolved = destination.resolve(r);
        System.out.println(r.toString());
        System.out.println(resolved.toString());
    }


    private void localCopy(Path toBeCopied,LocalTargetHost localTargetHost) throws IOException {
        FileSyncerImpl fs = new FileSyncerImpl();
        fs.sync(toBeCopied, localTargetHost);
    }

    @Test
    public void localCopy() throws IOException {
        localCopy(Paths.get("/Users/beynet/Desktop"), new LocalTargetHost(Paths.get(LOCAL_TARGET_DIR)));
    }


    private void remoteCopy(Path tobeCopied,RMITargetHost host) throws IOException, NotBoundException {
        FileSyncerImpl fs = new FileSyncerImpl();
        fs.sync(tobeCopied, new RMITargetHost(Configuration.getConfiguration().getCurrentHostKey(),"127.0.0.1", RMIUtils.getRMIUtils().getPort()));
    }

    @Test
    public void remoteCopy() throws IOException, NotBoundException {
        remoteCopy(Paths.get("/Users/beynet/Desktop"), new RMITargetHost(Configuration.getConfiguration().getCurrentHostKey(), "127.0.0.1", RMIUtils.getRMIUtils().getPort()));
    }



    @Test
    public void visitOneLevel() throws IOException {

        final String prefix = "yhcTU_";
        String tmp = System.getProperty("java.io.tmpdir");
        System.out.println(tmp);
        Path tempDir  = null;
        Path child1 = null ;
        Path child2Dir = null ;
        Path child5Dir = null ;
        Path child3NotFound  = null;
        Path child4 = null;
        Path child6NotFound = null;
        try {
            tempDir = Files.createTempDirectory(Paths.get(tmp), prefix);
            child1 = Files.createFile(tempDir.resolve(Paths.get("child1")));
            child2Dir = Files.createDirectory(tempDir.resolve(Paths.get("child2Dir")));
            child3NotFound = Files.createFile(child2Dir.resolve(Paths.get("child3NotFound")));
            child5Dir = Files.createDirectory(tempDir.resolve(Paths.get("child5Dir")));
            child4 = Files.createFile(tempDir.resolve(Paths.get("child4")));
            child6NotFound = Files.createFile(child5Dir.resolve(Paths.get("child6NotFound")));

            OneLevelVisitor visitor = new OneLevelVisitor(tempDir,tempDir);
            Files.walkFileTree(tempDir, visitor);
            List<File> result = visitor.getResult();
            assertThat(result.size(),is(4));
            for (File f : result) {
                if ("child1".equals(FileTools.getPathStringFromPathList(f.getPath()))) {
                    assertFalse(f.isDirectory());
                }
                else if ("child2Dir".equals(FileTools.getPathStringFromPathList(f.getPath()))) {
                    assertTrue(f.isDirectory());
                } else if ("child4".equals(FileTools.getPathStringFromPathList(f.getPath()))) {
                    assertFalse(f.isDirectory());
                }
                else if ("child5Dir".equals(FileTools.getPathStringFromPathList(f.getPath()))) {
                    assertTrue(f.isDirectory());
                }
                else {
                    assertFalse(true);
                }
            }

        } finally {
            if (child4!=null) Files.delete(child4);
            if( child3NotFound!=null) Files.delete(child3NotFound);
            if (child2Dir!=null) Files.delete(child2Dir);

            if( child6NotFound!=null) Files.delete(child6NotFound);
            if (child5Dir!=null) Files.delete(child5Dir);
            if (child1!=null) Files.delete(child1);
            if (tempDir!=null) Files.delete(tempDir);
        }
    }

    @Test
    public void listFilesRemote() throws IOException, NotBoundException {
        Path toBeCopied = Paths.get("/Users/beynet/Desktop");
        RMITargetHost targetHost = new RMITargetHost(Configuration.getConfiguration().getCurrentHostKey(),"127.0.0.1", RMIUtils.getRMIUtils().getPort());
        remoteCopy(toBeCopied, targetHost);

        // search childs file in copy
        File file = new File(FileTools.getPathListFromPath(Paths.get("Desktop")),true);
        targetHost.listFilesAt(file);

        // search child files in origin
        OneLevelVisitor oneLevelVisitor = new OneLevelVisitor(toBeCopied.getParent(),toBeCopied);
        Files.walkFileTree(toBeCopied,oneLevelVisitor);

        assertThat(file.getChilds().size(),is(oneLevelVisitor.getResult().size()));
        for (File child :file.getChilds()) {
            logger.debug("check file " + child.getPath());
            assertTrue(oneLevelVisitor.getResult().contains(child));
        }
    }

    @Test
    public void copyAndRemoveRemote() {
        final String property = System.getProperty("java.io.tempdir");
        final Path rootTmpDir = Paths.get(property);
        final Path tmpDir1 = null;
        try {
            tmpDir1=Files.createTempDirectory(rootTmpDir, "d1");
        }finally {
            if (tmpDir1!=null) Files.delete(tmpDir1);
        }
    }

    @Test
    public void listFilesLocale() throws IOException, NotBoundException {
        Path toBeCopied = Paths.get("/Users/beynet/Desktop");
        LocalTargetHost localTargetHost = new LocalTargetHost(Paths.get(LOCAL_TARGET_DIR));
        localCopy(toBeCopied, localTargetHost);

        // search childs file in copy
        File file = new File(FileTools.getPathListFromPath(Paths.get("Desktop")),true);
        localTargetHost.listFilesAt(file);

        // search child files in origin
        OneLevelVisitor oneLevelVisitor = new OneLevelVisitor(toBeCopied.getParent(),toBeCopied);
        Files.walkFileTree(toBeCopied,oneLevelVisitor);

        assertThat(file.getChilds().size(),is(oneLevelVisitor.getResult().size()));
        for (File child :file.getChilds()) {
            logger.debug("check file " + FileTools.getPathStringFromPathList(child.getPath()));
            assertTrue(oneLevelVisitor.getResult().contains(child));
        }

    }

    private final static Logger logger = Logger.getLogger(FilesSyncTest.class);
}
