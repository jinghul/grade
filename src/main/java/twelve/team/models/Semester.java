package twelve.team.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import twelve.team.Database;

/**
 * The container for courses.
 * Teachers map to semesters first, from which
 * they can then view the courses they manage.
 */
public class Semester {
    
    private ArrayList<Course> courses;
    private int semesterID;
    private int teacherID;
    private String semesterName;
    private int year;
    private boolean initialized;

    private Semester(int semesterID, String name, int year, int teacherID) {
        this.semesterID = semesterID;
        this.semesterName = name;
        this.year = year;
        this.teacherID = teacherID;
    }

    public static ArrayList<Semester> getSemesters(int teacherID) {
        String query = "select * from semester where teacherID = " + teacherID;
        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Semester> semesters = new ArrayList<>();
                while (result.next()) {
                    semesters.add(new Semester(result.getInt("semesterID"),
                            result.getString("semesterName"),
                            result.getInt("semesterYear"), teacherID));
                }

                return semesters;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Semester create(String semesterName, int semesterYear, int teacherID) {
        String query = "insert into semester (semesterName, semesterYear, teacherID) values (?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, semesterName);
            prpst.setInt(2, semesterYear);
            prpst.setInt(3, teacherID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    return new Semester(key.getInt(1), semesterName, semesterYear, teacherID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void delete(Semester semester) {
        String query = "delete from semester where semesterID = " + semester.semesterID;
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if (initialized) {
            return;
        }

        courses = Course.getCourses(semesterID, getName());
        initialized = true;
    }

    public void update(String semesterName, int semesterYear) {
        String update = "update semester " + "set semesterName = '" + semesterName
                + "' semesterYear = " + semesterYear +
                "where semesterID = " + semesterID;
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return semesterName + " " + year;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public int getYear() {
        return year;
    }

    public int getNumCourses() {
        return courses.size();
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
