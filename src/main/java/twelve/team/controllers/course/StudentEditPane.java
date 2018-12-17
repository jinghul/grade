package twelve.team.controllers.course;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import twelve.team.models.*;
import twelve.team.utils.StringUtil;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentEditPane extends AnchorPane {
    public static final String STUDENT_EDIT_PANE_FXML = "course/StudentEditPane.fxml";
    public static final String EMPTY_FIELD_ERROR = "Please enter the required fields.";
    private static final String NO_SECTION_ERROR = "You have no sections. Please create one first.";

    private static final String[] DEGREES = {"Undergraduate", "Graduate"};

    @FXML
    private AnchorPane root;

    @FXML
    private StackPane pane_stack;

    @FXML
    private Text txt_error;

    @FXML
    private TextField tf_name, tf_universityID;

    @FXML
    private ChoiceBox cb_degree, cb_section;

    @FXML
    private Button btn_save, btn_cancel, btn_comment;

    private Course course;
    private Section section;
    private Student student;
    private String comment;

    public StudentEditPane() {
        Loader.load(STUDENT_EDIT_PANE_FXML, this);
        Platform.runLater( () -> root.requestFocus() );

        InvalidationListener invalidationListener = observable -> {
            if (txt_error.isVisible()) {
                txt_error.setVisible(false);
            }
        };

        tf_name.textProperty().addListener(invalidationListener);
        tf_universityID.textProperty().addListener(invalidationListener);

        cb_degree.setItems(FXCollections.observableArrayList(DEGREES));
        cb_degree.getSelectionModel().select(0);

        btn_comment.setOnAction(e -> {
            twelve.team.utils.Dialog.showCommentDialog(student == null ? "Student" : student.getStudentName(), comment, pane_stack, item -> {
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
        if (StringUtil.isNullOrWhitespace(tf_name.getText(), tf_universityID.getText())) {
            txt_error.setText(EMPTY_FIELD_ERROR);
            txt_error.setVisible(true);
            return;
        }

        Section newSection = course.getSections().get(cb_section.getSelectionModel().getSelectedIndex());
        if (student == null) {
            student = section.addStudent(tf_name.getText(), tf_universityID.getText(), cb_degree.getSelectionModel().getSelectedIndex(), "");
        } else {
            student.update(tf_name.getText(), tf_universityID.getText(), cb_degree.getSelectionModel().getSelectedIndex(), "");
        }

        if (section != newSection) {
            section.moveStudent(newSection, student);
            section = newSection;
        }

        exit();
    }

    public HashMap<Section, Student> load(Course course, Section section, Student student) {
        this.student = student;
        this.section = section;
        tf_name.setText(student.getStudentName());
        tf_universityID.setText(student.getUniversityID());
        cb_degree.getSelectionModel().select(student.getDegree());

        return load(course);
    }

    public HashMap<Section, Student> load(Course course) {
        this.course = course;

        ArrayList<String> sectionCodes = new ArrayList<>();
        ArrayList<Section> sections = course.getSections();
        if (sections.size() == 0) {
            return null;
        }

        int index = -1;
        for (int i = 0; i < sections.size(); i++) {
            sectionCodes.add(sections.get(i).getSectionCode());
            if (section != null && sections.get(i).equals(section)) {
                index = i;
            }
        }

        cb_section.setItems(FXCollections.observableArrayList(sectionCodes));
        cb_section.getSelectionModel().select(index >= 0 ? index : 0);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("\t\t Add/Edit Assignment");
        stage.getIcons().add(new Image(Loader.ICON_PATH));

        stage.setScene(new Scene(this));
        stage.showAndWait();

        if (section == null || student == null) {
            return null;
        }

        HashMap<Section, Student> result = new HashMap<>();
        result.put(section, student);

        return result;
    }
}
