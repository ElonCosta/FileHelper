package Interface.Controllers;

import ArchiveLoader.Archive.Archive;
import ArchiveLoader.Archive.Paths;
import Utils.Utils;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static Main.Launcher.loader;

public class MonitoringController implements Initializable {

    /* FILE LISTING */
    @FXML private TreeView<Object> collections;
    private TreeItem<Object> files;

    /* FILE DISPLAY */
    /* ARCHIVE */
    @FXML private TextField fileName;
    @FXML private CheckBox archiveFile;
    @FXML private Label lastMod;
    @FXML private Label enabled;

    /* PATHS */
    @FXML private TextField pathFile;
    @FXML private TextField pathDest;
    @FXML private CheckBox pathDisabled;

    /* EDITING/NEW */
    @FXML private Group editing;

    /* READY */
    @FXML private Group ready;

    /* PATH BTNS */
    @FXML private Group pathBtns;

    private Archive selectedArchive;
    private Paths selectedPaths;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        files = new TreeItem<>("Files");
        files.setExpanded(true);
        collections.setRoot(files);
        initEvents();
    }

    public void postInit(){
        updateUI();
    }

    private void initEvents(){
        collections.setCellFactory(tv -> new TreeCell<Object>() {
            @Override
            public void updateItem(Object item1, boolean empty) {
                super.updateItem(item1, empty);
                if (empty) {
                    setText("");
                    setGraphic(null);
                } else {
                    if (item1 instanceof Paths) {
                        Paths p = (Paths) item1;
                        setStatus(p.toString(), p.getStatus());
                        if (p.isDisabled()) {
                            setStyle("-fx-text-fill: #7a7a7a;");
                        } else {
                            setStyle("-fx-text-fill: #000;");
                        }
                    } else if (item1 instanceof Archive) {
                        Archive a = (Archive) item1;
                        setStatus(a.toString(), a.getStatus());
                        setStyle("-fx-text-fill: #000;");
                    } else if (item1 instanceof String) {
                        setText(item1.toString());
                        setStyle("-fx-text-fill: #000;");
                    }
                }

            }

            private void setStatus(String s, Utils.STATUS status) {
                setText(s);
                if (status != Utils.STATUS.READY){
                    Text statusText = new Text(status.name().substring(0,1));
                    statusText.setFill(status == Utils.STATUS.EDITING ? Color.BLUE : Color.RED );
                    statusText.setTranslateY(-1);
                    setGraphic(statusText);
                }
            }
        });
        pathFile.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedPaths == null) return;
            setText(pathFile, selectedPaths.getFile(), newValue);
        });
        pathDest.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedPaths == null) return;
            setText(pathDest, selectedPaths.getDest(), newValue);
        });
    }

    private void setText(TextField tf, File f, Boolean b){
        if (b){
            tf.setText(f == null ? "" : f.getAbsolutePath());
        }else{
            tf.setText(f == null ? "" : Utils.getShorthandPath(f));
        }
    }

    private void updateUI(){
        updateFileList();
        updateFileDisplay();
    }

    private void updateFileList(){
        files.getChildren().clear();
        for (Archive a: loader.getArchiveMap().values()){
            files.getChildren().add(newTreeItem(a));
        }
        for (Archive a: loader.getNewArchives()){
            files.getChildren().add(newTreeItem(a));
        }
        if (files.getChildren().size() == 0){
            editing.setVisible(false);
            ready.setVisible(false);
            pathBtns.setVisible(false);
            fileName.setEditable(false);
            pathDest.setEditable(false);
            pathFile.setEditable(false);
            archiveFile.setDisable(true);
            pathDisabled.setDisable(true);
        }else{
            pathBtns.setVisible(true);
            archiveFile.setDisable(false);
            pathDisabled.setDisable(false);
        }

        if (selectedArchive != null){
            for (TreeItem<Object> item: files.getChildren()){
                if (item.getValue() == selectedArchive){
                    item.setExpanded(true);
                }
            }
        }else {
            if(files.getChildren().size() > 0){
                files.getChildren().get(0).setExpanded(true);
            }
        }
    }

    private void updateFileDisplay(){
        if (selectedArchive != null){
            fileName.setText(selectedArchive.getName());
            if (selectedArchive.getName() != null){
                fileName.positionCaret(selectedArchive.getName().length());
            }
            archiveFile.setSelected(selectedArchive.getArchiveFiles());
            String lastModText = "Last Mod: "+selectedArchive.getLastMod();
            lastMod.setText(lastModText);
            String enabledText = String.format("%d/%d Paths enabled",
                    selectedArchive.getPathsList().stream().filter(Paths::isEnabled).count(),
                    selectedArchive.getPathsList().size());
            enabled.setText(enabledText);

            pathFile.setText(selectedPaths.getFile() == null ? "" : Utils.getShorthandPath(selectedPaths.getFile()));
            pathDest.setText(selectedPaths.getDest() == null ? "" : Utils.getShorthandPath(selectedPaths.getDest()));
            pathDisabled.setSelected(selectedPaths.getDisabled());
            if (selectedArchive.getStatus().equals(Utils.STATUS.READY)){
                fileName.setEditable(false);
                if (selectedPaths.getStatus().equals(Utils.STATUS.READY)){
                    pathDest.setEditable(false);
                    pathFile.setEditable(false);
                }
                editing.setVisible(false);
                ready.setVisible(true);
            }else{
                if (selectedArchive.getStatus().equals(Utils.STATUS.NEW) && selectedArchive.getName() == null){
                    fileName.setText("");
                }
                fileName.setEditable(true);
                pathDest.setEditable(true);
                pathFile.setEditable(true);
                editing.setVisible(true);
                ready.setVisible(false);
            }
        }
    }

    private void createArchive(){
        Archive a = new Archive();
        loader.getNewArchives().add(a);
        selectedArchive = a;
        selectedPaths = selectedArchive.getPathsList().get(0);
        updateUI();
    }

    private void createPath(){
        selectedArchive.createNewPath();
        selectedPaths = selectedArchive.getPathsList().get(selectedArchive.getPathsList().size()-1);
        updateUI();
    }

    private TreeItem<Object> newTreeItem(Archive a){
        ChangeListener<Boolean> expandedListener = (obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded){
                ReadOnlyProperty<?> expandedProperty = (ReadOnlyProperty<?>) obs;
                Object ob = expandedProperty.getBean();
                for (TreeItem<Object> item : collections.getRoot().getChildren()){
                    if (item == ob){
                        collections.getSelectionModel().select(item);
                        if (item.getValue() instanceof Archive){
                            selectedArchive = (Archive) item.getValue();
                            if(selectedPaths == null || selectedPaths.getParent() != selectedArchive) selectedPaths = a.getPathsList().get(0);
                            updateFileDisplay();
                        }
                    }else{
                        item.setExpanded(false);
                    }
                }
            }
        };
        TreeItem<Object> item = new TreeItem<>(a);
        for (Paths p: a.getPathsList()){
            TreeItem<Object> path = new TreeItem<>(p);
            item.getChildren().add(path);
        }
        item.expandedProperty().addListener(expandedListener);
        return item;
    }

    @FXML private void editArchive(){
        selectedArchive.setStatus(Utils.STATUS.EDITING);
        updateUI();
    }

    @FXML private void finishEditing(ActionEvent e){
        Button b = (Button) e.getSource();
        if (b.getId().equals("cancelBtn")){
            if (selectedArchive.getStatus().equals(Utils.STATUS.EDITING)){
                selectedArchive.load();
            }else{
                loader.getNewArchives().remove(selectedArchive);
                selectedArchive = null;
                selectedPaths = null;
                updateUI();
            }
        }else{
            selectedArchive.save();
            loader.checkForFiles();
        }
        updateUI();
    }

    @FXML private void selectArchive(){
        TreeItem<Object> item = collections.getSelectionModel().getSelectedItem();
        if (item == null || item.getValue() instanceof String) return;
        if (!item.isExpanded()){
            item.setExpanded(true);
        }
        Object ob = item.getValue();
        if (ob instanceof Archive){
            Archive a = (Archive) ob;
            if (a == selectedArchive) return;
            selectedArchive = a;
            if(selectedPaths == null || selectedPaths.getParent() != selectedArchive) selectedPaths = a.getPathsList().get(0);
        }else if (ob instanceof Paths){
            Paths p = (Paths) ob;
            if (p == selectedPaths) return;
            selectedArchive = p.getParent();
            selectedPaths = p;
        }
        updateFileDisplay();
    }

    @FXML private void create(ActionEvent e){
        Button b = (Button) e.getSource();
        if (b.getId().equals("createArchive")){
            createArchive();
        }else{
            createPath();
        }
    }

    @FXML private void updateArchiveName(KeyEvent e){
        System.out.println(e.getCode().isLetterKey());
        try{
            if (Utils.isWritable(e) && (new KeyCodeCombination(e.getCode(), KeyCodeCombination.CONTROL_ANY, KeyCodeCombination.ALT_ANY).match(e))) return;
        }catch (IllegalArgumentException i){
            return;
        }
        String name = fileName.getText();
        if (!name.equals(selectedArchive.getName())){
            selectedArchive.setName(name);
        }
        updateUI();
    }

    @FXML private void updateArchiveArchiveFile(){

        boolean archiveFiles = archiveFile.isSelected();
        if (archiveFiles != selectedArchive.getArchiveFiles()){
            selectedArchive.setArchiveFiles(archiveFiles);
        }
        updateUI();
    }
}
