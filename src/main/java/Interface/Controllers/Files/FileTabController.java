package Interface.Controllers.Files;

import ArchiveLoader.FilesArchive;
import Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class FileTabController implements Initializable {

    private FilesArchive file;

    @FXML private Label name;

    @FXML private Label lastMod;

    @FXML private CheckBox archiveFiles;

    @FXML private TabPane pathsTabs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loadTab(FilesArchive fa){
        file = fa;
        name.setText(file.getName());
        lastMod.setText(file.getLastMod());
        archiveFiles.setSelected(file.getArchiveFiles());

        List<FilesArchive.Paths> paths = fa.getPathsList();
        int pos = 1;
        for (FilesArchive.Paths p: paths){
            Tab tab = new Tab();
            tab.setText(Integer.toString(pos));
            pathsTabs.getTabs().add(tab);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Utils.PathsTabUI);
                tab.setContent(fxmlLoader.load());
                ((PathsTabController) fxmlLoader.getController()).loadPaths(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
            pos++;
        }
    }

    @FXML
    private void archiveFiles(){
        file.setArchiveFiles(archiveFiles.isSelected());
        file.save();
    }
}
