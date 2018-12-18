package twelve.team.models.table;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import twelve.team.models.Assignment;
import twelve.team.models.Grade;
import twelve.team.models.Section;
import twelve.team.models.Student;

public class StudentModel {

    private SimpleStringProperty studentName;
    public SimpleStringProperty getStudentName() {
        return studentName;
    }

    private SimpleStringProperty universityID;
    public SimpleStringProperty getUniversityID() {
        return universityID;
    }

    private SimpleStringProperty degree; // Undergraduate = 0, Graduate = 1
    public SimpleStringProperty getDegree() {
        return degree;
    }

    private SimpleStringProperty comment;
    public SimpleStringProperty getComment() {
        return comment;
    }
    public void setComment(String comment) {
        if (grade != null) {
            grade.editComment(comment);
        } else {
            student.editComment(comment);
        }

        this.comment = new SimpleStringProperty(comment);
    }

    private Grade grade;
    private SimpleBooleanProperty isGraded;
    public SimpleBooleanProperty getIsGraded() {
        return isGraded;
    }

    private SimpleStringProperty sectionCode;
    public SimpleStringProperty getSectionCode() {
        return sectionCode;
    }

    private SimpleStringProperty gradeScore;
    public SimpleStringProperty getGrade() {
        return gradeScore;
    }
    public void setGrade(double newGrade) {
        this.gradeScore = new SimpleStringProperty(String.valueOf(newGrade));
        grade.updateGrade(newGrade,true);
    }

    private Student student;
    private Section section;

    public Section getSection() {
        return section;
    }

    public Student getStudent() {
        return student;
    }

    public StudentModel(String name, String universityID, String degree) {
        this.studentName = new SimpleStringProperty(name);
        this.universityID = new SimpleStringProperty(universityID);
        this.degree = new SimpleStringProperty(degree);
    }

    public StudentModel(Student student, Section section, Assignment assignment) {
        this(student.getStudentName(), student.getUniversityID(), student.getDegree() == 0 ? "Undergraduate" : "Graduate");
        this.student = student;
        this.section = section;
        this.sectionCode = new SimpleStringProperty(section.getSectionCode());
        if (assignment == null) {
            comment = new SimpleStringProperty(student.getComment());
            gradeScore = new SimpleStringProperty(String.valueOf(student.getAverage()));
        } else {
            grade = student.getGrade(assignment);
            isGraded = new SimpleBooleanProperty(grade.isGraded());
            gradeScore = new SimpleStringProperty(String.valueOf(grade.getGrade()));
            comment = new SimpleStringProperty(grade.getComment());
        }
    }

    public StudentModel(String name, String universityID, String degree, String comment, String grade) {
        studentName = new SimpleStringProperty(name);
        this.universityID = new SimpleStringProperty(universityID);
        this.degree = new SimpleStringProperty(degree);
        this.comment = new SimpleStringProperty(comment);
        this.gradeScore = new SimpleStringProperty(grade);
    }
}
