package twelve.team.models;

import twelve.team.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Course {

    private int semesterID;
    private String semesterName;

    private int courseID;
    private String courseDepartment;
    private int courseNumber;
    private String courseSection;
    private String courseDescription;

    private ArrayList<Student> students;
    private ArrayList<Category> categories;
    private boolean initialized;

    public Course(int courseID, String courseDepartment, int courseNumber, String courseSection, String courseDescription, String semesterName, int semesterID) {
        this.courseID = courseID;
        this.courseDepartment = courseDepartment;
        this.courseNumber = courseNumber;
        this.courseSection = courseSection;


        // TODO: COURSE TIMES
//        if (courseTimes != null) {
//            String[] times = courseTimes.split(" ,");
//            this.courseTimes = courseTimes;
//        }

        this.courseDescription = courseDescription;

        this.semesterID = semesterID;
        this.semesterName = semesterName;
    }

    public static ArrayList<Course> getCourses(int semesterID, String semesterName) {
        String query = String.format("select * from course where semesterID = ", semesterID);

        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Course> courses = new ArrayList<>();
                while (result.next()) {
                    courses.add(new Course(result.getInt("courseID"),
                            result.getString("courseDepartment"),
                            result.getInt("courseNumber"),
                            result.getString("courseSection"),
                            result.getString("courseDescription"),
                            semesterName,
                            semesterID));
                }

                return courses;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Course create(String courseDepartment, int courseNum, String courseSection, String courseDescription, String semesterName, int semesterID) {
        String query = "insert into course (courseDepartment, courseNumber, courseSection, courseDescription, semesterID) values (?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, courseDepartment);
            prpst.setInt(2, courseNum);
            prpst.setString(3, courseSection);
            prpst.setString(4, courseDescription);
            prpst.setInt(5, semesterID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    return new Course(key.getInt(1), courseDepartment, courseNum, courseSection, courseDescription, semesterName, semesterID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void init() {
        if (initialized) {
            return;
        }

        students = Student.getStudents(courseID, getName());
        categories = Category.getCategories(courseID);
        System.out.println(String.format("Course %s has %d students", getName(), students.size()));
        initialized = true;
    }

    public static void delete(Course course) {
        String query = String.format("delete from course where courseID = %d", course.courseID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String courseDepartment, int courseNum, String courseSection, String courseDescription) {
        String update = String.format("update course set courseDepartment = '%s', courseNumber = %d, courseSection = %s, " +
                "courseDescription = '%s' where courseId = %d", courseDepartment, courseNum, courseSection, courseDescription, courseID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.courseDepartment = courseDepartment;
            this.courseNumber = courseNum;
            this.courseSection = courseSection;
            this.courseDescription = courseDescription;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        if (courseSection != null) {
            return String.format("%s%d %s", courseDepartment, courseNumber, courseSection);
        }
        return String.format("%s%d", courseDepartment, courseNumber);
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public String getCourseSection() {
        return courseSection;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public String getNameWithSemester() {
        return String.format("%s | %s", getName(), semesterName);
    }

    public int getNumStudents() {
        if (!initialized) {
            return -1;
        }

        return students.size();
    }

    public void addCategory() {

    }

    public void addStudent() {

    }
}