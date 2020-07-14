package br.com.claw.iterface.controllers.monitoring;

import br.com.claw.archiveLoader.archive.Archive;
import br.com.claw.archiveLoader.archive.Paths;
import br.com.claw.enums.MONITOR_STATUS;
import br.com.claw.enums.STATUS;
import br.com.claw.iterface.components.fileChooser.FileChooser;
import br.com.claw.iterface.controllers.GenericController;
import br.com.claw.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.claw.Launcher.config;
import static br.com.claw.Launcher.loader;
import static br.com.claw.utils.ControllersUtils.elementDisplay;

public class FileDisplayController extends GenericController {

    @Setter
    private MonitoringController monitoringController;

    @Getter @Setter
    private Archive selectedArchive;
    @Getter @Setter
    private Paths selectedPaths;

    /* ARCHIVE */
    @FXML public TextField fileName;
    @FXML public CheckBox archiveFile;
    @FXML public Label lastMod;
    @FXML public Label enabled;

    /* PATHS */
    @FXML public TextField pathFile;
    @FXML public TextField pathDest;
    @FXML public CheckBox pathDisabled;

    /* GROUPS */
    @FXML public Group editing;
    @FXML public Group ready;
    @FXML public Group pathBtns;

    @FXML public Button newPaths;

    private final SimpleDateFormat lastModSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    @Override
    protected void postInit() {

    }

    @Override
    protected void initEvents() {
        pathFile.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedPaths == null) return;
            setText(pathFile, selectedPaths.getFile(), newValue);
        });
        pathDest.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedPaths == null) return;
            setText(pathDest, selectedPaths.getDest(), newValue);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initEvents();
    }

    private void setText(TextField tf, File f, Boolean b){
        if (b){
            tf.setText(f == null ? "" : f.getAbsolutePath());
        }else{
            tf.setText(f == null ? "" : Utils.getShorthandPath(f));
        }
    }

    @FXML private void updateArchiveName(KeyEvent e){
        String name = fileName.getText();
        if (!name.equals(selectedArchive.getName())){
            selectedArchive.setName(name);
            if (selectedPaths.onLatest()){
                selectedPaths.setOnLatest(true);
            }
        }
        monitoringController.updateUI();
    }

    public void update(){
        if (selectedArchive != null){
            fileName.setText(selectedArchive.getName());
            if (selectedArchive.getName() != null){
                fileName.positionCaret(selectedArchive.getName().length());
            }
            archiveFile.setSelected(selectedArchive.getArchiveFiles());
            String lastModText = "Last Mod: "+lastModSDF.format(selectedArchive.getLastMod());
            lastMod.setText(lastModText);
            String enabledText = String.format("%d/%d Paths enabled",
                    selectedArchive.getPathsList().stream().filter(Paths::isEnabled).count(),
                    selectedArchive.getPathsList().size());
            enabled.setText(enabledText);

            pathFile.setText(selectedPaths.getFile() == null ? "" : Utils.getShorthandPath(selectedPaths.getFile()));
            pathDest.setText(selectedPaths.getDest() == null ? "" : Utils.getShorthandPath(selectedPaths.getDest()));
            pathDisabled.setSelected(selectedPaths.getDisabled());
            elementDisplay(monitoringController,"updateFileDisplay");
        }
    }

    @FXML private void cancelEditing(ActionEvent e){
        switch (monitoringController.getMonitor_status()){
            case NEW_PATH -> monitoringController.removePath(selectedPaths);
            case NEW_ARCHIVE -> {
                monitoringController.removeArchive(selectedArchive);
                if (loader.getArchives().isEmpty()){
                    monitoringController.selectArchive(null);
                }else{
                    monitoringController.selectArchive(loader.getArchives().get(loader.getArchives().size()-1));
                }
            }
            case EDITING_PATH -> selectedPaths.load();
            case EDITING_ARCHIVE -> selectedArchive.load();
        }
        monitoringController.updateUI();
    }

    @FXML private void confirmEditing(ActionEvent e){
        switch (monitoringController.getMonitor_status()){
            case NEW_PATH -> {
                // TODO: add newPath
            }
            case NEW_ARCHIVE -> {
                if (selectedArchive.isValid().get(false) != null){
                    fileName.requestFocus();
                    return;
                }
                for (Paths p: selectedArchive.getPathsList().stream().filter(p -> p.getStatus().equals(STATUS.NEW)).collect(Collectors.toList())){
                    Map<Boolean, String> pathStatus = p.isValid();
                    if (pathStatus.get(false) != null){
                        monitoringController.selectPath(p);
                        switch (pathStatus.get(false)){
                            case "No file selected" -> pathFile.requestFocus();
                            case "No destination selected" -> pathDest.requestFocus();
                        }
                        monitoringController.updateUI();
                    }
                }
                selectedArchive.generateId();
                selectedArchive.save();
                config.save();
            }
            case EDITING_PATH -> {
                // TODO: add editingPath
            }
            case EDITING_ARCHIVE -> {
                // TODO: add editingArchive
            }
        }
    }

    @FXML private void searchFiles(ActionEvent e){
        FileChooser fc = new FileChooser();
        Button b = (Button) e.getSource();
        if (b.getId().equals("getFileBtn")){
            fc.setInitialFolder(selectedPaths.getFile() == null ? new File(System.getProperty("user.dir")) : selectedPaths.getFile().getParentFile());
            File f = fc.getAny();
            if (f == null) return;
            selectedPaths.setFile(f);
            if (selectedPaths.getDest() == null){
                selectedPaths.setOnLatest(true);
            }
        }else{
            fc.setInitialFolder(selectedPaths.getDest() == null ? new File(System.getProperty("user.dir")) : selectedPaths.getDest().getParentFile());
            File f = fc.getFolder();
            if (f == null) return;
            selectedPaths.setDest(f);
        }
        monitoringController.updateUI();
    }

    @FXML private void editArchive(){
        selectedArchive.setStatus(STATUS.EDITING);
        monitoringController.setMonitor_status(MONITOR_STATUS.EDITING_ARCHIVE);
        monitoringController.updateUI();
    }

}
