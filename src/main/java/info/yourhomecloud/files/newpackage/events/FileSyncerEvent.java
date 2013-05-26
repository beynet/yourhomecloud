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
public interface FileSyncerEvent {
    Path getFile();
    String getMessage();
}
