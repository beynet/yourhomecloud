/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.files.newpackage.events;

import java.nio.file.Path;

/**
 *
 * @author beynet
 */
public class EndOfCopy implements FileSyncerEvent{
    public EndOfCopy(Path file) {
        this.file = file;
    }

    @Override
    public Path getFile() {
        return this.file;
    }

    @Override
    public String getMessage(){
        return "end of copy <"+getFile().toString()+">";
    }

    
    private Path file ;
}
