package twelve.team.controllers;

import javafx.fxml.Initializable;
import twelve.team.models.Teacher;

import java.net.URL;
import java.util.ResourceBundle;

public class SemesterController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void loadFromTeacher(Teacher teacher) {
        teacher.getSemesters();
    }
}
