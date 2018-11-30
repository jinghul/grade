package twelve.team.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main controller for transitioning
 * between scenes for the application
 */
public class Controller {
    public static final String STAGE_NAME = "Grader";
    private static Application app;
    private static Stage stage;
    private static Controller controller;

    public Controller() {

    }

    public static boolean load(String fxml_path) {
        if (app == null) {
            return false;
        }

        try {
            Parent root = FXMLLoader.load(app.getClass().getResource(fxml_path));
            stage.setScene( new Scene(root) );
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void init(Application application, Stage appStage) {
        app = application;
        stage = appStage;

        stage.setTitle(STAGE_NAME);
        // TODO: setIcon

        load("/fxml/Login.fxml");

        stage.show();
    }

    public static Controller getController() {
        if (controller == null) {
            controller = new Controller();
        }

        return controller;
    }
}
