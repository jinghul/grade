package twelve.team.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import twelve.team.Database;

/**
 * The container for courses.
 * Teachers map to semesters first, from which
 * they can then view the courses they manage.
 */
public class Semester {
    
    ArrayList<Course> courses;
    String semesterID;
    String name;

    public Semester(String semesterID, String name) {
        this.semesterID = semesterID;
        this.name = name;
    }

    public static ArrayList<Semester> getSemesters(String teacherID) {
        try {
            /* Fetch the existing semesters of the teacher */
            String query = "select * from semester where teacherID = '" + teacherID + "'";
            ResultSet result = Database.getDatabase().getQuery(query);

            ArrayList<Semester> semesters = new ArrayList<>();
            while (result.next()) {
                semesters.add(new Semester(result.getString("semesterID"),
                        result.getString("semesterName")));
            }

            return semesters;
        } catch(SQLException e) {
            e.printStackTrace(); // DEBUG
            return null;
        }
    }
    
    private void fetch() {
        
    }
    
    public ArrayList<Course> getCourses() {
        return null;
    }
    
    public boolean editCourse(String courseID) {
        return false;
    }

    public boolean removeCourse(String courseID) {
        return false;
    }

    public boolean addCourse(Course course) {
        return false;
    }
}
