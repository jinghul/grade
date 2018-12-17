package twelve.team.models;

import twelve.team.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class Course {

    private int semesterID;
    private String semesterName;

    private final int courseID;
    private String courseDepartment;
    private int courseNumber;
    private String courseCode;
    private String courseDescription;

    private ArrayList<Section> sections;
    private ArrayList<Category> categories;

    public Course(int courseID, String courseDepartment, int courseNumber, String courseCode, String courseDescription, String semesterName, int semesterID) {
        this.courseID = courseID;
        this.courseDepartment = courseDepartment;
        this.courseNumber = courseNumber;
        this.courseCode = courseCode;
        this.courseDescription = courseDescription;

        this.semesterID = semesterID;
        this.semesterName = semesterName;
        sections = Section.getSections(courseID, getName());
        categories = Category.getCategories(courseID);
        System.out.println(String.format("Course %s has %d sections", getName(), sections.size()));
    }

    public static ArrayList<Course> getCourses(int semesterID, String semesterName) {
        String query = String.format("select * from course where semesterID = %d", semesterID);

        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Course> courses = new ArrayList<>();
                while (result.next()) {
                    courses.add(new Course(result.getInt("courseID"),
                            result.getString("courseDepartment"),
                            result.getInt("courseNumber"),
                            result.getString("courseCode"),
                            result.getString("courseDescription"),
                            semesterName,
                            semesterID));
                }

                return courses;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Course create(String courseDepartment, int courseNum, String courseCode, String courseDescription, String semesterName, int semesterID) {
        String query = "insert into course (courseDepartment, courseNumber, courseCode, courseDescription, semesterID) values (?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, courseDepartment);
            prpst.setInt(2, courseNum);
            prpst.setString(3, courseCode);
            prpst.setString(4, courseDescription);
            prpst.setInt(5, semesterID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    DefaultCategory.create("-", 1.0, 1.0, 0, key.getInt(1));
                    return new Course(key.getInt(1), courseDepartment, courseNum, courseCode, courseDescription, semesterName, semesterID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load the sections  + students and categories + assignments only when needed
     * Calls the load functions on them to get the required information from the database.
     */
    public void load() {
        for (Section section : sections) {
            section.load();
        }

        for (Category category : categories) {
            category.load();
        }
    }

    public static void delete(Course course) {
        String query = String.format("delete from course where courseID = %d", course.courseID);
        try (Statement statement = Database.getDatabase().getStatement()) {

            for (Category category : course.getCategories()) {
                Category.delete(category);
            }

            for (Section section : course.getSections()) {
                Section.delete(section);
            }

            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String courseDepartment, int courseNum, String courseSection, String courseDescription) {
        String update = String.format("update course set courseDepartment = '%s', courseNumber = %d, courseCode = %s, " +
                "courseDescription = '%s' where courseId = %d", courseDepartment, courseNum, courseSection, courseDescription, courseID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.courseDepartment = courseDepartment;
            this.courseNumber = courseNum;
            this.courseCode = courseSection;
            this.courseDescription = courseDescription;

            sections.forEach(section -> section.setCourseName(getName()));

            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        if (courseCode != null) {
            return String.format("%s%d %s", courseDepartment, courseNumber, courseCode);
        }
        return String.format("%s%d", courseDepartment, courseNumber);
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getNameWithSemester() {
        return String.format("%s | %s", getName(), semesterName);
    }

    public String getCourseDepartment() {
        return courseDepartment;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public int getNumSections() {
        return sections.size();
    }

    public ArrayList<Category> getCategories() {
        if (categories == null) {
            load();
        }
        return categories;
    }

    public ArrayList<Section> getSections() {
        if (sections == null) {
            load();
        }
        return sections;
    }

    public Category addCategory(String categoryName, double weightGR, double weightUG, boolean defaultCategory) {
        Category newCategory;
        if (defaultCategory) {
            newCategory = DefaultCategory.create(categoryName, weightGR, weightUG, categories.size(), courseID);
        } else {
            newCategory = Category.create(categoryName, weightGR, weightUG, categories.size(), courseID);
        }

        categories.add(newCategory != null ? newCategory.getIndex() : categories.size(), newCategory);
        return newCategory;
    }

    public void recalculateWeights(Category category, double newWeight, int degree) {
        if (categories.size() < 1) {
            return;
        }

        double difference = (degree == 0) ? newWeight - category.getWeightUG() : newWeight - category.getWeightGR();
        double update = -1 * difference / (categories.size() - 1);
        for (int i = 0; i < categories.size(); i++) {
            if (i != category.getIndex()) {
                categories.get(i).changeWeight((degree == 0) ? category.getWeightUG() + update : category.getWeightGR() + update, degree);
            }
        }
        category.changeWeight(newWeight, degree);
    }

    public void shiftCategory(Category category, int newIndex) {
        categories.remove(category.getIndex());
        for (int i = newIndex; i < categories.size(); i++) {
            categories.get(i).shift(i + 1);
        }
        categories.add(newIndex, category);
    }

    public void deleteCategory(int index) {
        Category.delete(categories.remove(index));
        for (int i = index; i < categories.size(); i++) {
            categories.get(i).shift(i - 1);
        }
    }

    public Section addSection(String sectionCode, String courseName) {
        Section newSection = Section.create(sectionCode, courseName, courseID);
        sections.add(newSection);
        return newSection;
    }

    public void deleteSection(int sectionID) {
        Section.delete(sections.get(sectionID));
        sections.remove(sectionID);
    }
}