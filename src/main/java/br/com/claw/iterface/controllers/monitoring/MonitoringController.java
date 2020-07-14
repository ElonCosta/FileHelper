package br.com.claw.iterface.controllers.monitoring;

import br.com.claw.archiveLoader.archive.Archive;
import br.com.claw.archiveLoader.archive.Paths;
import br.com.claw.enums.FXML_FILES;
import br.com.claw.enums.MONITOR_STATUS;
import br.com.claw.iterface.controllers.GenericController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import br.com.claw.utils.FXMLLoader;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static br.com.claw.Launcher.loader;
import static br.com.claw.utils.ControllersUtils.loadImages;

public class MonitoringController extends GenericController {

    @FXML public Pane fileListPanel;
    @Getter protected FileListController fileListController;

    @FXML public Pane fileDisplayPanel;
    @Getter protected FileDisplayController fileDisplayController;

    @Getter @Setter
    private MONITOR_STATUS monitor_status;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(FXML_FILES.MONITORING_FILE_LIST_UI);
            Node fileListNode = fxmlLoader.load();
            fileListPanel.getChildren().add(fileListNode);
            fileListController = fxmlLoader.getController();
            fileListController.setMonitoringController(this);
            fxmlLoader = new FXMLLoader(FXML_FILES.MONITORING_FILE_DISPLAY_UI);
            Node fileDisplayNode = fxmlLoader.load();
            fileDisplayPanel.getChildren().add(fileDisplayNode);
            fileDisplayController = fxmlLoader.getController();
            fileDisplayController.setMonitoringController(this);
        }catch (IOException i){
            i.printStackTrace();
        }
        loadImages(this);
        initEvents();
    }

    public void postInit(){
        updateUI();
    }

    protected void initEvents(){
        loadButtons();
    }

    public void updateUI(){
        fileListController.update();
        fileDisplayController.update();
    }

    public void updateFileList(){
        fileListController.update();
    }

    protected void createArchive(){
        Archive a = new Archive();
        Paths p = a.createNewPath();
        loader.getArchives().add(a);
        fileListController.getFiles().getChildren().add(fileListController.newTreeItem(a));
        selectArchive(a);
        selectPath(p);
        monitor_status = MONITOR_STATUS.NEW_ARCHIVE;
        updateUI();
    }

    protected void createPath(){
        Paths p = fileDisplayController.getSelectedArchive().createNewPath();
        selectPath(p);
        monitor_status = MONITOR_STATUS.NEW_PATH;
        updateUI();
    }

    private void loadButtons(){

    }

    public void selectArchive(Archive archive){
        if (archive == null){
            fileListController.setSelectedArchiveItem(null);
            fileDisplayController.setSelectedArchive(null);
        }else{
            fileListController.setSelectedArchiveItem(fileListController.getFiles().getChildren().filtered(c -> c.getValue() == archive).get(0));
            fileDisplayController.setSelectedArchive(archive);
        }
    }

    public void removeArchive(Archive archive){
        if(archive != null){
            fileListController.getFiles().getChildren().remove(fileListController.getFiles().getChildren().filtered(c -> c.getValue() == archive).get(0));
            loader.getArchives().remove(archive);
        }
    }

    public void selectPath(Paths paths){
        if (paths == null){
            fileListController.setSelectedPathsItem(null);
            fileDisplayController.setSelectedPaths(null);
        }else{
            fileListController.setSelectedPathsItem(fileListController.getSelectedArchiveItem().getChildren().filtered(c -> c.getValue() == paths).get(0));
            fileDisplayController.setSelectedPaths(paths);
            fileListController.getFilesTreeView().getSelectionModel().select(fileListController.getSelectedPathsItem());
        }
    }

    public void removePath(Paths paths){
        if (paths != null){
            fileListController.getSelectedArchiveItem().getChildren().remove(fileListController.getSelectedArchiveItem().getChildren().filtered(c-> c.getValue() == paths).get(0));
            fileDisplayController.getSelectedArchive().getPathsList().remove(paths);
        }
    }

    protected Archive getSelectedArchive(){
        return fileDisplayController.getSelectedArchive();
    }

    /* FXML METHODS */

    @FXML private void updateArchiveFile(){

        boolean archiveFiles = fileDisplayController.archiveFile.isSelected();
        if (archiveFiles != fileDisplayController.getSelectedArchive().getArchiveFiles()){
            fileDisplayController.getSelectedArchive().setArchiveFiles(archiveFiles);
        }
        fileDisplayController.update();
    }

    @FXML private void check(ActionEvent e){
        Button b = (Button) e.getSource();
        Archive archive = fileDisplayController.getSelectedArchive();
        Paths paths = fileDisplayController.getSelectedPaths();
        switch (b.getId()) {
            case "check" -> loader.check();
            case "checkThis" -> archive.checkThis();
            case "checkThisPath" -> archive.checkThis(paths);
        }
    }
}
