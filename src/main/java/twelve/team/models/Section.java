package twelve.team.models;

import twelve.team.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Section {

    private int courseID;
    private String courseName;

    private int sectionID;
    private String sectionCode;
    private ArrayList<Student> students;

    public Section(int sectionID, String sectionCode, String courseName, int courseID) {
        this.sectionID = sectionID;
        this.sectionCode = sectionCode;
        this.courseName = courseName;
        this.courseID = courseID;
        students = new ArrayList<>();
    }

    public static Section create(String sectionCode, String courseName, int courseID) {
        String query = "insert into section (sectionCode, courseID) values (?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, sectionCode);
            prpst.setInt(2, courseID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    return new Section(key.getInt(1), sectionCode, courseName, courseID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Section> getSections(int courseID, String courseName) {
        String query = String.format("select * from section where courseID = %d", courseID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Section> sections = new ArrayList<>();
                while (result.next()) {
                    sections.add(new Section(result.getInt("sectionID"),
                            result.getString("sectionCode"), courseName, result.getInt("courseID")));
                }

                return sections;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void delete(Section section) {
        String query = String.format("delete from section where sectionID = %d", section.sectionID);
        try (Statement statement = Database.getDatabase().getStatement()) {

            ArrayList<Student> students = section.getStudents();

            if (students.size() > 0) {
                Student.clear(students);
            }

            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSectionID() {
        return sectionID;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getNameWithCourse() {
        return String.format("%s | %s", sectionCode, courseName);
    }

    public ArrayList<Student> getStudents() {
        if (students == null) {
            load();
        }
        return students;
    }

    public int getNumStudents() {
        return students.size();
    }

    public void load() {
        students = Student.getStudents(sectionID);
    }

    public void update(String sectionCode) {
        String update = String.format("update category set sectionCode = '%s' where sectionID = %d",sectionCode, sectionID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.sectionCode = sectionCode;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Grade> getGrades(Assignment assignment) {
        ArrayList<Grade> grades = new ArrayList<>();
        for (Student student : students) {
            grades.add(student.getGrade(assignment));
        }

        return grades;
    }

    public Student addStudent(String studentName, String universityID, int degree, String comment) {
        Student newStudent = Student.create(studentName, universityID, degree, comment, sectionID);
        students.add(newStudent);
        return newStudent;
    }

    public void moveStudent(Section toSection, Student student) {
        student.move(toSection.sectionID);
        toSection.students.add(students.remove(students.indexOf(student)));
    }

    public void deleteStudent(Student student) {
        deleteStudent(students.indexOf(student));
    }

    public void deleteStudent(int index) {
        Student.delete(students.remove(index));
    }

}
