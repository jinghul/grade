package twelve.team.controllers.tiles;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import twelve.team.Loader;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ResourceBundle;

public class PlusButton extends Button implements Initializable {
    public static final String PLUS_FXML_PATH = "PlusButton.fxml";


    public PlusButton() {
        Loader.load(PLUS_FXML_PATH, this);
    }

    public void init(EventHandler<ActionEvent> handler, String backgroundStyle) {
        setOnAction(handler);
        setBackgroundColor(backgroundStyle);
    }

    public void setBackgroundColor(String backgroundStyle) {
        StringBuilder style = new StringBuilder();
        style.append("-fx-background-color: ");
        style.append(backgroundStyle);
        setStyle(style.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }
}
