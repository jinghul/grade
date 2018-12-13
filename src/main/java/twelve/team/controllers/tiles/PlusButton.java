package twelve.team.controllers.tiles;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import twelve.team.Loader;

import java.net.URL;
import java.util.ResourceBundle;

public class PlusButton extends Button implements Initializable {
    public static final String PLUS_FXML_PATH = "PlusButton.fxml";


    public PlusButton() {
        Loader.load(PLUS_FXML_PATH, this);
    }

    public void init(EventHandler<ActionEvent> handler) {
        setOnAction(handler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}