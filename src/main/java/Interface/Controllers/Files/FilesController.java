package Interface.Controllers.Files;

import ArchiveLoader.FilesArchive;
import Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static Main.Launcher.loader;

public class FilesController implements Initializable {

    @FXML private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void loadFiles(){
        Map<String, FilesArchive> map = loader.getArchiveMap();
        for (FilesArchive fa: map.values()){
            Tab tab = new Tab();
            tab.setText(fa.getName());
            tabPane.getTabs().add(tab);

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Utils.FilesTabUI);
                tab.setContent(fxmlLoader.load());
                ((FileTabController) fxmlLoader.getController()).loadTab(fa);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
