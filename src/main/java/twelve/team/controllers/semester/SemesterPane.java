package twelve.team.controllers.semester;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.controllers.course.CourseEditPane;
import twelve.team.controllers.common.PlusButton;
import twelve.team.controllers.common.TileButton;
import twelve.team.controllers.course.CoursePane;
import twelve.team.models.Course;
import twelve.team.models.Semester;
import twelve.team.utils.Animator;
import twelve.team.utils.Dialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SemesterPane extends VBox implements Initializable {
    public static final String SEMESTER_FXML_PATH = "semester/SemesterPane.fxml";
    public static final String THEME_COLOR = "#B43E8F";

    @FXML
    private Text txt_semester;

    @FXML
    private TilePane tilePane;


    private Semester semester;
    ArrayList<Course> courses;

    public SemesterPane() {
        Loader.load(SEMESTER_FXML_PATH, this);
    }

    public void load(Semester semester) {
        this.semester = semester;
        txt_semester.setText(semester.getName());
        courses = semester.getCourses();

        for (int i = 0; i < courses.size(); i++) {
            addCourse(courses.get(i), -1);
        }

        PlusButton plusButton = new PlusButton();
        plusButton.init(e -> {

            // Add course pane
            createCourse(new CourseEditPane().load(semester));

        }, THEME_COLOR);

        tilePane.getChildren().add(plusButton);
    }

    private void createCourse(Course course) {
        if (course == null) {
            return;
        }

        addCourse(course, courses.indexOf(course));
    }

    private void addCourse(Course course, int index) {
        if (course == null) {
            return;
        }

        // TODO: SORTING

        TileButton tile = new TileButton();
        tile.init(course.getName(), getNumSectionsDisplay(course.getNumSections()),
                e -> {
                    // Click on course button
                    CoursePane coursePane = new CoursePane();
                    coursePane.load(semester, course);
                    Animator.fadeOut(this, ev -> {
                        Router.getRouter().addPane(coursePane, false);
                    });

                }, e -> {
                    // Click on course edit button
                    e.consume();
                    Course updatedCourse = new CourseEditPane().load(semester, course);
                    tile.update(updatedCourse.getName(), getNumSectionsDisplay(updatedCourse.getNumSections()));
                },
                e -> {
                    // Click on course delete button
                    e.consume();
                    Dialog.showAlertDialog("Delete Course", String.format("Are you sure you want to delete course: %s?", course.getName()), ev -> {
                        deleteCourse(tile, course);
                    });

                }, THEME_COLOR);

        tile.setOpacity(0);
        if (index == -1) {
            tilePane.getChildren().add(tile);
        } else {
            tilePane.getChildren().add(index, tile);
        }
        Animator.fadeIn(tile, null);
    }

    private String getNumSectionsDisplay(int numSections) {
        return String.format("%d sections", numSections);
    }

    private void deleteCourse(TileButton tile, Course course) {
        // TODO: Convert tile to index

        semester.deleteCourse(course);

        Animator.fadeOut(tile, e -> tilePane.getChildren().remove(tile));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }
}
