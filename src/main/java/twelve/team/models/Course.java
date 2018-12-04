package twelve.team.models;

import twelve.team.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Course {

    private int courseID;
    private String name;
    private String semesterName;
    private ArrayList<Student> students;
    private ArrayList<Category> categories;
    private boolean initialized;

    public Course(int courseID, String name, String semesterName) {
        this.courseID = courseID;
        this.name = name;
        this.semesterName = semesterName;
    }

    public static ArrayList<Course> getCourses(int semesterID, String semesterName) {
        String query = "select * from course where semesterID = " + semesterID;
        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Course> courses = new ArrayList<>();
                while (result.next()) {
                    courses.add(new Course(result.getInt("courseID"),
                            result.getString("courseName"),
                            semesterName));
                }

                return courses;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void init() {
        if (initialized) {
            return;
        }

//        courses = Course.getCourses(semesterID);
        initialized = true;
    }

    public String getName() {
        return name;
    }

    public String getNameWithSemester() {
        return name + "  |  " + semesterName;
    }

    public int getNumStudents() {
        return students.size();
    }
}