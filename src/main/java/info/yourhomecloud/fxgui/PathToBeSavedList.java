/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yourhomecloud.fxgui;

import info.yourhomecloud.configuration.Configuration;
import java.nio.file.Paths;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author beynet
 */
public class PathToBeSavedList extends ListView<String>{
    public PathToBeSavedList(ObservableList<String> hostToBeSaved) {
        super(hostToBeSaved);
        textDirectoriesList = hostToBeSaved;
        setOnKeyPressed(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent t) {
                if (t.getCode().equals(KeyCode.DELETE)) {
                    int selected = getSelectionModel().getSelectedIndex();
                    if (selected>=0) {
                        String get = textDirectoriesList.get(selected);
                        Configuration.getConfiguration().removeDirectoryToBeSaved(Paths.get(get));
                    }
                }
            }
            
        });
    }
    public void setListContent(List<String> hostToBeSaved) {
        textDirectoriesList.clear();
        textDirectoriesList.addAll(hostToBeSaved);
    }
    
    private final ObservableList<String> textDirectoriesList;
}
