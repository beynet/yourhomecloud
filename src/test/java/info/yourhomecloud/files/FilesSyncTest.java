package info.yourhomecloud.files;

import info.yourhomecloud.RootTest;
import info.yourhomecloud.files.impl.FileSyncerImpl;
import info.yourhomecloud.hosts.impl.LocalTargetHost;
import info.yourhomecloud.hosts.impl.RMITargetHost;
import info.yourhomecloud.network.rmi.RMIUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;

import org.junit.Test;

public class FilesSyncTest extends RootTest{
    @Test
    public void testRel() {
        Path local = Paths.get("/tmp/a/b/d");
        Path source = Paths.get("/tmp/a");
        
        Path destination = Paths.get("/truc/machin");
        Path r = source.relativize(local);
        
        Path resolved = destination.resolve(r);
        System.out.println(r.toString());
        System.out.println(resolved.toString());
    }
    
    @Test
    public void localCopy() throws IOException {
        FileSyncerImpl fs = new FileSyncerImpl();
        fs.sync(Paths.get("/Users/beynet/Desktop"), new LocalTargetHost(Paths.get("/tmp/DeskCP")));
    }
    
    @Test
    public void remoteCopy() throws IOException, NotBoundException {
        FileSyncerImpl fs = new FileSyncerImpl();
        fs.sync(Paths.get("/Users/beynet/Desktop"), new RMITargetHost("127.0.0.1", RMIUtils.getRMIUtils().getPort()));
    }
}
