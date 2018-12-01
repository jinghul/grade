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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import twelve.team.Auth;
import twelve.team.models.Teacher;

public class LoginController implements Initializable, EventHandler<ActionEvent>, ChangeListener<String> {
    public static final String INCORRECT_CREDENTIALS = "Incorrect username or password.";
    public static final String MISSING_PASSWORD = "Please input a password.";
    public static final String MISSING_USERNAME = "Please input a username.";

    @FXML
    private HBox root;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater( () -> root.requestFocus() );
        btn_login.setOnAction(this);
        btn_register.setOnAction(this);

        tf_username.textProperty().addListener(this);
        tf_password.textProperty().addListener(this);
        tf_password.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == btn_login || event.getSource() == tf_password) {
            authenticate();
        } else if (event.getSource() == btn_register) {
            Controller.getController().navigateToRegistration();
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
            Controller.getController().navigateToSemesterSelection(teacher);
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
