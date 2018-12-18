package twelve.team.controllers.course;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.models.Assignment;
import twelve.team.models.Course;
import twelve.team.models.Section;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ResourceBundle;

public class AssignmentSummaryPane extends VBox implements Initializable {
    public static final String ASSIGNMENT_PANE_FXML = "course/AssignmentSummaryPane.fxml";

    @FXML
    private StudentPane student_pane;

    @FXML
    private Button btn_back;

    public AssignmentSummaryPane() {
        Loader.load(ASSIGNMENT_PANE_FXML, this);
        if(Router.getRouter().canGoBack()) {
            btn_back.setVisible(true);
            btn_back.setOnAction(e -> {
                Router.getRouter().goBack();
            });
        } else {
            btn_back.setVisible(false);
        }
    }

    public void load(Section section, Assignment assignment) {
        student_pane.load(section, assignment);
    }

    public void load(Course course, Assignment assignment) {
        student_pane.load(course, assignment);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }
}
