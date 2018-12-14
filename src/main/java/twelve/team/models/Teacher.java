package twelve.team.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import twelve.team.Database;

/**
 * The user representation of a teacher. Initialized after authentication and
 * saves a list of semesters. A teacher object is passed into the semester selection
 * screen as a reference to CRUD semesters.
 */
public class Teacher {
    /* Teacher Variables */
    private int teacherID;
    private String teacherName;
    private String username;

    private ArrayList<Semester> semesters;
    private ArrayList<Course> courses;

    public Teacher(String username) {
        /* First fetch the teacher's teacherName */
        String query = "select * from teacher where username = '" + username + "'";

        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)){
                if (result.next()) {
                    this.username = username;
                    this.teacherID = result.getInt("teacherID");
                    this.teacherName = result.getString("teacherName").split(" ")[0];
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Teacher(int teacherID, String username, String teacherName) {
        this.teacherID = teacherID;
        this.username = username;
        this.teacherName = teacherName;
    }

    public static void create(String username, String password, String name) {
        String query = "insert into teacher (username, password, teacherName) values (?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatement(query)) {
            prpst.setString(1, username);
            prpst.setString(2, password);
            prpst.setString(3, name);
            prpst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.teacherName;
    }

    public ArrayList<Semester> getSemesters() {
        if (semesters == null) {
            semesters = Semester.getSemesters(teacherID);
        }
        
        return semesters;
    }

    public void deleteSemester(int index) {
        Semester.delete(semesters.remove(index));
    }

    public void deleteSemester(Semester semester) {
        deleteSemester(semesters.indexOf(semester));
    }

    public Semester addSemester(String semesterName, int semesterYear) {
        Semester semester = Semester.create(semesterName, semesterYear, teacherID);
        semesters.add(semester);
        return semester;
    }
}
