package Interface.Controllers;

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

    /* MONITORING */
    @FXML private Button monitoringBtn;
    private Node monitoringNode;
    private MonitoringController monitoringController;

    @FXML private AnchorPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBar.getItems().forEach(i-> i.setFocusTraversable(false));
        try {
            initializeMonitoringTab();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane.getChildren().add(monitoringNode);
        monitoringBtn.setDisable(true);
    }

    public void postInit(){
        monitoringController.postInit();
    }

    public void initializeMonitoringTab() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.MonitoringUI);
        monitoringNode = fxmlLoader.load();
        monitoringController = fxmlLoader.getController();
    }

    @FXML private void changeScene(ActionEvent e){
        Button b = (Button) e.getSource();
        enableButtons();
        b.setDisable(true);
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
}
