package twelve.team.models.table;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import twelve.team.models.*;

public class AssignmentModel {

    private Assignment assignment;
    private Category category;
    private Grade grade;

    private SimpleStringProperty gradeScore;
    private SimpleStringProperty assignmentName;
    private SimpleDoubleProperty totalPoints;
    private SimpleBooleanProperty extracredit;
    private SimpleStringProperty comment;

    public AssignmentModel(double gradeScore, String assignmentName, double totalPoints, boolean extracredit, String comment) {
        this.gradeScore = new SimpleStringProperty(String.valueOf(gradeScore));
        this.assignmentName = new SimpleStringProperty(assignmentName);
        this.totalPoints = new SimpleDoubleProperty(totalPoints);
        this.extracredit = new SimpleBooleanProperty(extracredit);
        this.comment = new SimpleStringProperty(comment);
    }

    public SimpleStringProperty getGradeScore() {
        return gradeScore;
    }

    public Category getCategory() {
        return category;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public SimpleStringProperty getAssignmentName() {
        return assignmentName;
    }

    public SimpleDoubleProperty getTotalPoints() {
        return totalPoints;
    }

    public SimpleBooleanProperty getExtracredit() {
        return extracredit;
    }

    public SimpleStringProperty getComment() {
        return comment;
    }

    public AssignmentModel(Assignment assignment, Category category, Course course) {
        double average = 0;
        int count = 0;
        for (Section section : course.getSections()) {
            for (Grade grade : section.getGrades(assignment)) {
                average += grade.getGrade();
                count += 1;
            }
        }

        average = average / count;
        this.gradeScore = new SimpleStringProperty(String.valueOf(average));
        this.category = category;
        this.assignment = assignment;
        this.assignmentName = new SimpleStringProperty(assignment.getName());
        this.totalPoints = new SimpleDoubleProperty(assignment.getTotalPoints());
        this.extracredit = new SimpleBooleanProperty(assignment.isExtracredit());
        this.comment = new SimpleStringProperty(assignment.getComment());
    }

    public AssignmentModel(Assignment assignment, Category category, Section section) {
        double average = 0;
        int count = 0;
        for (Grade grade : section.getGrades(assignment)) {
            average += grade.getGrade();
            count += 1;
        }
        average = average / count;

        this.gradeScore = new SimpleStringProperty(String.valueOf(average));
        this.category = category;
        this.assignment = assignment;
        this.assignmentName = new SimpleStringProperty(assignment.getName());
        this.totalPoints = new SimpleDoubleProperty(assignment.getTotalPoints());
        this.extracredit = new SimpleBooleanProperty(assignment.isExtracredit());
        this.comment = new SimpleStringProperty(assignment.getComment());
    }

    public void setGrade(double grade) {
        if (this.grade != null) {
            this.grade.updateGrade(grade, true);
        }
        this.gradeScore = new SimpleStringProperty(String.valueOf(grade));
    }

    public AssignmentModel(Assignment assignment, Category category, Student student) {
        grade = assignment.getGrade(student);
        this.gradeScore = new SimpleStringProperty(String.valueOf(grade.getGrade()));
        this.category = category;
        this.assignment = assignment;
        this.assignmentName = new SimpleStringProperty(assignment.getName());
        this.totalPoints = new SimpleDoubleProperty(assignment.getTotalPoints());
        this.extracredit = new SimpleBooleanProperty(assignment.isExtracredit());
        this.comment = new SimpleStringProperty(assignment.getComment());
    }
}
