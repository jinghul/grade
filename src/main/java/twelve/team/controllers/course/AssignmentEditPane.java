package twelve.team.controllers.course;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import twelve.team.Loader;
import twelve.team.models.Assignment;
import twelve.team.models.Category;
import twelve.team.models.Course;
import twelve.team.utils.StringUtil;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

public class AssignmentEditPane extends AnchorPane {
    public static final String ASSIGNMENT_EDIT_PANE_FXML = "course/AssignmentEditPane.fxml";
    public static final String EMPTY_FIELD_ERROR = "Please enter the required fields.";
    public static final String DATE_ERROR = "Please enter a valid time span.";
    private static final String POINTS_FORMAT_ERROR = "Please enter a valid number for points or decimal for weights.";
    private static final String WEIGHT_FORMAT_ERROR = "Please enter a number less than or equal to '1.0' for a weight.";

    @FXML
    private AnchorPane root;

    @FXML
    private StackPane pane_stack;

    @FXML
    private Text txt_error;

    @FXML
    private TextField tf_name, tf_totalpoints, tf_ugweight, tf_grweight;

    @FXML
    private ChoiceBox cb_category;

    @FXML
    private CheckBox cb_optional, cb_extracredit;

    @FXML
    private DatePicker dp_start, dp_end;

    @FXML
    private Button btn_save, btn_cancel, btn_comment;

    private Course course;
    private Category category;
    private Assignment assignment;
    private String comment;

    public AssignmentEditPane() {
        Loader.load(ASSIGNMENT_EDIT_PANE_FXML, this);
        Platform.runLater( () -> root.requestFocus() );

        InvalidationListener invalidationListener = observable -> {
            if (txt_error.isVisible()) {
                txt_error.setVisible(false);
            }
        };

        tf_name.textProperty().addListener(invalidationListener);
        tf_totalpoints.textProperty().addListener(invalidationListener);
        tf_ugweight.textProperty().addListener(invalidationListener);
        tf_grweight.textProperty().addListener(invalidationListener);

        dp_start.chronologyProperty().addListener(invalidationListener);
        dp_end.chronologyProperty().addListener(invalidationListener);


        btn_comment.setOnAction(e -> {
           twelve.team.utils.Dialog.showCommentDialog(assignment == null ? "Assignment" : assignment.getName(), comment, pane_stack, item -> {
               this.comment = item;
           });
        });
        btn_cancel.setOnAction(e -> exit());
        btn_save.setOnAction(e -> save());
    }

    public void exit() {
        getScene().getWindow().hide();
    }

    public void save() {
        if (StringUtil.isNullOrWhitespace(tf_name.getText(),tf_totalpoints.getText(), tf_ugweight.getText(), tf_grweight.getText())) {
            txt_error.setText(EMPTY_FIELD_ERROR);
            txt_error.setVisible(true);
            return;
        }

        int totalPoints;
        double weightUG;
        double weightGR;
        try {
            totalPoints = Integer.valueOf(tf_totalpoints.getText());
            weightUG = Double.valueOf(tf_ugweight.getText());
            weightGR = Double.valueOf(tf_grweight.getText());

            if (weightUG > 1 || weightGR > 1) {
                txt_error.setText(WEIGHT_FORMAT_ERROR);
                txt_error.setVisible(true);
                return;
            }

        } catch (NumberFormatException e) {
            txt_error.setText(POINTS_FORMAT_ERROR);
            txt_error.setVisible(true);
            return;
        }

        if (dp_start.getValue() == null || dp_end.getValue() == null || dp_start.getValue().isAfter(dp_end.getValue())) {
            txt_error.setText(DATE_ERROR);
            txt_error.setVisible(true);
            return;
        }

        category = course.getCategories().get(cb_category.getSelectionModel().getSelectedIndex());
        if (assignment == null) {
            assignment = category.addAssignment(tf_name.getText(), totalPoints, cb_optional.isSelected(), cb_extracredit.isSelected(), weightUG, weightGR,
                    java.sql.Date.valueOf(dp_start.getValue()), java.sql.Date.valueOf(dp_end.getValue()), comment);
        } else {
            assignment.update(tf_name.getText(), totalPoints, cb_optional.isSelected(), cb_extracredit.isSelected(), weightUG, weightGR,
                    java.sql.Date.valueOf(dp_start.getValue()), java.sql.Date.valueOf(dp_end.getValue()), comment, assignment.getIndex());
        }

        exit();
    }

    public HashMap<Category, Assignment> load(Course course, Category category, Assignment assignment) {
        this.assignment = assignment;
        this.category = category;
        tf_name.setText(assignment.getName());
        tf_totalpoints.setText(String.valueOf(assignment.getTotalPoints()));
        tf_ugweight.setText(String.valueOf(assignment.getWeightUG()));
        tf_grweight.setText(String.valueOf(assignment.getWeightGR()));
        cb_category.getSelectionModel().select(category.getIndex());
        cb_optional.setSelected(assignment.isOptional());
        cb_extracredit.setSelected(assignment.isExtracredit());


        dp_start.setValue(assignment.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dp_end.setValue(assignment.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return load(course);
    }

    public HashMap<Category, Assignment> load(Course course) {
        this.course = course;

        ArrayList<String> categoryNames = new ArrayList<>();
        course.getCategories().forEach(category1 -> categoryNames.add(category1.getName()));

        cb_category.setItems(FXCollections.observableArrayList(categoryNames));
        cb_category.getSelectionModel().select(0);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("\t\t Add/Edit Assignment");
        stage.getIcons().add(new Image(Loader.ICON_PATH));

        stage.setScene(new Scene(this));
        stage.showAndWait();

        if (category == null || assignment == null) {
            return null;
        }

        HashMap<Category, Assignment> result = new HashMap<>();
        result.put(category, assignment);

        return result;
    }
}