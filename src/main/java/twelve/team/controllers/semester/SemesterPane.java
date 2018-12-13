package twelve.team.controllers.semester;

import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import twelve.team.Loader;
import twelve.team.controllers.tiles.TileButton;
import twelve.team.models.Course;
import twelve.team.models.Semester;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SemesterPane extends VBox implements Initializable {
    public static final String SEMESTER_FXML_PATH = "semester/SemesterPane.fxml";
    public static final String THEME_COLOR = "#c2c2f0";

    private Semester semester;

    public SemesterPane() {
        Loader.load(SEMESTER_FXML_PATH, this);
    }

    public void load(Semester semester) {
        this.semester = semester;
        ArrayList<Course> courses = semester.getCourses();

        for (Course course : courses) {
            TileButton tile = new TileButton();

            course.init();
            tile.init(course.getName(), course.getNumStudents() + " students",
                    e -> {
                        // Click on course button
                    },
                    e -> {
                        // Click on course edit button
                    },
                    e -> {
                        // Click on course delete button
                    }, THEME_COLOR);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }
}
