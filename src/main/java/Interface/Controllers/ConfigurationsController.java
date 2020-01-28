package Interface.Controllers;

import ArchiveLoader.Configurations;
import Main.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static Utils.Utils.*;

public class ConfigurationsController implements Initializable {

    private Configurations.Global global;

    private DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML private TextField rootFld;
    @FXML private Button rootBtn;

    @FXML private TextField archiveFld;
    @FXML private Button archiveBtn;

    @FXML private TextField latestFld;
    @FXML private Button latestBtn;

    @FXML private SpinnerValueFactory<Integer> routineFld;

    @FXML private CheckBox displayTime;

    @FXML private CheckBox archiveFiles;

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

        displayTime.setSelected(global.getDisplayTime());
        archiveFiles.setSelected(global.getArchiveFiles());

        routineFld.setValue(global.getRoutineTime());
    }

    private void initEvents(){
        rootFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                rootFld.setText(global.getRootFolder().getAbsolutePath());
            }else {
                rootFld.setText(getShorthandPath(global.getRootFolder()));
            }
        });
        rootBtn.setOnAction(e -> {
            rootFld.requestFocus();
            directoryChooser.setInitialDirectory(global.getRootFolder());
            File folder = directoryChooser.showDialog(Launcher.scene.getWindow());
            if (folder != null){
                global.setRootFolder(folder);
                rootFld.setText(getShorthandPath(global.getRootFolder()));
            }
        });

        archiveFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                archiveFld.setText(global.getArchiveFolder().getAbsolutePath());
            }else {
                archiveFld.setText(getShorthandPath(global.getArchiveFolder()));
            }
        });
        archiveBtn.setOnAction(e -> {
            archiveFld.requestFocus();
            directoryChooser.setInitialDirectory(global.getArchiveFolder());
            File folder = directoryChooser.showDialog(Launcher.scene.getWindow());
            if (folder != null){
                global.setArchiveFolder(folder);
                archiveFld.setText(getShorthandPath(global.getArchiveFolder()));
            }
        });

        latestFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                latestFld.setText(global.getVersionFolder().getAbsolutePath());
            }else {
                latestFld.setText(getShorthandPath(global.getVersionFolder()));
            }
        });
        latestBtn.setOnAction(e -> {
            latestFld.requestFocus();
            directoryChooser.setInitialDirectory(global.getVersionFolder());
            File folder = directoryChooser.showDialog(Launcher.scene.getWindow());
            if (folder != null){
                global.setVersionFolder(folder);
                latestFld.setText(getShorthandPath(global.getVersionFolder()));
            }
        });
    }

    @FXML private void updateRoutineTime(){
        global.setRoutineTime(routineFld.getValue());
    }

    @FXML private void updateDisplayTime(){
        global.setDisplayTime(displayTime.isSelected());
    }

    @FXML private void updateArchiveFiles(){
        global.setArchiveFiles(archiveFiles.isSelected());
    }
}
