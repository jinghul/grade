package twelve.team.models;

import javafx.beans.property.SimpleStringProperty;
import twelve.team.Database;
import twelve.team.controllers.MainPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Student implements Commentable{

    private int sectionID;
    private int studentID;
    public int getStudentID() {
        return studentID;
    }

    private String studentName;
    public String getStudentName() {
        return studentName;
    }

    private String universityID;
    public String getUniversityID() {
        return universityID;
    }

    private int degree; // Undergraduate = 0, Graduate = 1
    public int getDegree() {
        return degree;
    }

    private String comment;
    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void editComment(String comment) {
        String update = String.format("update student set comment = '%s' where studentID = %d", comment, studentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.comment = comment;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    HashMap<Category, ArrayList<Grade>> grades;

    public Student(int studentID, String studentName, String universityID, int degree, String comment, int sectionID) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.universityID = universityID;
        this.degree = degree;
        this.comment = comment;
        this.sectionID = sectionID;
    }

    public static ArrayList<Student> getStudents(int sectionID) {
        String query = String.format("select * from student where sectionID = %d", sectionID);

        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Student> students = new ArrayList<>();
                while (result.next()) {
                    students.add(new Student(result.getInt("studentID"),
                            result.getString("studentName"),
                            result.getString("universityID"),
                            result.getInt("degree"),
                            result.getString("comment"),
                            sectionID));
                }

                return students;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Student create(String studentName, String universityID, int degree, String comment, int sectionID) {
        String query = "insert into student (studentName, universityID, degree, comment, sectionID) values (?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, studentName);
            prpst.setString(2, universityID);
            prpst.setInt(3, degree);
            prpst.setString(4, comment);
            prpst.setInt(5, sectionID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    Student student = new Student(key.getInt(1), studentName, universityID, degree, comment, sectionID);
                    for (Category category : MainPane.teacher.getCurrentCourse().getCategories()) {
                        for (Assignment assignment : category.getAssignments()) {
                            Grade.create(assignment, student, 0, "", false);
                        }
                    }

                    return student;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void delete(Student student) {
        String query = String.format("delete from student where studentID = %d", student.studentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clear(ArrayList<Student> students) {
        if (students.size() == 0) {
            return;
        }

        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            ids.add(String.valueOf(students.get(i).getStudentID()));
        }

        Grade.clearStudents(ids);
        String query = String.format("delete from student where studentID in (%s)", String.join(",", ids));
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String studentName, String universityID, int degree, String comment) {
        String update = String.format("update student set studentName = '%s', universityID = '%s', degree = %d, " +
                "comment = '%s' where studentID = %d", studentName, universityID, degree, comment, studentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.studentName = studentName;
            this.universityID = universityID;
            this.degree = degree;
            this.comment = comment;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void move(int sectionID) {
        String update = String.format("update student set sectionID = %d where studentID = %d", sectionID, studentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.sectionID = sectionID;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Grade getGrade(Assignment assignment) {
        String query = String.format("select * from grade where assignmentID = %d AND studentID = %d", assignment.getAssignmentID(), getStudentID());
        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                if (result.next()) {
                    return new Grade(assignment, this, result.getDouble("grade"), result.getString("comment"), result.getBoolean("graded"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<Category, ArrayList<Grade>> getGrades() {
        grades = new HashMap<>();
        for (Category category : MainPane.teacher.getCurrentCourse().getCategories()) {
            grades.put(category, category.getGrades(this));
        }

        return grades;
    }

    public double getAverage() {
        if (grades == null) grades = getGrades();

        double average = 0;
        for (Category category : grades.keySet()) {
            double categoryWeight = degree == 0 ? category.getWeightUG() : category.getWeightGR();
            for (Grade grade : grades.get(category)) {
                if (!grade.isGraded()) {
                    continue;
                }

                Assignment assignment = grade.getAssignment();
                double assignmentWeight = degree == 0 ? assignment.getWeightUG() : assignment.getWeightGR();
                double gradePercentage = grade.getGrade() < 0 ? (assignment.getTotalPoints() + grade.getGrade()) / assignment.getTotalPoints()
                        : grade.getGrade() / assignment.getTotalPoints();
                average += categoryWeight * assignmentWeight * gradePercentage;
            }
        }

        return average;
    }

    public double getAverage(Category category) {
        if (grades == null) grades = getGrades();

        double average = 0;
        double categoryWeight = degree == 0 ? category.getWeightUG() : category.getWeightGR();
        for (Grade grade : grades.get(category)) {
            if (!grade.isGraded()) {
                continue;
            }

            Assignment assignment = grade.getAssignment();
            double assignmentWeight = degree == 0 ? assignment.getWeightUG() : assignment.getWeightGR();
            double gradePercentage = grade.getGrade() < 0 ? (assignment.getTotalPoints() + grade.getGrade()) / assignment.getTotalPoints()
                    : grade.getGrade() / assignment.getTotalPoints();
            average += categoryWeight * assignmentWeight * gradePercentage;
        }

        return average;
    }
}