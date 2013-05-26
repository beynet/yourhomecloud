/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.files;

import info.yourhomecloud.files.impl.FileSyncerImpl;

/**
 *
 * @author beynet
 */
public class FileSyncerBuilder {
    public static FileSyncer createMonodirectionalFileSyncer() {
        return new FileSyncerImpl();
    }
}
