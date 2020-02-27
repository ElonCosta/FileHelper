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
import java.util.List;
import java.util.ResourceBundle;

public class FileChooserController implements Initializable {

    @FXML private TableView fileTable;
    @FXML private ComboBox<FileExtension> extensionsBox;
    @FXML private TextField selectedFle;

    @Setter
    private File selectedFile;
    @Setter
    private Stage stage;
    @Getter
    private File choosenFile;
    @Getter @Setter
    private Boolean foldersOnly = false;
    @Getter @Setter
    private Boolean filesOnly = false;
    @Setter
    private List<FileExtension> extensions;
    @Setter
    private File initialFolder;

    private TableColumn<String, FileDetails>[] columns = new TableColumn[4];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileTable.setFocusTraversable(false);
    }

    public void load(){
        if (initialFolder == null) initialFolder = new File(System.getProperty("user.dir"));
        loadUI();
        File[] children = initialFolder.listFiles();
        List<FileDetails> details = new ArrayList<>();
        if (children != null){
            for (File f: children){
                FileDetails d = new FileDetails(f);
                if (!f.isDirectory() && extensions.stream().noneMatch(e -> e.matches(d.getType()))) continue;
                if (foldersOnly && !f.isDirectory()) continue;
                details.add(d);
            }
        }
        createColumns();
        fileTable.getColumns().add(columns[0]);
        fileTable.getColumns().add(columns[1]);
        fileTable.getColumns().add(columns[2]);
        fileTable.getColumns().add(columns[3]);
        FileDetails.sortBySize(details,true);
        for (FileDetails f: details){
                fileTable.getItems().add(f);
        }
    }

    private void loadUI(){
        for (FileExtension f: extensions){
            extensionsBox.getItems().add(f);
        }
        extensionsBox.getSelectionModel().select(0);
    }

    private void reload(){
        fileTable.getItems().clear();
        File[] children = initialFolder.listFiles();
        List<FileDetails> details = new ArrayList<>();
        if (children != null){
            for (File f: children){
                FileDetails d = new FileDetails(f);
                if (!f.isDirectory() && extensions.stream().noneMatch(e -> e.matches(d.getType()))) continue;
                if (foldersOnly && !f.isDirectory()) continue;
                details.add(d);
            }
        }
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
                            initialFolder = rowData.getFile();
                            reload();
                        }
                    }
                }
            });
            return row ;
        });
        fileTable.setOnSort( s -> {
        });
    }

    @FXML private void chooseFile() {
        if (selectedFile != null) choosenFile = selectedFile;
        stage.close();
    }

    @FXML private void cancel(){
        stage.close();
    }
}
