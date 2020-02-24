package Interface.Controllers;

import Interface.Controllers.Files.FilesController;
import Interface.Controllers.NewFile.NewFileController;
import Main.Launcher;
import Utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML private Pane main;

    @FXML private ToolBar menuBar;

    /* LOG */
    @FXML private Button logBtn;
    private Node monitoringNode;
    private MonitoringController monitoringController;

    /* CONFIGURATIONS */
    @FXML private Button cfgBtn;
    private Node cfgNode;
    private ConfigurationsController configurationsController;

    /* FILES */
    @FXML private Button fleBtn;
    private Node fleNode;
    private FilesController filesController;

    /* NEW FILE */
    @FXML private Button newFleBtn;
    private Node newFleNode;
    private NewFileController newFileController;

    @FXML private AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.getItems().forEach(i-> i.setFocusTraversable(false));
        try {
            initializeMonitoringTab();
            initializeConfigurationTab();
            initializeFilesTab();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane.getChildren().add(monitoringNode);
        logBtn.setDisable(true);
    }

    public void postInit(){
        filesController.loadFiles();
        monitoringController.postInit();
    }

    public void initializeMonitoringTab() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.MonitoringUI);
        monitoringNode = fxmlLoader.load();
        monitoringController = fxmlLoader.getController();
    }

    public void initializeConfigurationTab() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.ConfigUI);
        cfgNode = fxmlLoader.load();
        configurationsController = fxmlLoader.getController();
    }

    public void initializeFilesTab() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.FilesUI);
        fleNode = fxmlLoader.load();
        filesController = fxmlLoader.getController();
    }

    public void initializeNewFileTab() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.NewFileUI);
        newFleNode = fxmlLoader.load();
        newFileController = fxmlLoader.getController();
    }

    @FXML private void changeScene(ActionEvent e){
        Button b = (Button) e.getSource();
        enableButtons();
        b.setDisable(true);
        switch (b.getId()){
            case "logBtn":
                mainPane.getChildren().set(0, monitoringNode);
                Launcher.scene.getWindow().setHeight(489);
                break;
            case "cfgBtn":
                mainPane.getChildren().set(0,cfgNode);
                Launcher.scene.getWindow().setHeight(326);
                break;
            case "fleBtn":
                mainPane.getChildren().set(0,fleNode);
                Launcher.scene.getWindow().setHeight(346);
                break;
            case "newFleBtn":
                mainPane.getChildren().set(0, newFleNode);
                Launcher.scene.getWindow().setHeight(346);
                break;
        }
    }

    public void updateUI(){
        monitoringController.updateUI();
    }

    public void updateFileList(){
        monitoringController.updateFileList();
    }

    public void updateFileDisplay(){
        monitoringController.updateFileDisplay();
    }

    private void enableButtons(){
        for (Node n: menuBar.getItems()){
            Button b = (Button) n;
            b.setDisable(false);
        }
    }

    public void reloadFiles(){
        filesController.reloadFiles();
    }
}
