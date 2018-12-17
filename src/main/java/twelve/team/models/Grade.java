package twelve.team.models;

import twelve.team.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Grade implements Commentable {
    private Assignment assignment;
    private Student student;
    private double grade;
    private boolean graded;

    private String comment;

    public Grade(Assignment assignment, Student student, double grade, String comment, boolean graded) {
        this.assignment = assignment;
        this.student = student;
        this.grade = grade;
        this.comment = comment;
    }

    public static Grade create(Assignment assignment, Student student, double grade, String comment, boolean graded) {
        String query = "insert into grade (assignmentID, studentID, grade, comment, graded) values (?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatement(query)) {
            prpst.setInt(1, assignment.getAssignmentID());
            prpst.setInt(2, student.getStudentID());
            prpst.setDouble(3, grade);
            prpst.setString(4, comment);
            prpst.setBoolean(5, graded);
            prpst.executeUpdate();

            return new Grade(assignment, student, grade, comment, graded);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearAssignment(int assignmentID) {
        String query = String.format("delete from grade where assignmentID = %d", assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearAssignments(ArrayList<String> ids) {
        if (ids.size() == 0) {
            return;
        }

        String query = String.format("delete from grade where assignmentID in (%s)", String.join(",", ids));
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearStudent(int studentID) {
        String query = String.format("delete from grade where studentID = %d", studentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearStudents(ArrayList<String> ids) {
        if (ids.size() == 0) {
            return;
        }

        String query = String.format("delete from grade where studentID in (%s)", String.join(",", ids));
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(double grade, String comment, boolean graded) {
        String update = String.format("update grade set grade = %f, comment = '%s', graded = %b where studentID = %d AND assignmentID = %d",
                grade, comment, graded, student.getStudentID(), assignment.getAssignmentID());
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.grade = grade;
            this.comment = comment;
            this.graded = graded;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setGraded(boolean graded) {
        String update = String.format("update grade set graded = %b where studentID = %d AND assignmentID = %d",
                graded, student.getStudentID(), assignment.getAssignmentID());
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.graded = graded;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGrade(double grade, boolean graded) {
        String update = String.format("update grade set grade = %f, graded = %b where studentID = %d AND assignmentID = %d",
                grade, graded, student.getStudentID(), assignment.getAssignmentID());
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.grade = grade;
            this.graded = graded;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Assignment getAssignment()
    {
        return assignment;
    }

    public Student getStudent() {
        return student;
    }

    public double getGrade() {
        return grade;
    }

    public boolean isGraded() {
        return graded;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void editComment(String comment) {
        this.comment = comment;
    }
}
