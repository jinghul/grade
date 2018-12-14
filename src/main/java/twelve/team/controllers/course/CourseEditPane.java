package twelve.team.controllers.course;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import twelve.team.Loader;
import twelve.team.controllers.MainPane;
import twelve.team.models.Course;
import twelve.team.models.Semester;
import twelve.team.utils.StringUtil;

public class CourseEditPane extends AnchorPane {
    public static final String ICON_PATH = "/imgs/main_icon.png";
    public static final String EDIT_SEMESTER_FXML_PATH = "course/CourseEditPane.fxml";
    public static final String EMPTY_FIELD_ERROR = "Please enter the required fields.";
    public static final String COURSE_NUM_ERROR = "Please enter a valid course number.";

    @FXML
    private Text txt_title;

    @FXML
    private Text txt_error;

    @FXML
    private TextField tf_dept;

    @FXML
    private TextField tf_course_num;

    @FXML
    private TextField tf_section;

    @FXML
    private TextArea tf_description;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_cancel;

    private Course course;
    private Semester semester;

    public CourseEditPane() {
        Loader.load(EDIT_SEMESTER_FXML_PATH, this);
        InvalidationListener invalidationListener = observable -> {
            if (txt_error.isVisible()) {
                txt_error.setVisible(false);
            }
        };

        tf_dept.textProperty().addListener(invalidationListener);
        tf_course_num.textProperty().addListener(invalidationListener);

        btn_cancel.setOnAction(e -> exit());
        btn_save.setOnAction(e -> save());
    }

    public void exit() {
        getScene().getWindow().hide();
    }

    public void save() {
        if (StringUtil.isNullOrWhitespace(tf_course_num.getText()) || StringUtil.isNullOrWhitespace(tf_dept.getText())) {
            txt_error.setText(EMPTY_FIELD_ERROR);
            txt_error.setVisible(true);
            return;
        }

        int courseNum;

        try {
            courseNum = Integer.valueOf(tf_course_num.getText());
        } catch (NumberFormatException e) {
            txt_error.setText(COURSE_NUM_ERROR);
            txt_error.setVisible(true);
            return;
        }

        if (course == null) {
            course = semester.addCourse(tf_dept.getText(), courseNum, tf_section.getText(), tf_description.getText());
            course.init();
        } else {
            course.update(tf_dept.getText(), courseNum, tf_section.getText(), tf_description.getText());
        }

        exit();
    }

    public Course load(Semester semester, Course course) {
        this.course = course;
        tf_dept.setText(course.getCourseDepartment());
        tf_course_num.setText(String.valueOf(course.getCourseNumber()));
        if (course.getCourseSection() != null) {
            tf_section.setText(course.getCourseSection());
        }
        if (course.getCourseDescription() != null) {
            tf_description.setText(course.getCourseDescription());
        }

        return load(semester);
    }

    public Course load(Semester semester) {
        this.semester = semester;

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("\t Add/Edit Course");
        stage.getIcons().add(new Image(ICON_PATH));

        stage.setScene(new Scene(this));
        stage.showAndWait();
        return course;
    }
}