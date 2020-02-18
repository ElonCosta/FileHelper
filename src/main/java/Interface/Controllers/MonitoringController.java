package Interface.Controllers;

import ArchiveLoader.Archive.Archive;
import ArchiveLoader.Archive.Collections;
import ArchiveLoader.Loader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

import static Main.Launcher.loader;

public class MonitoringController implements Initializable {

    @FXML private TreeView<Archive> collections;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeItem<Archive> root = new TreeItem("Root");
        root.setExpanded(true);
        for (int i = 1; i < 6; i++) {
            TreeItem<Archive> item = new TreeItem("Message" + i);
            root.getChildren().add(item);
        }
        collections.setRoot(root);
    }
}
