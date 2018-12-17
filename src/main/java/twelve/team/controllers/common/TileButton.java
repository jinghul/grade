package twelve.team.controllers.common;

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
    private Text txt_title;

    @FXML
    private Text txt_subtitle;

    @FXML
    private Button btn_edit;

    @FXML
    private Button btn_delete;


    public TileButton() {
        Loader.load(TILE_FXML_PATH, this);
    }

    public void init(String title, String subtitle, EventHandler<ActionEvent> buttonHandler, EventHandler<ActionEvent> editHandler, EventHandler<ActionEvent> deleteHandler, String backgroundStyle) {

        // TODO: Font sizing/ wrapping

        System.out.println(String.format("Creating tile: %s %s", title, subtitle));

        update(title, subtitle);
        setBackgroundColor(backgroundStyle);
        setButtonClick(buttonHandler);
        setEditClick(editHandler);
        setDeleteClick(deleteHandler);
    }

    public void update(String title, String subtitle) {
        setTitle(title);
        setSubtitle(subtitle);
    }

    public void setBackgroundColor(String backgroundStyle) {
        String style = "-fx-background-color: " +
                backgroundStyle;
        setStyle(style);
    }

    public void setTitle(String title) {
        txt_title.setText(title);
    }

    public void setSubtitle(String subtitle) {
        txt_subtitle.setText(subtitle);
    }

    public void setButtonClick(EventHandler<ActionEvent> handler) {
        setOnAction(handler);
    }

    public void setEditClick(EventHandler<ActionEvent> handler) {
        btn_edit.setOnAction(handler);
    }

    public void setDeleteClick(EventHandler<ActionEvent> handler) {
        btn_delete.setOnAction(handler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
