package twelve.team.controllers.course;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import twelve.team.Loader;
import twelve.team.models.Course;
import twelve.team.models.Section;
import twelve.team.models.Semester;
import twelve.team.utils.StringUtil;

public class SectionEditPane extends AnchorPane {
    public static final String SECTION_EDIT_PANE_FXML = "course/SectionEditPane.fxml";
    public static final String EMPTY_FIELD_ERROR = "Please enter the required fields.";

    @FXML
    private AnchorPane root;

    @FXML
    private Text txt_error;

    @FXML
    private TextField tf_sectionCode;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_cancel;

    private Course course;
    private Section section;

    public SectionEditPane() {
        Loader.load(SECTION_EDIT_PANE_FXML, this);
        Platform.runLater( () -> root.requestFocus() );
        tf_sectionCode.textProperty().addListener(observable -> {
            if (txt_error.isVisible()) {
                txt_error.setVisible(false);
            }
        });

        btn_cancel.setOnAction(e -> exit());
        btn_save.setOnAction(e -> save());
    }

    public void exit() {
        getScene().getWindow().hide();
    }

    public void save() {
        if (StringUtil.isNullOrWhitespace(tf_sectionCode.getText())) {
            txt_error.setText(EMPTY_FIELD_ERROR);
            txt_error.setVisible(true);
            return;
        }

        if (section == null) {
            section = course.addSection(tf_sectionCode.getText(), course.getName());
        } else {
            section.update(tf_sectionCode.getText());
        }

        exit();
    }

    public Section load(Course course, Section section) {
        this.section = section;
        tf_sectionCode.setText(section.getSectionCode());

        return load(course);
    }

    public Section load(Course course) {
        this.course = course;

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("\t Add/Edit Course");
        stage.getIcons().add(new Image(Loader.ICON_PATH));

        stage.setScene(new Scene(this));
        stage.showAndWait();
        return section;
    }
}