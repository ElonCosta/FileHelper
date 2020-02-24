package Main;

import ArchiveLoader.Archive.Archive;
import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.Controllers.AppController;
import Log.Log;
import Utils.Utils;
import com.Hasher.Hasher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Utils.Utils.*;

public class Launcher extends Application {

    public static Scene scene;
    public static AppController app;
    public static Configurations config;
    public static Hasher hasher;
    public static Log log;
    public static Loader loader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(e -> {
            config.save();
        });

        primaryStage.setTitle(UIVE.TITLE.getVar());
        primaryStage.setResizable(false);
        primaryStage.setMaxWidth(607);
//        primaryStage.setMaxHeight(479);

        config = new Configurations();
        config.loadHasher();
        FXMLLoader fxmlLoader = new FXMLLoader(AppUI);
        scene = new Scene(fxmlLoader.load());
        app = fxmlLoader.getController();
//        log = new Log();
//        config.loadCommands();
        loader = new Loader();

        primaryStage.setScene(scene);
        primaryStage.show();

        loader.load();
        app.postInit();
    }
}
