package Interface.Controllers.NewFile;

import ArchiveLoader.FilesArchive;
import Main.Launcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static Utils.Utils.*;

import static Main.Launcher.*;

public class PathsTabController implements Initializable {

    private FilesArchive.Paths path;
    private Tab tab;

    @FXML private TextField fileFld;
    @FXML private Button fileBtn;

    @FXML private TextField destFld;
    @FXML private Button destBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void load(FilesArchive.Paths path, Tab tab){
        this.tab = tab;
        this.path = path;
        this.path.setOnLatest(true);
        destFld.setText(getShorthandPath(this.path.getDest()));
        initEvents();
    }

    private void initEvents(){
        fileFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                String path = this.path.getFile() != null ? this.path.getFile().getAbsolutePath() : "";
                fileFld.setText(path);
            }else{
                if (!fileFld.getText().trim().equals("")){
                    File f = new File(fileFld.getText());
                    path.setFile(f);
                }
                String path = this.path.getFile() != null ? getShorthandPath(this.path.getFile()) : "";
                fileFld.setText(path);
            }
        });
        destFld.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                String path = this.path.getDest() != null ? this.path.getDest().getAbsolutePath() : "";
                destFld.setText(path);
            }else{
                File f = new File(destFld.getText());
                path.setOnLatest(path.isOnLatest());
                path.setDest(f);
                String path = this.path.getDest() != null ? getShorthandPath(this.path.getDest()) : "";
                destFld.setText(path);
            }
        });
    }

    @FXML private void selectFolder(ActionEvent e){
        Button b = (Button) e.getSource();
        File file;
        switch (b.getId()){
            case "destBtn":
                DirectoryChooser dc = new DirectoryChooser();
                file = dc.showDialog(Launcher.scene.getWindow());
                if (file != null && !file.equals(path.getDest())){
                    path.setDest(file);
                    destFld.setText(getShorthandPath(file));
                }
                break;
            case "fileBtn":
                FileChooser fc = new FileChooser();
                file = fc.showOpenDialog(scene.getWindow());
                if (file != null && !file.equals(path.getFile())){
                    path.setFile(file);
                    fileFld.setText(getShorthandPath(file));
                }
                break;
        }
    }

    public void updateDestField(){
        destFld.setText(getShorthandPath(path.getDest()));
    }

    public boolean isCreatable(){
        if (fileFld.getText() == null || fileFld.getText().trim().equals("")){
            tab.getTabPane().getSelectionModel().select(tab);
            fileFld.requestFocus();
            return false;
        }else if (destFld.getText() == null || destFld.getText().trim().equals("")){
            tab.getTabPane().getSelectionModel().select(tab);
            destFld.requestFocus();
            return false;
        }
        return true;
    }
}
