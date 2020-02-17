package Interface.Controllers.Files;

import ArchiveLoader.FilesArchive;
import Main.Launcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static Utils.Utils.*;

public class PathsTabController implements Initializable {

    private FilesArchive.Paths paths;

    @FXML private TextField pathFld;
    @FXML private Button pathBtn;

    @FXML private TextField destFld;
    @FXML private Button destBtn;

    @FXML private CheckBox disablePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loadPaths(FilesArchive.Paths p){
        paths = p;
        pathFld.setText(getShorthandPath(p.getFile()));
        destFld.setText(getShorthandPath(p.getDest()));
        disablePath.setSelected(p.getDisabled());

        initEvents();
    }

    private void initEvents(){
        pathFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                pathFld.setText(paths.getFile().getAbsolutePath());
            }else {
                pathFld.setText(getShorthandPath(paths.getFile()));
            }
        });
        pathBtn.setOnAction(e -> {
            pathFld.requestFocus();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(paths.getFile().getParent()));
            fileChooser.setInitialFileName(paths.getFile().getName());
            File file = fileChooser.showOpenDialog(Launcher.scene.getWindow());
            if (file != null){
                paths.setFile(file.getAbsolutePath());
                pathFld.setText(getShorthandPath(file));
            }
        });

        destFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                destFld.setText(paths.getDest().getAbsolutePath());
            }else {
                destFld.setText(getShorthandPath(paths.getDest()));
            }
        });
        destBtn.setOnAction(e -> {
            destFld.requestFocus();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(paths.getDest().getParent()));
            File folder = directoryChooser.showDialog(Launcher.scene.getWindow());
            if (folder != null){
                paths.setDest(folder.getAbsolutePath()+"\\"+paths.getFile().getName());
                destFld.setText(getShorthandPath(paths.getDest()));
            }
        });
    }

    @FXML private void disablePath(){
        paths.disablePath(disablePath.isSelected());
    }
}
