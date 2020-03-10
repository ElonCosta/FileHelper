package Interface.Components.FileChooser;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FileChooserController implements Initializable {

    @FXML private TableView fileTable;
    @FXML private ComboBox<FileExtension> extensionsBox;
    @FXML private TextField selectedFle;
    @FXML private TextField pathFld;

    @FXML private Button previous;
    @FXML private Button next;
    @FXML private Button gotoParent;

    @Setter
    private File selectedFile;
    @Setter
    private Stage stage;
    @Getter
    private File chosenFile;
    @Getter @Setter
    private Boolean foldersOnly = false;
    @Getter @Setter
    private Boolean filesOnly = false;
    @Setter
    private FileChooser fileChooser;

    private TableColumn[] columns = new TableColumn[4];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileTable.setFocusTraversable(false);
        pathFld.setFocusTraversable(false);
    }

    public void load(){
        loadUI();
        gotoParent.setDisable(fileChooser.initialFolder.getParentFile() == null);
        next.setDisable(!fileChooser.historic.hasNext());
        previous.setDisable(!fileChooser.historic.hasPrevious());
        var children = fileChooser.initialFolder.listFiles();
        pathFld.setText(fileChooser.initialFolder.getAbsolutePath());
        pathFld.selectPositionCaret(pathFld.getText().length());
        List<FileDetails> details = getFileList(children);
        createColumns();
        FileDetails.sortBySize(details,true);
        for (FileDetails f: details){
                fileTable.getItems().add(f);
        }
    }

    private List<FileDetails> getFileList(File[] children) {
        List<FileDetails> details = new ArrayList<>();
        if (children != null){
            for (File f: children){
                FileDetails d = new FileDetails(f);
                if (!f.isDirectory() && fileChooser.extensions.stream().noneMatch(e -> e.matches(d.getType()))) continue;
                if (foldersOnly && !f.isDirectory()) continue;
                details.add(d);
            }
        }
        return details;
    }

    private void loadUI(){
        for (FileExtension f: fileChooser.extensions){
            extensionsBox.getItems().add(f);
        }
        extensionsBox.getSelectionModel().select(0);
    }

    private void reload(){
        fileTable.getItems().clear();
        gotoParent.setDisable(fileChooser.initialFolder.getParentFile() == null);
        next.setDisable(!fileChooser.historic.hasNext());
        previous.setDisable(!fileChooser.historic.hasPrevious());
        File[] children = fileChooser.initialFolder.listFiles();
        pathFld.setText(fileChooser.initialFolder.getAbsolutePath());
        pathFld.positionCaret(pathFld.getText().length());
        List<FileDetails> details = getFileList(children);
        FileDetails.sortBySize(details,true);
        for (FileDetails f: details){
            if (f.getFile().isDirectory()) fileTable.getItems().add(0,f);
            else fileTable.getItems().add(f);
        }
    }

    public void createColumns(){
        TableColumn<String, FileDetails> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        columns[0] = nameColumn;
        TableColumn<String, FileDetails> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        columns[1] = typeColumn;
        TableColumn<String, FileDetails> lastModColumn = new TableColumn<>("Date modified");
        lastModColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        columns[2] = lastModColumn;
        TableColumn<String, FileDetails> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        columns[3] = sizeColumn;

        Arrays.asList(columns).forEach(c-> c.setSortable(false));

        fileTable.setRowFactory( tv -> {
            TableRow<FileDetails> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()){
                    FileDetails rowData = row.getItem();
                    if(event.getClickCount() == 1){
                        if (filesOnly && !rowData.getFile().isDirectory()){
                            selectedFile = rowData.getFile();
                            selectedFle.setText(selectedFile.getName());
                        }else if(foldersOnly && rowData.getFile().isDirectory()){
                            selectedFile = rowData.getFile();
                            selectedFle.setText(selectedFile.getName());
                        }else if (!foldersOnly && !filesOnly){
                            selectedFile = rowData.getFile();
                            selectedFle.setText(selectedFile.getName());
                        }
                    }else if (event.getClickCount() == 2) {
                        if (rowData.getFile().isDirectory()){
                            fileChooser.initialFolder = rowData.getFile();
                            fileChooser.historic.addToHistoric(rowData);
                            reload();
                        }
                    }
                }
            });
            return row ;
        });
        fileTable.setOnSort( s -> {
        });
        fileTable.getColumns().addAll(columns);
    }

    @FXML private void previousFolder(){
        fileChooser.initialFolder = fileChooser.historic.previous().getFile();
        reload();
    }

    @FXML private void nextFolder(){
        fileChooser.initialFolder = fileChooser.historic.next().getFile();
        reload();
    }

    @FXML private void parentFolder(){
        fileChooser.historic.addToHistoric(new FileDetails(fileChooser.initialFolder));
        fileChooser.initialFolder = fileChooser.initialFolder.getParentFile();
        reload();
    }

    @FXML private void chooseFile() {
        if (selectedFile != null) chosenFile = selectedFile;
        stage.close();
    }

    @FXML private void cancel(){
        stage.close();
    }
}
