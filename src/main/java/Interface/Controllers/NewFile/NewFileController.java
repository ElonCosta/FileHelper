package Interface.Controllers.NewFile;

import ArchiveLoader.FilesArchive;
import Utils.Utils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static Main.Launcher.*;

public class NewFileController implements Initializable {

    FilesArchive file;

    @FXML private TextField nmeFld;

    @FXML private TabPane pathsTab;
    @FXML private Tab newTab;

    private List<PathsTabController> pathsTabControllers = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        file = new FilesArchive();
        newTab.setClosable(false);

        newPath();
    }

    private void newPath(){

        FilesArchive.Paths path = file.createNewPath();
        Tab tab = new Tab();
        tab.setText(Integer.toString(file.getPathsList().size()));
        int index = file.getPathsList().size() - 1;
        tab.setOnClosed(e -> {
            file.getPathsList().remove(path);
            rearangeTabs();
        });
        pathsTab.getTabs().add(index,tab);
        pathsTab.getSelectionModel().select(index);
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.NFPathsTabUI);
            tab.setContent(fxmlLoader.load());
            PathsTabController pathsTabController = fxmlLoader.getController();
            pathsTabControllers.add(pathsTabController);
            pathsTabController.load(path,tab);
        }catch (IOException io){
            io.printStackTrace();
        }


        if (pathsTab.getTabs().size() == 2){
            pathsTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        }else{
            pathsTab.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
            newTab.setClosable(false);
        }
    }

    @FXML private void createPathTab(){
        if (pathsTab.getTabs().size() > 1){
            newPath();
        }
    }

    @FXML private void createFile(){
        if(isCreatable()){
            file.save();
//            loader.checkForFiles();
            app.reloadFiles();
            reloadPage();
        }
    }

    private void rearangeTabs(){
        if (pathsTab.getTabs().size() == 2){
            pathsTab.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        }
        int index = 1;
        for (Tab t: pathsTab.getTabs()) {
            t.setText(t.equals(newTab)? "+" : Integer.toString(index));
            index++;
        }
    }

    @FXML private void updateName(){
        file.setName(nmeFld.getText());
        file.getPathsList().stream().filter(FilesArchive.Paths::onLatest).forEach(p ->{
            File dest = new File(config.getGlobal().getVersionFolder().getAbsolutePath() + "\\" + file.getName());
            p.setDest(dest);
        });
        pathsTabControllers.forEach(PathsTabController::updateDestField);
    }

    public boolean isCreatable(){
        if(file.getName() == null){
            nmeFld.requestFocus();
            return false;
        }
        for (PathsTabController p: pathsTabControllers) {
            if (!p.isCreatable()){
                return false;
            }
        }
        return true;
    }

    private void reloadPage(){
        file = new FilesArchive();
        nmeFld.setText("");
        pathsTab.getTabs().clear();
        newPath();
        pathsTab.getTabs().add(newTab);
        rearangeTabs();
    }
}
