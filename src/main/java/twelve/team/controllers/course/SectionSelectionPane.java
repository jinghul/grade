package twelve.team.controllers.course;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.controllers.MainPane;
import twelve.team.controllers.common.PlusButton;
import twelve.team.controllers.common.TileButton;
import twelve.team.models.Course;
import twelve.team.models.Section;
import twelve.team.models.Semester;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SectionSelectionPane extends VBox implements Initializable {
    public static final String SECTION_SELECTION_FXML_PATH = "course/SectionSelectionPane.fxml";
    public static final String THEME_COLOR = "#BDEDE0";

    @FXML
    private TilePane tilePane;

    @FXML
    private Button btn_back;

    public SectionSelectionPane() {
        Loader.load(SECTION_SELECTION_FXML_PATH, this);
    }

    private Course course;
    private ArrayList<Section> sections;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }

    public void load(Course course) {
        this.course = course;
        sections = course.getSections();

        for (int i = 0; i < sections.size(); i++) {
            addSection(sections.get(i), -1);
        }

        if(Router.getRouter().canGoBack()) {
            btn_back.setVisible(true);
            btn_back.setOnAction(e -> {
                Router.getRouter().goBack();
            });
        } else {
            btn_back.setVisible(false);
        }

        PlusButton plusButton = new PlusButton();
        plusButton.init(e -> {
            // Add semester pane
            createSection(new SectionEditPane().load(course));

        }, THEME_COLOR);

        tilePane.getChildren().add(plusButton);
    }

    private void createSection(Section section) {
        if (section == null) {
            return;
        }

        addSection(section, sections.indexOf(section));
    }

    private void addSection(Section section, int index) {
        if (section == null) {
            return;
        }

        // TODO: SORTING

        TileButton tile = new TileButton();
        tile.init(section.getSectionCode(), getNumStudentsDisplay(section.getNumStudents()),
                e -> {
                    // Click on semester button
                    SectionPane sectionPane = new SectionPane();
                    sectionPane.load(course, section);
                    Animator.fadeOut(this, ev -> {
                        Router.getRouter().addPane(sectionPane, false);
                    });
                }, e -> {
                    // Click on semester edit button
                    e.consume();
                    Section updatedSection = new SectionEditPane().load(course, section);
                    tile.update(updatedSection.getSectionCode(), getNumStudentsDisplay(updatedSection.getNumStudents()));
                },
                e -> {
                    e.consume();
                    twelve.team.utils.Dialog.showAlertDialog("Delete Section", String.format("Are you sure you want to delete Section: %s?", section.getSectionCode()), item -> {
                        if (item) {
                            deleteSection(tile, section);
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

    private void deleteSection(TileButton tile, Section section) {
        // TODO: Convert tile to index
        course.deleteSection(section);

        Animator.fadeOut(tile, e -> tilePane.getChildren().remove(tile));
    }

    public static String getNumStudentsDisplay(int numStudents) {
        return numStudents + " students";
    }
}
