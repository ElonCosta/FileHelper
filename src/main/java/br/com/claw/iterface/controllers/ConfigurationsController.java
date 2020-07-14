package br.com.claw.iterface.controllers;

import br.com.claw.archiveLoader.Configurations;
import br.com.claw.Launcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static br.com.claw.utils.Utils.*;

public class ConfigurationsController extends GenericController {

    private Configurations.Global global;

    private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML private TextField rootFld;
    @FXML private Button rootBtn;

    @FXML private TextField archiveFld;
    @FXML private Button archiveBtn;

    @FXML private TextField latestFld;
    @FXML private Button latestBtn;

    @FXML private SpinnerValueFactory<Integer> routineFld;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        global = Launcher.config.getGlobal();
        setValues();
        initEvents();
    }

    private void setValues(){
        rootFld.setText(getShorthandPath(global.getRootFolder()));
        archiveFld.setText(getShorthandPath(global.getArchiveFolder()));
        latestFld.setText(getShorthandPath(global.getVersionFolder()));

        routineFld.setValue(global.getRoutineTime());
    }

    protected void initEvents(){
        rootFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) rootFld.setText(global.getRootFolder().getAbsolutePath());
            else rootFld.setText(getShorthandPath(global.getRootFolder()));
        });
        archiveFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) archiveFld.setText(global.getArchiveFolder().getAbsolutePath());
            else archiveFld.setText(getShorthandPath(global.getArchiveFolder()));
        });
        latestFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) latestFld.setText(global.getVersionFolder().getAbsolutePath());
            else latestFld.setText(getShorthandPath(global.getVersionFolder()));
        });
    }

    @FXML private void buttonAction(ActionEvent e){
        Button b = (Button) e.getSource();
        File folder;
        switch (b.getId()){
            case "latestBtn":
                latestFld.requestFocus();
                directoryChooser.setInitialDirectory(global.getVersionFolder());
                folder = directoryChooser.showDialog(Launcher.scene.getWindow());
                if (folder != null){
                    global.setVersionFolder(folder);
                    latestFld.setText(getShorthandPath(global.getVersionFolder()));
                }
                break;
            case "archiveBtn":
                archiveFld.requestFocus();
                directoryChooser.setInitialDirectory(global.getArchiveFolder());
                folder = directoryChooser.showDialog(Launcher.scene.getWindow());
                if (folder != null){
                    global.setArchiveFolder(folder);
                    archiveFld.setText(getShorthandPath(global.getArchiveFolder()));
                }
                break;
            case "rootBtn":
                rootFld.requestFocus();
                directoryChooser.setInitialDirectory(global.getRootFolder());
                folder = directoryChooser.showDialog(Launcher.scene.getWindow());
                if (folder != null){
                    global.setRootFolder(folder);
                    rootFld.setText(getShorthandPath(global.getRootFolder()));
                }
                break;
        }
    }

    @FXML private void updateRoutineTime(){
        global.setRoutineTime(routineFld.getValue());
    }

    @Override
    protected void postInit() {

    }
}
