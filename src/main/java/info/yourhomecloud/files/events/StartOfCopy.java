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
public class StartOfCopy implements FileSyncerEvent {
    public StartOfCopy(Path file) {
        this.file = file;
    }

    @Override
    public Path getFile() {
        return this.file;
    }
    
    @Override
    public String getMessage(){
        return "start of copy <"+getFile().toString()+">";
    }
    
    private Path file ;
}
