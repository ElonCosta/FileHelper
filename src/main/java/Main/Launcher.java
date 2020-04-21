package Main;

import ArchiveLoader.Configurations;
import ArchiveLoader.Loader;
import Interface.Controllers.AppController;
import br.com.Hasher.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static Utils.Utils.*;

public class Launcher extends Application {

    public static Scene scene;
    public static AppController app;
    public static Configurations config;
    public static Hasher hasher;
    public static Loader loader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(e -> config.save());

        primaryStage.setTitle(UIVE.TITLE.getVar());
        primaryStage.setResizable(false);

        config = new Configurations();
        config.loadHasher();
        FXMLLoader fxmlLoader = new FXMLLoader(AppUI);
        scene = new Scene(fxmlLoader.load());
        app = fxmlLoader.getController();
        loader = new Loader();

        primaryStage.setScene(scene);
        primaryStage.show();

        loader.load();
        app.postInit();
    }
}
