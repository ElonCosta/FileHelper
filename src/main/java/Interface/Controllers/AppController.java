package Interface.Controllers;

import Interface.Controllers.Files.FilesController;
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
    private Node logNode;
    private LogController logController;

    @FXML private Button cfgBtn;
    private Node cfgNode;
    private ConfigurationsController configurationsController;

    @FXML private Button fleBtn;
    private Node fleNode;
    private FilesController filesController;

    @FXML private Button newFleBtn;

    @FXML private AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Node n: menuBar.getItems()){
            Button b = (Button) n;
            b.setFocusTraversable(false);
        }
        try {
            initializeLogTab();
            initializeConfigurationTab();
            initializeFilesTab();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane.getChildren().add(logNode);
        logBtn.setDisable(true);
    }

    public void initializeLogTab() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.LogUI);
        logNode = fxmlLoader.load();
        logController = fxmlLoader.getController();
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

    @FXML private void changeScene(ActionEvent e){
        Button b = (Button) e.getSource();
        enableButtons();
        b.setDisable(true);
        switch (b.getText()){
            case "Log":
                mainPane.getChildren().set(0,logNode);
                Launcher.scene.getWindow().setHeight(489);
                break;
            case "Configurations":
                mainPane.getChildren().set(0,cfgNode);
                Launcher.scene.getWindow().setHeight(326);
                break;
            case "Files":
                mainPane.getChildren().set(0,fleNode);
                Launcher.scene.getWindow().setHeight(346);
                break;
            case "New File":
                break;
        }
    }

    public void clearLog(Integer n) {
        logController.clearLog(n);
    }

    public void appendLog(String ln) {
        logController.appendLog(ln);
    }

    private void enableButtons(){
        for (Node n: menuBar.getItems()){
            Button b = (Button) n;
            b.setDisable(false);
        }
    }

    public void loadFiles(){
        filesController.loadFiles();
    }
}
