package twelve.team.models.table;

import javafx.beans.property.SimpleStringProperty;
import twelve.team.models.Assignment;
import twelve.team.models.Category;
import twelve.team.models.Course;

public class AssignmentModel {

    private Assignment assignment;
    private Category category;

    private SimpleStringProperty assignmentName;

    public AssignmentModel(Assignment assignment, Category category, boolean average) {

    }
}
