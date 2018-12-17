package twelve.team.controllers.course;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.controllers.MainPane;
import twelve.team.models.*;
import twelve.team.models.table.CategoryWeight;
import twelve.team.utils.Animator;
import twelve.team.utils.Dialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CoursePane extends VBox implements Initializable, EventHandler<ActionEvent> {
    public static final String COURSE_FXML_PATH = "course/CoursePane.fxml";
    public static final String THEME_COLOR = "#9b2915";

    @FXML
    private Text txt_description, txt_courseName;

    @FXML
    private Accordion accordion_course;

    @FXML
    private AssignmentPane assignment_pane;

    @FXML
    private StudentPane student_pane;

    @FXML
    private TitledPane pane_description, pane_syllabus;

    @FXML
    private JFXTreeTableView<CategoryWeight> table_syllabus;

    @FXML
    private JFXToggleButton toggle_degree;

    @FXML
    private JFXButton btn_gradebook, btn_sections, btn_students, btn_assignments, btn_addAssignment, btn_addCategory, btn_back;

    private Semester semester;
    private Course course;
    private int degree;
    private Node current;

    public CoursePane() {
        Loader.load(COURSE_FXML_PATH, this);
    }

    public void reset() {
        table_syllabus.refresh();
    }

    public void load(Semester semester, Course course) {
        MainPane.teacher.setCurrentCourse(course);

        current = accordion_course;

        this.semester = semester;
        this.course = course;

        btn_gradebook.setOnAction(this);
        btn_sections.setOnAction(this);
        btn_students.setOnAction(this);
        btn_assignments.setOnAction(this);

        if(Router.getRouter().canGoBack()) {
            btn_back.setVisible(true);
            btn_back.setOnAction(e -> {
                Router.getRouter().goBack();
            });
        } else {
            btn_back.setVisible(false);
        }

        // Load the corresponding data.
        course.load();

        txt_courseName.setText(course.getName());
        txt_description.setText(course.getCourseDescription());

        degree = 0; // Undergraduate
        toggle_degree.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                degree = 1;
                toggle_degree.setText("Graduate");
            } else {
                degree = 0;
                toggle_degree.setText("Undergraduate");
            }
            table_syllabus.refresh();
        });

        accordion_course.setExpandedPane(pane_syllabus);

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
        weight.setPrefWidth(100);
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

            if (result == null) {
                return;
            }

            result.keySet().forEach(category -> {
                Assignment assignment = result.get(category);

                Dialog.promptWeightRecalc(res -> {
                    if (res) {
                        category.recalculateWeights(assignment, 0);
                        category.recalculateWeights(assignment, 1);
                    }
                });

                if (category.isDefault()) {
                    table_syllabus.getRoot().getChildren().add(new TreeItem<>(new CategoryWeight(result.get(category), false)));
                    return;
                }

                for (TreeItem<CategoryWeight> categoryWeightItem : table_syllabus.getRoot().getChildren()) {
                    if (categoryWeightItem.getValue().getCategory() != null && categoryWeightItem.getValue().getCategory().equals(category)) {
                        categoryWeightItem.getChildren().add(new TreeItem<>(new CategoryWeight(result.get(category), false)));
                        return;
                    }
                }
            });
        });

        btn_addCategory.setOnAction(e -> {
            Category result = new CategoryEditPane().load(course);

            if (result == null) {
                return;
            }

            Dialog.promptWeightRecalc(res -> {
                if (res) {
                    course.recalculateWeights(result, 0);
                    course.recalculateWeights(result, 1);
                }
            });


            table_syllabus.getRoot().getChildren().add(new TreeItem<>(new CategoryWeight(result, true)));
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(this, null);
    }

    @Override
    public void handle(ActionEvent event) {
        event.consume();
        if (event.getSource() == btn_gradebook) {

        } else if (event.getSource() == btn_sections) {
            SectionSelectionPane sectionSelectionPane = new SectionSelectionPane();
            sectionSelectionPane.load(course);
            Animator.fadeOut(this, ev -> {
                Router.getRouter().addPane(sectionSelectionPane, false);
            });
        } else if (event.getSource() == btn_assignments) {
            if (current == assignment_pane) {
                return;
            }
            assignment_pane.toFront();
            assignment_pane.load(course);
        } else if (event.getSource() == btn_students) {
            if (current == student_pane) {
                return;
            }
            student_pane.toFront();
            student_pane.load(course);
        }
    }
}
