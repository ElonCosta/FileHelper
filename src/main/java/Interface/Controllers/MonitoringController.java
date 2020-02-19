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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

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


    private Archive selectedArchive;
    private Paths selectedPaths;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        files = new TreeItem<Object>("Files");
        files.setExpanded(true);
        collections.setRoot(files);
        initEvents();
    }

    public void postInit(){
        updateUI();
    }

    private void initEvents(){
        collections.setCellFactory(tv -> {
            TreeCell cell = new TreeCell<Object>() {
                @Override
                public void updateItem(Object item1, boolean empty) {
                    super.updateItem(item1, empty);
                    if (empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        if (item1 instanceof Paths) {
                            Paths p = (Paths) item1;
                            setText(p.toString());
                            if (p.getStatus() != Utils.STATUS.READY){
                                Text status = new Text(p.getStatus().name().substring(0,1));
                                status.setFill(p.getStatus() == Utils.STATUS.EDITING ? Color.BLUE : Color.RED );
                                status.setTranslateY(-1);
                                setGraphic(status);
                            }
                            if (p.getDisabled()) {
                                setStyle("-fx-text-fill: #7a7a7a;");
                            } else {
                                setStyle("-fx-text-fill: #000;");
                            }
                        } else if (item1 instanceof Archive) {
                            Archive a = (Archive) item1;
                            setText(a.toString());
                            if (a.getStatus() != Utils.STATUS.READY){
                                Text status = new Text(a.getStatus().name().substring(0,1));
                                status.setFill(a.getStatus() == Utils.STATUS.EDITING ? Color.BLUE : Color.RED );
                                status.setTranslateY(-1);
                                setGraphic(status);
                            }
                            setStyle("-fx-text-fill: #000;");
                        } else if (item1 instanceof String) {
                            setText(item1.toString());
                            setStyle("-fx-text-fill: #000;");
                        }
                    }

                }
            };
            return cell;
            });
        pathFile.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                pathFile.setText(selectedPaths.getFile().getAbsolutePath());
            }else{
                pathFile.setText(Utils.getShorthandPath(selectedPaths.getFile()));
            }
        });
        pathDest.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                pathDest.setText(selectedPaths.getDest().getAbsolutePath());
            }else{
                pathDest.setText(Utils.getShorthandPath(selectedPaths.getDest()));
            }
        });
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

    private void updateUI(){
        updateFileList();
        updateFileDisplay();
    }

    private void updateFileList(){
        ChangeListener<Boolean> expandedListener = (obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded){
                ReadOnlyProperty<?> expandedProperty = (ReadOnlyProperty<?>) obs;
                Object ob = expandedProperty.getBean();
                for (TreeItem<Object> item : collections.getRoot().getChildren()){
                    if (item == ob){
                        collections.getSelectionModel().select(item);
                        if (item.getValue() instanceof Archive){
                            Archive a = (Archive) item.getValue();
                            selectedArchive = a;
                            if(selectedPaths == null || selectedPaths.getParent() != selectedArchive) selectedPaths = a.getPathsList().get(0);
                            updateFileDisplay();
                        }
                    }else{
                        item.setExpanded(false);
                    }
                }
            }
        };
        files.getChildren().clear();
        for (Archive a: loader.getArchiveMap().values()){
            TreeItem<Object> item = new TreeItem<>(a);
            for (Paths p: a.getPathsList()){
                TreeItem<Object> path = new TreeItem<>(p);
                item.getChildren().add(path);
            }
            item.expandedProperty().addListener(expandedListener);
            files.getChildren().add(item);
        }
        if (selectedArchive != null){
            for (TreeItem<Object> item: files.getChildren()){
                if (item.getValue() == selectedArchive){
                    item.setExpanded(true);
                }
            }
        }else {
            files.getChildren().get(0).setExpanded(true);
        }
    }

    private void updateFileDisplay(){
        if (selectedArchive != null){
            fileName.setText(selectedArchive.getName());
            archiveFile.setSelected(selectedArchive.getArchiveFiles());
            String lastModText = "Last Mod: "+selectedArchive.getLastMod();
            lastMod.setText(lastModText);
            String enabledText = String.format("%d/%d Paths enabled",
                    selectedArchive.getPathsList().stream().filter(p->!p.getDisabled()).count(),
                    selectedArchive.getPathsList().size());
            enabled.setText(enabledText);

            pathFile.setText(Utils.getShorthandPath(selectedPaths.getFile()));
            pathDest.setText(Utils.getShorthandPath(selectedPaths.getDest()));
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
                fileName.setEditable(true);
                pathDest.setEditable(true);
                pathFile.setEditable(true);
                editing.setVisible(true);
                ready.setVisible(false);
            }
        }
    }

    @FXML private void editArchive(){
        selectedArchive.setStatus(selectedArchive.getStatus() == Utils.STATUS.READY ? Utils.STATUS.EDITING : Utils.STATUS.READY);
        updateUI();
    }

    @FXML private void finishEditing(ActionEvent e){
        Button b = (Button) e.getSource();
        if (b.getId().equals("cancelBtn")){
            selectedArchive.load();
        }else{
            selectedArchive.save();
        }
        updateUI();
    }
}
