package br.com.claw;

import br.com.claw.archiveLoader.Configurations;
import br.com.claw.archiveLoader.Loader;
import br.com.claw.iterface.controllers.AppController;
import br.com.Hasher.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import br.com.claw.utils.FXMLLoader;

import static br.com.claw.utils.Utils.*;
import static br.com.claw.enums.FXML_FILES.*;

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
        FXMLLoader fxmlLoader = new FXMLLoader(APP_UI);
        scene = new Scene(fxmlLoader.load());
        app = fxmlLoader.getController();
        loader = new Loader();

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setHeight(466d);
        primaryStage.setWidth(618d);

        loader.load();
        app.postInit();
    }
}
