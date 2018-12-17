package twelve.team.controllers.course;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.models.*;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ResourceBundle;

public class SectionPane extends VBox implements Initializable, EventHandler<ActionEvent> {
    public static final String SECTION_FXML_PATH = "course/SectionPane.fxml";
    public static final String THEME_COLOR = "#BDEDE0";

    @FXML
    private StackPane pane_stack;

    @FXML
    private Text txt_sectionName;

    @FXML
    private JFXButton btn_gradebook, btn_students, btn_assignments;

    @FXML
    private AnchorPane pane_student, pane_gradebook, pane_assignments;

    private Course course;
    private Section section;

    private AnchorPane current;

    public SectionPane() {
        Loader.load(SECTION_FXML_PATH, this);
    }

    public void load(Course course, Section section) {
        current = pane_student;

        this.course = course;
        this.section = section;

        txt_sectionName.setText(section.getNameWithCourse());

        btn_gradebook.setOnAction(this);
        btn_students.setOnAction(this);
        btn_assignments.setOnAction(this);

        txt_sectionName.setText(course.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }

    @Override
    public void handle(ActionEvent event) {
        event.consume();
        if (event.getSource() == btn_gradebook) {

        } else if (event.getSource() == btn_assignments) {

        } else if (event.getSource() == btn_students) {

        }
    }
}
