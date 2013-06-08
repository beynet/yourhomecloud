/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.files.events;

import java.nio.file.Path;

/**
 *
 * @author beynet
 */
public class StartOfSync implements FileSyncerEvent {
    private Path path;
    public StartOfSync(Path dir) {
        this.path = dir ;
    }
    
    @Override
    public Path getFile() {
        return path;
    }

    @Override
    public String getMessage() {
        return "enf of sync";
    }
    
}
