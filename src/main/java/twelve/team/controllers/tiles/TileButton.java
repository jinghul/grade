package twelve.team.controllers.tiles;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import twelve.team.Loader;

import java.net.URL;
import java.util.ResourceBundle;

public class TileButton extends Button implements Initializable {
    public static final String TILE_FXML_PATH = "TileButton.fxml";

    @FXML
    private Button root;

    @FXML
    private Text txt_title;

    @FXML
    private Text txt_subtitle;

    @FXML
    private Button btn_edit;


    public TileButton() {
        Loader.load(TILE_FXML_PATH, this);
    }

    public void init(String title, String subtitle, EventHandler<ActionEvent> buttonHandler, EventHandler<ActionEvent> editHandler) {
        setTitle(title);
        setSubtitle(subtitle);
        setButtonClick(buttonHandler);
        setEditClick(editHandler);
    }

    public void setTitle(String title) {
        txt_title.setText(title);
    }

    public void setSubtitle(String subtitle) {
        txt_subtitle.setText(subtitle);
    }

    public void setButtonClick(EventHandler<ActionEvent> handler) {
        root.setOnAction(handler);
    }

    public void setEditClick(EventHandler<ActionEvent> handler) {
        btn_edit.setOnAction(handler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
