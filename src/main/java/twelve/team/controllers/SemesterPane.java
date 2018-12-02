package twelve.team.controllers;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import twelve.team.Loader;
import twelve.team.models.Teacher;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ResourceBundle;

public class SemesterPane extends GridPane implements Initializable {
    public static final String SEMESTER_FXML_PATH = "SemesterPane.fxml";

    public SemesterPane() {
        Loader.load(SEMESTER_FXML_PATH, this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }

    public void loadFromTeacher(Teacher teacher) {
        teacher.getSemesters();
    }
}
