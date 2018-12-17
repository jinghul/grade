package twelve.team.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import twelve.team.Loader;
import twelve.team.Router;
import twelve.team.controllers.login.LoginPane;
import twelve.team.controllers.semester.SemesterSelectionPane;
import twelve.team.utils.Animator;
import twelve.team.models.Teacher;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPane implements Initializable, EventHandler<ActionEvent> {
    public static final String MAIN_FXML_PATH = "MainPane.fxml";


    @FXML
    private GridPane root;

    @FXML
    private Text txt_name;

    @FXML
    private Button btn_home, btn_semester, btn_course, btn_grade, btn_logout;

    public static Teacher teacher;

    public static void load(Teacher teacher) {
        MainPane mainPane = (MainPane) Loader.loadToScene(MAIN_FXML_PATH);

        // Make sure the pane is loaded correctly
        assert mainPane != null;

        mainPane.setTeacher(teacher);
        Router.create(mainPane);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Animator.fadeIn(root, null);
        btn_home.setOnAction(this);
        btn_semester.setOnAction(this);
        btn_logout.setOnAction(this);
    }


    private void setTeacher(Teacher teacherRef) {
        teacher = teacherRef;
        txt_name.setText(teacher.getName());
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public GridPane getRoot() {
        return root;
    }

    @Override
    public void handle(ActionEvent event) {
        Object request = event.getSource();
        Router router = Router.getRouter();
        router.clearStacks();
        if (request == btn_home) {
            HomePane homePane = new HomePane();
            router.addPane(homePane, true);
        } else if (request == btn_semester) {
            SemesterSelectionPane semesterSelectionPane = new SemesterSelectionPane();
            semesterSelectionPane.load();
            router.addPane(semesterSelectionPane, true);
        } else if (request == btn_course) {

        } else if (request == btn_grade) {

        } else if (request == btn_logout) {
            Animator.fadeOut(root, e -> LoginPane.load());
        }
    }
}
