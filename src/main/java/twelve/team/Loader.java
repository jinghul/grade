package twelve.team;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import twelve.team.controllers.LoginPane;

import java.io.IOException;

/**
 * The main loader for transitioning
 * between scenes for the application
 */
public class Loader {
    public static final String APP_NAME = "Grader";
    public static final String ICON_PATH = "/imgs/main_icon.png";
    public static final String FXML_ROOT = "/fxml/";

    /* FXML Rendering - Constant for whole App duration */
    private static Application app;
    private static Stage stage;

    public static FXMLLoader getLoader(String fxml_file) {
        return new FXMLLoader(app.getClass().getResource(FXML_ROOT + fxml_file));
    }

    public static void load(String fxml_file, Node pane) {
        FXMLLoader loader = getLoader(fxml_file);
        loader.setRoot(pane);
        loader.setController(pane);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init(Application application, Stage appStage) {
            app = application;
            stage = appStage;

            // Initial load of login page.
            loadToScene(LoginPane.LOGIN_FXML_PATH);
            stage.getIcons().add(new Image(ICON_PATH));
            stage.show();
    }


    public static Node loadFile(String fxml_path) {
        if (app == null) {
            return null;
        }

        try {
            System.out.println("Loading: " + FXML_ROOT + fxml_path);

            // Set the new location of the loader
            FXMLLoader fxmlLoader = new FXMLLoader(app.getClass().getResource(FXML_ROOT + fxml_path));
            return fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Initializable loadToScene(String fxml_path) {
        if (app == null) {
            return null;
        }

        try {
            System.out.println("Loading: " + FXML_ROOT + fxml_path);

            // Set the new location of the loader
            FXMLLoader fxmlLoader = new FXMLLoader(app.getClass().getResource(FXML_ROOT + fxml_path));

            if (stage.getScene() != null) {
                stage.getScene().setRoot(fxmlLoader.load());
            } else {
                stage.setScene(new Scene(fxmlLoader.load()));
            }

            return fxmlLoader.getController();
        } catch (IOException e) {
            return null;
        }
    }
}
