package twelve.team.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import twelve.team.models.Teacher;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The main controller for transitioning
 * between scenes for the application
 */
public class Controller {
    public static final String STAGE_NAME = "Grader";
    public static final String FXML_ROOT = "/fxml/";
    public static final String LOGIN_FXML_PATH = "Login.fxml";
    public static final String REGISTER_FXML_PATH = "Register.fxml";
    public static final String SEMESTER_FXML_PATH = "Semesters.fxml";

    private static Controller controller;

    /* FXML Rendering */
    private Application app;
    private Stage stage;
    private FXMLLoader loader;

    /* Navigation */
    private String current;
    private ArrayList<String> backStack;

    public Controller(Application application, Stage appStage) {
        // TODO: Make singleton instead of static class.
        app = application;
        stage = appStage;
    }

    public static Controller getController() {
        return controller;
    }

    public static void init(Application application, Stage appStage) {
        if (controller == null) {
            controller = new Controller(application, appStage);
            controller.navigateToLogin();
            controller.setStage();
        }
    }

    public void setStage() {
        // TODO: setIcon
        stage.setTitle(STAGE_NAME);
        stage.show();
    }

    private Initializable load(String fxml_path) {
        if (app == null) {
            return null;
        }

        try {

            System.out.println("Next page: " + FXML_ROOT + fxml_path);

            // Set the new location of the loader
            loader = new FXMLLoader(app.getClass().getResource(FXML_ROOT + fxml_path));
            stage.setScene( new Scene(loader.load()));

            // Track current page
            current = fxml_path;

            return loader.getController();
        } catch (IOException e) {
            return null;
        }
    }

    public void navigateToLogin() {
        load(LOGIN_FXML_PATH);
    }

    public void navigateToRegistration() {
        load(REGISTER_FXML_PATH);
    }

    public void navigateToSemesterSelection(Teacher teacher) {
        Semesters semesterController = (Semesters) load(SEMESTER_FXML_PATH);
        semesterController.loadFromTeacher(teacher);
    }

    public void navigateToCourseSelection() {

    }

    public void navigateToCourse() {

    }
}
