package twelve.team.controllers.course;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.controllers.MainPane;
import twelve.team.models.Assignment;
import twelve.team.models.Category;
import twelve.team.models.Course;
import twelve.team.models.Semester;
import twelve.team.models.graphic.CategoryWeight;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CoursePane extends VBox implements Initializable {
    public static final String COURSE_FXML_PATH = "course/CoursePane.fxml";
    public static final String THEME_COLOR = "#9b2915";

    @FXML
    private Text txt_description, txt_courseName;

    @FXML
    private TitledPane pane_description, pane_syllabus;

    @FXML
    private JFXTreeTableView<CategoryWeight> table_syllabus;

    @FXML
    private JFXToggleButton toggle_degree;

    @FXML
    private JFXButton btn_gradebook, btn_sections, btn_students, btn_assignments, btn_addAssignment, btn_addCategory;

    private Semester semester;
    private Course course;
    private int degree;

    public CoursePane() {
        Loader.load(COURSE_FXML_PATH, this);
    }

    public void load(Semester semester, Course course) {
        MainPane.teacher.setCurrentCourse(course);

        this.semester = semester;
        this.course = course;

        // Load the corresponding data.
        course.load();

        txt_courseName.setText(course.getName());
        txt_description.setText(course.getCourseDescription());

        degree = 0; // Undergraduate
        toggle_degree.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                degree = degree == 0 ? 1 : 0;
                toggle_degree.setText(degree == 0 ? "Undergraduate" : "Graduate");
            }
        });

        pane_syllabus.setExpanded(true);

        JFXTreeTableColumn<CategoryWeight, String> name = new JFXTreeTableColumn<>("Name");
        name.setPrefWidth(200);
        name.setCellValueFactory(param -> param.getValue().getValue().getName());
        name.setCellFactory(param -> new TextFieldTreeTableCell<>());
        name.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        name.setOnEditCommit(event -> {
            final CategoryWeight categoryWeight = event.getRowValue().getValue();
            categoryWeight.setName(event.getNewValue());
        });

        JFXTreeTableColumn<CategoryWeight, String> weight = new JFXTreeTableColumn<>("Weight");
        name.setPrefWidth(100);
        weight.setCellValueFactory(param -> {
            CategoryWeight categoryWeight = param.getValue().getValue();
            return degree == 0 ? categoryWeight.getWeightUG() : categoryWeight.getWeightGR();
        });
        weight.setCellFactory(param -> new TextFieldTreeTableCell<>());
        weight.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        weight.setOnEditCommit(event -> {
            final CategoryWeight categoryWeight = event.getRowValue().getValue();
            double newWeight;
            try {
                newWeight = Double.valueOf(event.getNewValue());
                if (degree == 0) {
                    categoryWeight.setWeightUG(newWeight);
                } else {
                    categoryWeight.setWeightGR(newWeight);
                }
            } catch(NumberFormatException e) {
                return;
            }
        });

        ArrayList<TreeItem<CategoryWeight>> treeCategories = new ArrayList<>();
        for (Category category : course.getCategories()) {
            if (category.isDefault()) {
                for (Assignment assignment : category.getAssignments()) {
                    treeCategories.add(new TreeItem<>(new CategoryWeight(assignment, false)));
                }
                continue;
            }

            TreeItem<CategoryWeight> categoryItem = new TreeItem<>(new CategoryWeight(category, true));
            for (Assignment assignment : category.getAssignments()) {
                categoryItem.getChildren().add(new TreeItem<>(new CategoryWeight(assignment, false)));
            }

            treeCategories.add(categoryItem);
        }

        final TreeItem<CategoryWeight> root = new TreeItem<>(new CategoryWeight("Root", 0, 0));
        root.getChildren().addAll(treeCategories);

        table_syllabus.setRoot(root);
        table_syllabus.getColumns().setAll(name, weight);
        table_syllabus.setShowRoot(false);

        btn_addAssignment.setOnAction(e -> {
            HashMap<Category, Assignment> result = new AssignmentEditPane().load(course);
            result.keySet().forEach(category -> {
                for (TreeItem<CategoryWeight> categoryWeightItem : table_syllabus.getRoot().getChildren()) {
                    if (categoryWeightItem.getValue().getCategory().equals(category)) {
                        categoryWeightItem.getChildren().add(new TreeItem<>(new CategoryWeight(result.get(category), false)));
                        return;
                    }
                }
            });
        });

        btn_addCategory.setOnAction(e -> {
            Category result = new CategoryEditPane().load(course);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }
}
