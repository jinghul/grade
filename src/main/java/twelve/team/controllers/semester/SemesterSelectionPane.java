package twelve.team.controllers.semester;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.controllers.MainPane;
import twelve.team.controllers.common.PlusButton;
import twelve.team.controllers.common.TileButton;
import twelve.team.models.Semester;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SemesterSelectionPane extends VBox implements Initializable {
    public static final String SEMESTER_SELECTION_PANE_FXML = "semester/SemesterSelectionPane.fxml";
    public static final String THEME_COLOR = "#957FEF";

    @FXML
    private TilePane tilePane;

    @FXML
    private Button backButton;

    public SemesterSelectionPane() {
        Loader.load(SEMESTER_SELECTION_PANE_FXML, this);
    }
    ArrayList<Semester> semesters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }

    public void load() {
        semesters = MainPane.teacher.getSemesters();

        for (int i = 0; i < semesters.size(); i++) {
            addSemester(semesters.get(i), -1);
        }

        PlusButton plusButton = new PlusButton();
        plusButton.init(e -> {
            // Add semester pane
            createSemester(new SemesterEditPane().load());

        }, THEME_COLOR);

        tilePane.getChildren().add(plusButton);
    }

    public void reset() {
        tilePane.getChildren().clear();
        load();
    }

    private void createSemester(Semester semester) {
        if (semester == null) {
            return;
        }

        addSemester(semester, semesters.indexOf(semester));
    }

    private void addSemester(Semester semester, int index) {
        if (semester == null) {
            return;
        }

        // TODO: SORTING

        TileButton tile = new TileButton();
        tile.init(semester.getName(), getNumCoursesDisplay(semester.getNumCourses()),
                e -> {
                    // Click on semester button
                    SemesterPane semesterPane = new SemesterPane();
                    semesterPane.load(semester);
                    Animator.fadeOut(this, ev -> {
                        Router.getRouter().addPane(semesterPane, false);
                    });
                }, e -> {
                    // Click on semester edit button
                    e.consume();
                    Semester updatedSemester = new SemesterEditPane().load(semester);
                    tile.update(updatedSemester.getName(), getNumCoursesDisplay(updatedSemester.getNumCourses()));
                },
                e -> {
                    e.consume();
                    twelve.team.utils.Dialog.showAlertDialog("Delete Semester", String.format("Are you sure you want to delete Semester: %s?", semester.getName()), item -> {
                        if (item) {
                            deleteSemester(tile, semester);
                        }
                    });
                }, THEME_COLOR);

        if (index == -1) {
            tilePane.getChildren().add(tile);
        } else {
            tilePane.getChildren().add(index, tile);
        }
        Animator.fadeIn(tile, null);
    }

    private void deleteSemester(TileButton tile, Semester semester) {
        // TODO: Convert tile to index
        MainPane.teacher.deleteSemester(semester);

        Animator.fadeOut(tile, e -> tilePane.getChildren().remove(tile));
    }

    public static String getNumCoursesDisplay(int numCourses) {
        return numCourses + " courses";
    }
}
