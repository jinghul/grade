package twelve.team;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import twelve.team.controllers.MainPane;
import twelve.team.controllers.login.LoginPane;
import twelve.team.models.Teacher;

import java.io.IOException;

/**
 * The main loader for transitioning
 * between scenes for the application
 */
public class Loader {
    public static final String APP_NAME = "Grader";
    public static final String ICON_PATH = "/imgs/main_icon.png";
    public static final String FXML_ROOT = "/fxml/";

    private static Stage stage;

    public static FXMLLoader getLoader(String fxml_file) {
        return new FXMLLoader(App.class.getResource(FXML_ROOT + fxml_file));
    }

    public static void init(Stage appStage) {
        /* FXML Rendering - Constant for whole App duration */
        stage = appStage;

        // Initial load of login page.
        loadToScene(LoginPane.LOGIN_FXML_PATH);
//        TODO: Replace this on production

//        MainPane.load(new Teacher("jing"));
        stage.getIcons().add(new Image(ICON_PATH));
        stage.show();
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

    public static Initializable loadToScene(String fxml_path) {
        try {
            System.out.println("Loading: " + FXML_ROOT + fxml_path);

            // Set the new location of the loader
            FXMLLoader fxmlLoader = getLoader(fxml_path);

            if (stage.getScene() != null) {
                stage.getScene().setRoot(fxmlLoader.load());
            } else {
                stage.setScene(new Scene(fxmlLoader.load()));
            }

            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
