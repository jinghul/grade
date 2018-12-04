package twelve.team.controllers.login;

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
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.utils.Animator;
import twelve.team.Database;
import twelve.team.utils.StringUtil;
import twelve.team.models.Teacher;

import javax.xml.crypto.Data;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class RegisterPane implements Initializable, EventHandler<ActionEvent>, ChangeListener<String> {
    public static final String REGISTER_FXML_PATH = "login/RegisterPane.fxml";
    private static final String INVALID_INPUT = "Invalid input: All fields are required.";
    private static final String DATABASE_ERROR = "Error saving to MySql database.";
    private static final String CREATION_SUCCESS = "User successfully created! Return to login.";
    private static final String ALREADY_EXISTS = "Username is taken. Please try again.";
    @FXML
    private GridPane root;

    @FXML
    private TextField tf_name;

    @FXML
    private TextField tf_username;

    @FXML
    private PasswordField tf_password;

    @FXML
    private Button btn_confirm;

    @FXML
    private Button btn_cancel;

    @FXML
    private Text txt_error;

    public static void load() {
        Loader.loadToScene(REGISTER_FXML_PATH);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater( () -> root.requestFocus() );

        // Set handlers for the buttons.
        btn_confirm.setOnAction(this);
        btn_cancel.setOnAction(this);

        // Get rid of error when input starts again.
        tf_username.textProperty().addListener(this);
        tf_password.textProperty().addListener(this);
        tf_name.textProperty().addListener(this);

        Animator.fadeIn(root, null);
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == btn_confirm) {
            // Insert new teacher
            registerUser();

        } else if (event.getSource() == btn_cancel) {
            Animator.fadeOut(root, (e) -> returnToLogin());
        }
    }

    private void registerUser() {
        String name = tf_name.getText();
        String username = tf_username.getText();
        String password = tf_password.getText();

        if (StringUtil.isNullOrWhitespace(name) || StringUtil.isNullOrWhitespace(username) || StringUtil.isNullOrWhitespace(password)) {
            // Display incorrect input;
            displayError(INVALID_INPUT);
            return;
        }

        String query = "select * from teacher where username = '" + username + "'";
        try (Statement statement = Database.getDatabase().getStatement()){
            try (ResultSet exists = statement.executeQuery(query)) {
                if (exists.next()) {
                    displayError(ALREADY_EXISTS);
                    return;
                }

                Teacher.create(username, password, name);

                displaySuccess();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayError(DATABASE_ERROR);
        }

    }

    private void returnToLogin() {
        LoginPane.load();
    }

    private void displayError(String error) {
        txt_error.setText(error);
        txt_error.setVisible(true);
    }

    private void displaySuccess() {
        txt_error.setFill(Paint.valueOf("#4747d1"));
        txt_error.setText(CREATION_SUCCESS);
        txt_error.setVisible(true);

        // Disable confirm
        btn_confirm.setDisable(true);

        // Lock the input
        tf_name.setEditable(false);
        tf_password.setEditable(false);
        tf_username.setEditable(false);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (txt_error.isVisible()) {
            txt_error.setVisible(false);
        }
    }
}
