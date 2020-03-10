package Interface.Components.FileChooser;

import Utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.File;
import java.util.List;

public class FileChooser {

    public enum DETAILS{
        NAME(0, "Name"),
        TYPE(1, "Type"),
        DATE(2, "Date modified"),
        SIZE(3, "Size");

        @Getter
        int i;
        @Getter
        String name;

        DETAILS(int i, String name){
            this.i = i;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Getter
    List<FileExtension> extensions;
    @Getter
    File initialFolder;
    @Getter
    FileHistoric historic;

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
            controller.setFileChooser(this);
            initialFolder = new File(System.getProperty("user.dir"));
            historic = new FileHistoric(new FileDetails(initialFolder));
            setExtensions();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public FileChooser(String... fileExtensions){
        this();
        setExtensions(fileExtensions);
    }

    public FileChooser(File initialFolder){
        this();
        setInitialFolder(initialFolder);
    }

    public FileChooser(File initialFolder, String... fileExtensions){
        this();
        setInitialFolder(initialFolder);
        setExtensions(fileExtensions);
    }

    public File getFile(){
        controller.setFilesOnly(true);
        controller.load();
        stage.showAndWait();
        return controller.getChosenFile();
    }

    public File getFolder(){
        controller.setFoldersOnly(true);
        controller.load();
        stage.showAndWait();
        return controller.getChosenFile();
    }

    public File getAny(){
        controller.load();
        stage.showAndWait();
        return controller.getChosenFile();
    }

    public void setExtensions(String... extensions) {
        this.extensions = FileExtension.createExtensionList(extensions);
    }

    public void setInitialFolder(File initialFolder) {
        if (initialFolder == null) return;
        this.initialFolder = initialFolder.getParentFile();
        historic = new FileHistoric(new FileDetails(initialFolder.getParentFile()));
    }

}

