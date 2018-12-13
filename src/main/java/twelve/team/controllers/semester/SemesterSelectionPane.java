package twelve.team.controllers.semester;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import twelve.team.Loader;
import twelve.team.controllers.MainPane;
import twelve.team.controllers.tiles.PlusButton;
import twelve.team.controllers.tiles.TileButton;
import twelve.team.models.Semester;
import twelve.team.models.Teacher;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SemesterSelectionPane extends VBox implements Initializable {
    public static final String SELECTION_FXML_PATH = "semester/SemesterSelectionPane.fxml";
    public static final String THEME_COLOR = "#c2c2f0";

    @FXML
    private TilePane tilePane;

    public SemesterSelectionPane() {
        Loader.load(SELECTION_FXML_PATH, this);
    }
    ArrayList<Semester> semesters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }

    public void load() {
        semesters = MainPane.teacher.getSemesters();

        for (int i = 0; i < semesters.size(); i++) {
            semesters.get(i).init();
            addSemester(semesters.get(i), -1);
        }

        PlusButton plusButton = new PlusButton();
        plusButton.init(e -> {
            // Add semester pane
            createSemester(new SemesterEditPane().load());

        }, THEME_COLOR);

        tilePane.getChildren().add(plusButton);
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

        TileButton tile = new TileButton();
        tile.init(semester.getName(), getNumCoursesDisplay(semester.getNumCourses()),
                e -> {
                    // Click on semester button
                    new SemesterPane().load(semester);

                }, e -> {
                    // Click on semester edit button
                    Semester updatedSemester = new SemesterEditPane().load(semester);
                    tile.update(updatedSemester.getName(), getNumCoursesDisplay(updatedSemester.getNumCourses()));
                    e.consume();
                },
                e -> {
                    deleteSemester(tile, semester);
                    e.consume();
                }, THEME_COLOR);

        tile.setOpacity(0);
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
