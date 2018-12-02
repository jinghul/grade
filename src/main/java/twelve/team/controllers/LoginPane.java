package twelve.team.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.utils.Animator;
import twelve.team.Auth;
import twelve.team.models.Teacher;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginPane implements Initializable, EventHandler<ActionEvent>, ChangeListener<String> {
    public static final String LOGIN_FXML_PATH = "LoginPane.fxml";
    public static final String INCORRECT_CREDENTIALS = "Incorrect username or password.";
    public static final String MISSING_PASSWORD = "Please input a password.";
    public static final String MISSING_USERNAME = "Please input a username.";

    @FXML
    private GridPane root;

    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Text txt_error;

    @FXML
    private Button btn_login;

    @FXML
    private Button btn_register;

    // TODO: StackPane transition to registration and back.

    public static void load() {
        Loader.loadToScene(LOGIN_FXML_PATH);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater( () -> root.requestFocus() );
        btn_login.setOnAction(this);
        btn_register.setOnAction(this);

        tf_username.textProperty().addListener(this);
        tf_password.textProperty().addListener(this);
        tf_password.setOnAction(this);

        Animator.fadeIn(root, null);
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == btn_login || event.getSource() == tf_password) {
            authenticate();
        } else if (event.getSource() == btn_register) {
            Animator.fadeOut(root, (e) -> RegisterPane.load());
        }
    }

    private void authenticate() {
        String username = tf_username.getText();
        String password = tf_password.getText();

        if (username.equals("")) {
            displayError(MISSING_USERNAME);
            return;
        } else if (password.equals("")) {
            displayError(MISSING_PASSWORD);
            return;
        }

        if (Auth.authenticate(username, password)) {
            // move to semesters screen
            Teacher teacher = new Teacher(username);
            Animator.fadeOut(root, (e) -> MainPane.load(teacher));
        } else {
            // Display incorrect entry
            displayError(INCORRECT_CREDENTIALS);
        }
    }

    private void displayError(String error) {
        txt_error.setText(error);
        txt_error.setVisible(true);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (txt_error.isVisible()) {
            txt_error.setVisible(false);
        }
    }
}
