package Interface.Controllers;

import ArchiveLoader.Archive.Archive;
import ArchiveLoader.Archive.Paths;
import Interface.Components.FileChooser.FileChooser;
import Utils.Utils;
import afester.javafx.svg.SvgLoader;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import static Main.Launcher.loader;
import static Utils.Utils.STATUS;

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

    /* CHECK BUTTONS */
    @FXML private Button checkThis;
    @FXML private Button checkThisPath;
    @FXML private Button check;

    /* PAUSE BUTTON */
    @FXML private ToggleButton pause;

    /* NEW/REMOVE BUTTON */

    @FXML private Button createPath;
    @FXML private Button createArchive;
    @FXML private Button removePath;
    @FXML private Button removeArchive;

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
                if (empty || item1 == null) {
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

            private void setStatus(String s, STATUS status) {
                setText(s);
                if (status != STATUS.READY && status != STATUS.CHECKING){
                    Text statusText = new Text(status.name().substring(0,1));
                    statusText.setFill(status == STATUS.EDITING ? Color.BLUE : Color.RED );
                    statusText.setTranslateY(-1);
                    setGraphic(statusText);
                }else if(status == STATUS.CHECKING){
                    ProgressIndicator pi = new ProgressIndicator();
                    pi.setMaxWidth(15);
                    pi.setMaxHeight(15);
                    pi.setProgress(-1);
                    setGraphic(pi);
                }else{
                    setGraphic(null);
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
        loadButtons();
    }

    private void setText(TextField tf, File f, Boolean b){
        if (b){
            tf.setText(f == null ? "" : f.getAbsolutePath());
        }else{
            tf.setText(f == null ? "" : Utils.getShorthandPath(f));
        }
    }

    public void updateUI(){
        updateFileList();
        updateFileDisplay();
    }

    void updateFileList(){
//        files.getChildren().clear();
        collections.refresh();
        for (Archive a: loader.getArchives()){
            if (files.getChildren().stream().noneMatch(c -> c.getValue().equals(a))){
                files.getChildren().add(newTreeItem(a));
            }
        }
        for (Archive a: loader.getNewArchives()){
            if (files.getChildren().stream().noneMatch(c -> c.getValue().equals(a))){
                files.getChildren().add(newTreeItem(a));
            }
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

    public void updateFileDisplay(){
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
            if (selectedArchive.getStatus().equals(STATUS.READY) || selectedArchive.getStatus().equals(STATUS.CHECKING)){
                fileName.setEditable(false);
                editing.setVisible(false);
                ready.setVisible(true);
                if (selectedPaths.getStatus().equals(STATUS.READY) || selectedPaths.getStatus().equals(STATUS.CHECKING)){
                    pathDest.setEditable(false);
                    pathFile.setEditable(false);
                    pathDisabled.setDisable(false);
                }else{
                    pathDest.setEditable(true);
                    pathFile.setEditable(true);
                    editing.setVisible(true);
                    if(selectedPaths.getStatus() == STATUS.NEW){
                        pathDisabled.setDisable(true);
                    }
                }
            }else{
                if (selectedArchive.getStatus().equals(STATUS.NEW) && selectedArchive.getName() == null){
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
        selectedPaths = a.createNewPath();
        updateUI();
    }

    private void createPath(){
        selectedPaths = selectedArchive.createNewPath();
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
        for (Paths p: a.getNewPathsList()){
            TreeItem<Object> path = new TreeItem<>(p);
            item.getChildren().add(path);
        }
        item.expandedProperty().addListener(expandedListener);
        return item;
    }

    private void loadButtons(){

        try {
        checkThisPath.setGraphic(createGraphic(new File(Utils.CheckThisFile.toURI()), 0.035));
        checkThisPath.setTooltip(new Tooltip("Check selected path"));

        checkThis.setGraphic(createGraphic(new File(Utils.Check.toURI()), 0.2));
        checkThis.setTooltip(new Tooltip("Check selected archiving"));

        check.setGraphic(createGraphic(new File(Utils.Check.toURI()), 0.4));
        check.setTooltip(new Tooltip("Check archives"));
        pause.setGraphic(createGraphic(new File(Utils.Pause.toURI()),0.2));

        removeArchive.setGraphic(createGraphic(new File(Utils.remove.toURI()),0.3));
        createArchive.setGraphic(createGraphic(new File(Utils.add.toURI()),0.3));

        removePath.setGraphic(createGraphic(new File(Utils.remove.toURI()),0.25));
        createPath.setGraphic(createGraphic(new File(Utils.add.toURI()),0.25));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Group createGraphic(File is, Double scaleX, Double scaleY){
        SvgLoader loader = new SvgLoader();
        Group checkThisPathSvg = loader.loadSvg(is.getAbsolutePath());
        checkThisPathSvg.setScaleY(scaleX);
        checkThisPathSvg.setScaleX(scaleY);
        return new Group(checkThisPathSvg);
    }

    private Group createGraphic(File is, Double scaleSqrd){
        return createGraphic(is, scaleSqrd, scaleSqrd);
    }

    /* FXML METHODS */

    @FXML private void editArchive(){
        selectedArchive.setStatus(STATUS.EDITING);
        updateUI();
    }

    @FXML private void finishEditing(ActionEvent e){
        Button b = (Button) e.getSource();
        if (b.getId().equals("cancelBtn")){
            if (selectedArchive.getStatus() == STATUS.NEW){
                loader.getNewArchives().remove(selectedArchive);
                updateUI();
                selectedArchive = loader.getNewArchives().isEmpty() ? loader.getArchives().get(loader.getArchives().size()-1) : loader.getNewArchives().get(loader.getNewArchives().size()-1) ;

            }else if(selectedArchive.getStatus() == STATUS.EDITING){
                selectedArchive.load();
            }else if (selectedPaths.getStatus() == STATUS.NEW){

            }else if( selectedPaths.getStatus() == STATUS.EDITING){
                selectedPaths.load();
            }
        }else{

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
            if (!Utils.isWritable(e) && (new KeyCodeCombination(e.getCode(), KeyCodeCombination.CONTROL_ANY, KeyCodeCombination.ALT_ANY).match(e))) return;
        }catch (IllegalArgumentException i){
            return;
        }
        String name = fileName.getText();
        if (!name.equals(selectedArchive.getName())){
            selectedArchive.setName(name);
        }
        updateUI();
    }

    @FXML private void updateArchiveFile(){

        boolean archiveFiles = archiveFile.isSelected();
        if (archiveFiles != selectedArchive.getArchiveFiles()){
            selectedArchive.setArchiveFiles(archiveFiles);
        }
        updateFileDisplay();
    }

    @FXML private void check(ActionEvent e){
        Button b = (Button) e.getSource();
        if (b.getId().equals("check")){
            loader.check();
        }
    }

    @FXML private void searchFiles(ActionEvent e){
        FileChooser fc = new FileChooser();
        Button b = (Button) e.getSource();
        if (b.getId().equals("getFileBtn")){
            fc.setInitialFolder(selectedPaths.getFile());
            File f = fc.getAny();
            if (f == null) return;
            System.out.println(f.getAbsolutePath());
        }else{
            fc.setInitialFolder(selectedPaths.getDest());
            File f = fc.getFolder();
            if (f == null) return;
            System.out.println(f.getAbsolutePath());
        }
    }
}
