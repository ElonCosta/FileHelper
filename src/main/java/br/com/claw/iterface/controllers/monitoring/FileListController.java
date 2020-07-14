package br.com.claw.iterface.controllers.monitoring;

import br.com.claw.archiveLoader.archive.Archive;
import br.com.claw.archiveLoader.archive.Paths;
import br.com.claw.enums.STATUS;
import br.com.claw.iterface.controllers.GenericController;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static br.com.claw.Launcher.loader;
import static br.com.claw.utils.ControllersUtils.elementDisplay;

public class FileListController extends GenericController {

    @Setter
    private MonitoringController monitoringController;

    @Getter
    @FXML private TreeView<Object> filesTreeView;
    @Getter
    private TreeItem<Object> files;

    @FXML public Button newArchive;

    @Getter @Setter
    private TreeItem<Object> selectedArchiveItem;
    @Getter @Setter
    private TreeItem<Object> selectedPathsItem;

    @Override
    protected void postInit() {

    }

    @Override
    protected void initEvents() {
        filesTreeView.setCellFactory(tv -> new TreeCell<>() {
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
                if (status != STATUS.READY) {
                    Text statusText = new Text(status.name().substring(0, 1));
                    statusText.setTranslateY(-1);
                    setGraphic(statusText);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        files = new TreeItem<>("Files");
        files.setExpanded(true);
        filesTreeView.setRoot(files);
        initEvents();
    }

    public void update(){
        filesTreeView.refresh();
        List<Archive> archives = loader.getArchives().stream().filter(p -> files.getChildren().stream().noneMatch(c -> p == c.getValue())).collect(Collectors.toList());
        for (Archive p: archives) {
            files.getChildren().add(newTreeItem(p));
        }

        if (monitoringController.getSelectedArchive() != null){
            files.getChildren().stream().filter(c -> c.getValue() == monitoringController.getSelectedArchive()).collect(Collectors.toCollection(ArrayList::new)).get(0).setExpanded(true);

            if (selectedArchiveItem.getChildren().size() != monitoringController.getSelectedArchive().getPathsList().size()){
                List<Paths> paths = monitoringController.getSelectedArchive().getPathsList().stream().filter(p -> selectedArchiveItem.getChildren().stream().noneMatch(c -> p == c.getValue())).collect(Collectors.toList());
                for (Paths p: paths) {
                    selectedArchiveItem.getChildren().add(new TreeItem<>(p));
                }
            }
        }else {
            if(files.getChildren().size() > 0){
                files.getChildren().get(0).setExpanded(true);
            }
        }
        elementDisplay(monitoringController,"updateFileList");
    }

    protected TreeItem<Object> newTreeItem(Archive a){
        ChangeListener<Boolean> expandedListener = (obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded){
                ReadOnlyProperty<?> expandedProperty = (ReadOnlyProperty<?>) obs;
                Object ob = expandedProperty.getBean();
                for (TreeItem<Object> item : filesTreeView.getRoot().getChildren()){
                    if (item == ob){
                        filesTreeView.getSelectionModel().select(item);
                        if (item.getValue() instanceof Archive){
                            monitoringController.selectArchive((Archive) item.getValue());
                            selectedArchiveItem = item;
                            if(selectedPathsItem == null || selectedPathsItem.getParent() != selectedArchiveItem) monitoringController.selectPath(a.getPathsList().get(0));
                            selectedPathsItem = selectedArchiveItem.getChildren().get(0);
//                            updateFileDisplay();
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

    @FXML private void newArchive(ActionEvent e){
        monitoringController.createArchive();
    }
}
