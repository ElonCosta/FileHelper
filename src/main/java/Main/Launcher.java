package Main;

import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.Controllers.AppController;
import Log.Log;
import Utils.Utils;
import com.Hasher.Hasher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static Utils.Utils.*;

public class Launcher extends Application {

    public static boolean sceneLoaded = false;

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
            log.readCommand("close");
        });

        config = new Configurations();
        config.loadHasher();
        FXMLLoader fxmlLoader = new FXMLLoader(AppUI);
        scene = new Scene(fxmlLoader.load());
        sceneLoaded = true;
        app = fxmlLoader.getController();
        log = new Log();
        config.loadCommands();
        loader = new Loader();
        loader.load();
        app.loadFiles();

        primaryStage.setTitle(UIVE.TITLE.getVar());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setMaxWidth(536);
        primaryStage.setMaxHeight(479);

    }
}
