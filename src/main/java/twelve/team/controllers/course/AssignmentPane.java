package twelve.team.controllers.course;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.controllers.MainPane;
import twelve.team.controllers.semester.SemesterPane;
import twelve.team.models.*;
import twelve.team.models.table.AssignmentModel;
import twelve.team.models.table.StudentModel;
import twelve.team.utils.Animator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AssignmentPane extends AnchorPane implements Initializable, EventHandler<ActionEvent> {
    public static final String ASSIGNMENT_FXML_PATH = "course/AssignmentPane.fxml";

    @FXML
    private AnchorPane root;

    @FXML
    private TextField tf_search;

    @FXML
    private Button btn_addAssignment;

    @FXML
    private JFXTreeTableView table_assignments;

    private Course course;
    private Section section;
    private Student student;

    public AssignmentPane() {
        Loader.load(ASSIGNMENT_FXML_PATH, this);
    }

    private void reset() {
        course = null;
        section = null;
        student = null;
        if (table_assignments.getRoot() != null) {
            table_assignments.getRoot().getChildren().clear();
        }
    }

    public void load(Course course, Student student) {
        this.student = student;
        this.course = course;
        ArrayList<TreeItem<AssignmentModel>> assignmentModels = new ArrayList<>();
        for (Category category : course.getCategories()) {
            for (Assignment assignment : category.getAssignments()) {
                TreeItem<AssignmentModel> assignmentModel = new TreeItem<>(new AssignmentModel(assignment, category, student));
                assignmentModels.add(assignmentModel);
            }
        }
        loadTable(assignmentModels);
    }

    public void load(Course course, Section section) {
        this.section = section;
        this.course = course;

        ArrayList<TreeItem<AssignmentModel>> assignmentModels = new ArrayList<>();
        for (Category category : course.getCategories()) {
            for (Assignment assignment : category.getAssignments()) {
                TreeItem<AssignmentModel> assignmentModel = new TreeItem<>(new AssignmentModel(assignment, category, section));
                assignmentModels.add(assignmentModel);
            }
        }

        loadTable(assignmentModels);
    }

    public void load(Course course) {
        this.course = course;
        ArrayList<TreeItem<AssignmentModel>> assignmentModels = new ArrayList<>();
        for (Category category : course.getCategories()) {
            for (Assignment assignment : category.getAssignments()) {
                TreeItem<AssignmentModel> assignmentModel = new TreeItem<>(new AssignmentModel(assignment, category, course));
                assignmentModels.add(assignmentModel);
            }
        }
        loadTable(assignmentModels);
    }

    private void loadTable(ArrayList<TreeItem<AssignmentModel>> assignmentModels) {

        JFXTreeTableColumn<AssignmentModel, String> name = new JFXTreeTableColumn<>("Name");
        name.setPrefWidth(200);
        name.setCellValueFactory(param -> param.getValue().getValue().getAssignmentName());

        JFXTreeTableColumn<AssignmentModel, String> grade = new JFXTreeTableColumn<>("Grade");
        grade.setPrefWidth(200);
        grade.setCellValueFactory(param -> param.getValue().getValue().getGradeScore());

        System.out.println("STUDENT NULL ? " + String.valueOf(student == null));
        if (student != null) {
            table_assignments.setEditable(true);
            grade.setCellFactory(param -> new TextFieldTreeTableCell<>());
            grade.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
            grade.setOnEditCommit(event -> {
                final AssignmentModel assignmentModel = event.getRowValue().getValue();
                double newGrade;
                try {
                    newGrade = Double.valueOf(event.getNewValue());
                    assignmentModel.setGrade(newGrade);
                } catch(NumberFormatException e) {
                    return;
                }
            });
        }

        JFXTreeTableColumn<AssignmentModel, Number> totalPoints = new JFXTreeTableColumn<>("TotalPoints");
        totalPoints.setPrefWidth(200);
        totalPoints.setCellValueFactory(param -> param.getValue().getValue().getTotalPoints());

        JFXTreeTableColumn<AssignmentModel, Boolean> extracredit = new JFXTreeTableColumn<>("Extracredit");
        extracredit.setPrefWidth(200);
        extracredit.setCellValueFactory(param -> param.getValue().getValue().getExtracredit());

        JFXTreeTableColumn<AssignmentModel, String> comments = new JFXTreeTableColumn<>("Comment");
        comments.setPrefWidth(200);
        comments.setCellValueFactory(param -> param.getValue().getValue().getComment());

        final TreeItem<AssignmentModel> root = new TreeItem<>(new AssignmentModel(0, "ID", 0, false, "comment"));
        final boolean b = root.getChildren().addAll(assignmentModels);

        table_assignments.setRoot(root);
        table_assignments.getColumns().setAll(name, grade, totalPoints, extracredit, comments);

        if (student == null) {
            table_assignments.setRowFactory( tv -> {
                TreeTableRow<AssignmentModel> row = new TreeTableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 3 && (! row.isEmpty()) ) {
                        AssignmentModel rowData = row.getItem();
                        AssignmentSummaryPane assignmentSummaryPane = new AssignmentSummaryPane();
                        if (section != null) {
                            assignmentSummaryPane.load(section, rowData.getAssignment());
                        } else {
                            assignmentSummaryPane.load(course, rowData.getAssignment());
                        }

                        Router.getRouter().addPane(assignmentSummaryPane, false);
                    }
                });
                return row ;
            });
        }


        table_assignments.setShowRoot(false);
        btn_addAssignment.setOnAction(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void handle(ActionEvent event) {
        HashMap<Category, Assignment> result = (new AssignmentEditPane()).load(course);

        if (result == null) {
            return;
        }

        result.keySet().forEach(category -> {
            Assignment assignment = result.get(category);
            if (student != null) {
                table_assignments.getRoot().getChildren().add(new TreeItem<>(new AssignmentModel(assignment, category, student)));
            } else if (section != null) {
                table_assignments.getRoot().getChildren().add(new TreeItem<>(new AssignmentModel(assignment, category, section)));
            } else {
                table_assignments.getRoot().getChildren().add(new TreeItem<>(new AssignmentModel(assignment, category, course)));
            }
        });
    }
}
