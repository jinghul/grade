package twelve.team.controllers.semester;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import twelve.team.Loader;
import twelve.team.controllers.MainPane;
import twelve.team.models.Semester;

public class SemesterEditPane extends AnchorPane {
    public static final String ICON_PATH = "/imgs/main_icon.png";
    public static final String EDIT_SEMESTER_FXML_PATH = "semester/SemesterEditPane.fxml";
    private static final String[] SEMESTER_LIST = {"Fall", "Spring", "Summer 1", "Summer 2"};

    private static final String SEMESTER_EXISTS_ERROR = "Semester already exists.";
    private static final String YEAR_FORMAT_ERROR = "Please enter a valid year.";

    @FXML
    private AnchorPane root;

    @FXML
    private Text txt_title;

    @FXML
    private Text txt_error;

    @FXML
    private ChoiceBox cb_name;

    @FXML
    private TextField tf_year;

    @FXML
    private Button btn_save;

    @FXML
    private Button btn_cancel;

    private Semester semester;

    public SemesterEditPane() {
        Loader.load(EDIT_SEMESTER_FXML_PATH, this);
        Platform.runLater( () -> root.requestFocus() );

        cb_name.setItems(FXCollections.observableArrayList(SEMESTER_LIST));
        cb_name.getSelectionModel().select(0);
        tf_year.textProperty().addListener(e -> {
            if (txt_error.isVisible()) {
                txt_error.setVisible(false);
            }
        });
        tf_year.setOnAction(e -> save());
        btn_cancel.setOnAction(e -> exit());
        btn_save.setOnAction(e -> save());
    }

    public void exit() {
        getScene().getWindow().hide();
    }

    public void save() {
        String semesterName = cb_name.getSelectionModel().getSelectedItem().toString();
        int semesterYear;
        try {
             semesterYear = Integer.valueOf(tf_year.getText());
        } catch (NumberFormatException e) {
            txt_error.setText(YEAR_FORMAT_ERROR);
            txt_error.setVisible(true);
            return;
        }

        if (semester == null) {
            if (!MainPane.teacher.checkSemesterExists(semesterName, semesterYear)) {
                semester = MainPane.teacher.addSemester(semesterName, semesterYear);
            } else {
                txt_error.setText(SEMESTER_EXISTS_ERROR);
                txt_error.setVisible(true);
                return;
            }
        } else {
            if (!(semesterName + " " + semesterYear).equals(semester.getName())) {
                semester.update(semesterName, semesterYear);
            }
        }

        exit();
    }

    public Semester load(Semester semester) {
        this.semester = semester;
        for (int i = 0; i < cb_name.getItems().size(); i++) {
            if (cb_name.getItems().get(i).toString().equals(semester.getSemesterName())) {
                cb_name.getSelectionModel().select(i);
            }
        }

        tf_year.setText(String.valueOf(semester.getYear()));
        return load();
    }

    public Semester load() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("\t Add/Edit Semester");
        stage.getIcons().add(new Image(ICON_PATH));

        stage.setScene(new Scene(this));
        stage.showAndWait();
        return semester;
    }
}
