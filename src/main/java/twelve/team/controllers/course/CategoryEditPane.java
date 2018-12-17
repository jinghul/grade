package twelve.team.controllers.course;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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

public class CategoryEditPane extends AnchorPane {
    public static final String CATEGORY_EDIT_PANE_FXML = "course/CategoryEditPane.fxml";
    public static final String EMPTY_FIELD_ERROR = "Please enter the required fields.";
    private static final String POINTS_FORMAT_ERROR = "Please enter a valid decimal for weights.";
    private static final String WEIGHT_FORMAT_ERROR = "Please enter a number less than or equal to '1.0' for a weight.";

    @FXML
    private AnchorPane root;

    @FXML
    private Text txt_error;

    @FXML
    private TextField tf_name, tf_ugweight, tf_grweight;

    @FXML
    private Button btn_save, btn_cancel;

    private Course course;
    private Category category;

    public CategoryEditPane() {
        Loader.load(CATEGORY_EDIT_PANE_FXML, this);
        Platform.runLater( () -> root.requestFocus() );

        InvalidationListener invalidationListener = observable -> {
            if (txt_error.isVisible()) {
                txt_error.setVisible(false);
            }
        };

        tf_name.textProperty().addListener(invalidationListener);
        tf_ugweight.textProperty().addListener(invalidationListener);
        tf_grweight.textProperty().addListener(invalidationListener);

        btn_cancel.setOnAction(e -> exit());
        btn_save.setOnAction(e -> save());
    }

    public void exit() {
        getScene().getWindow().hide();
    }

    public void save() {
        if (StringUtil.isNullOrWhitespace(tf_name.getText(), tf_ugweight.getText(), tf_grweight.getText())) {
            txt_error.setText(EMPTY_FIELD_ERROR);
            txt_error.setVisible(true);
            return;
        }

        double weightUG;
        double weightGR;
        try {
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

        if (category == null) {
            category = course.addCategory(tf_name.getText(), weightUG, weightGR, false);
        } else {
            category.update(tf_name.getText(), weightUG, weightGR, category.getIndex());
        }

        exit();
    }

    public Category load(Course course, Category category) {
        this.category = category;
        tf_name.setText(category.getName());
        tf_ugweight.setText(String.valueOf(category.getWeightUG()));
        tf_grweight.setText(String.valueOf(category.getWeightGR()));
        return load(course);
    }

    public Category load(Course course) {
        this.course = course;

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("\t\t Add/Edit Assignment");
        stage.getIcons().add(new Image(Loader.ICON_PATH));

        stage.setScene(new Scene(this));
        stage.showAndWait();

        return category;
    }
}