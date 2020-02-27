package Interface.Components.FileChooser;

import Utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class FileChooser {

    public enum DETAILS{
        NAME(0, "Name"),
        TYPE(1,"Type"),
        DATE(2, "Date modified"),
        SIZE(3, "Size");

        @Getter
        private int i;
        @Getter
        private String name;

        DETAILS(int i, String name){
            this.i = i;
            this.name = name;
        }
    }

    @Getter
    private List<FileExtension> fileExtensions;
    @Getter
    private File initialFolder;

    private Stage stage;
    private FileChooserController controller;

    public FileChooser(){
        try{
            stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.FileChooserUI);
            Scene scene = new Scene(fxmlLoader.load());
            controller = fxmlLoader.getController();
            stage.setScene(scene);
            controller.setStage(stage);
            setFileExtensions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public FileChooser(String... fileExtensions){
        this();
        setFileExtensions(fileExtensions);
    }

    public FileChooser(File initialFolder){
        this();
        setInitialFolder(initialFolder);
    }

    public FileChooser(File initialFolder, String... fileExtensions){
        this();
        setInitialFolder(initialFolder);
        setFileExtensions(fileExtensions);
    }

    public File getFile(){
        controller.setFilesOnly(true);
        controller.load();
        stage.showAndWait();
        return controller.getChoosenFile();
    }

    public File getFolder(){
        controller.setFoldersOnly(true);
        controller.load();
        stage.showAndWait();
        return controller.getChoosenFile();
    }

    public File getAny(){
        controller.load();
        stage.showAndWait();
        return controller.getChoosenFile();
    }

    public void setFileExtensions(String... fileExtensions) {
        List<FileExtension> extensions = FileExtension.createExtensionList(fileExtensions);
        this.fileExtensions = extensions;
        controller.setExtensions(extensions);
    }

    public void setInitialFolder(File initialFolder) {
        if (initialFolder == null) return;
        this.initialFolder = initialFolder.getParentFile();
        controller.setInitialFolder(initialFolder.getParentFile());
    }

}

