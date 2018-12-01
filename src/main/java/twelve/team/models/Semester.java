package twelve.team.models;

import java.sql.PreparedStatement;
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

    public ArrayList<Semester> getSemesters(Teacher teacher) {
//        String query = "select * where teacherID = '" + teacher.get
//        PreparedStatement prpst = Database.getDatabase().prepareStatement()
        return null;
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
