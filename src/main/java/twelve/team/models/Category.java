package twelve.team.models;

import twelve.team.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Category implements Weightable {

    private final int categoryID;
    private int index;
    private String categoryName;
    private double weightUG;
    private double weightGR;
    private int courseID;
    private ArrayList<Assignment> assignments;


    public Category(int categoryID, String categoryName, double weightGR, double weightUG, int index, int courseID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.weightGR = weightGR;
        this.weightUG = weightUG;
        this.index = index;
        this.courseID = courseID;
        assignments = new ArrayList<>();
    }

    public static Category create(String categoryName, double weightGR, double weightUG, int index, int courseID) {
        String query = "insert into category (categoryName, weightGR, weightUG, place, courseID) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, categoryName);
            prpst.setDouble(2, weightGR);
            prpst.setDouble(3, weightUG);
            prpst.setInt(4, index);
            prpst.setInt(5, courseID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    return new Category(key.getInt(1), categoryName, weightGR, weightUG, index, courseID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Category> getCategories(int courseID) {
        String query = String.format("select * from category where courseID = %d", courseID);

        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Category> categories = new ArrayList<>();
                while (result.next()) {
                    if (result.getBoolean("isDefault")) {
                        categories.add(new DefaultCategory(result.getInt("categoryID"),
                                result.getString("categoryName"),
                                result.getDouble("weightGR"),
                                result.getDouble("weightUG"),
                                result.getInt("place"),
                                courseID));
                    } else {
                        categories.add(new Category(result.getInt("categoryID"),
                                result.getString("categoryName"),
                                result.getDouble("weightGR"),
                                result.getDouble("weightUG"),
                                result.getInt("place"),
                                courseID));
                    }
                }

                categories.sort(Comparator.comparingInt(Category::getIndex));
                return categories;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void delete(Category category) {
        String query = String.format("delete from category where categoryID = %d", category.categoryID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            ArrayList<Assignment> assignments = category.getAssignments();
            if (assignments.size() > 0) {
                Assignment.clear(assignments);
            }
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isDefault() {
        return false;
    }

    public int getCategoryID() {
        return  categoryID;
    }

    @Override
    public String getName() {
        return  categoryName;
    }

    public int getNumAssignments() {
        return assignments.size();
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void setName(String name) {
        String update = String.format("update category set categoryName = '%s' where categoryID = %d", name, categoryID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.categoryName = name;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getWeightUG() {
        return weightUG;
    }

    @Override
    public double getWeightGR() {
        return weightGR;
    }

    @Override
    public void setWeightUG(double weightUG) {
        String update = String.format("update category set weightUG = %f where categoryID = %d", weightUG, categoryID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.weightUG = weightUG;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWeightGR(double weightGR) {
        String update = String.format("update category set weightGR = %f where categoryID = %d", weightGR, categoryID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.weightGR = weightGR;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeWeight(double newWeight, int degree) {
        if (degree == 0) {
            setWeightUG(newWeight);
        } else {
            setWeightGR(newWeight);
        }
    }

    public ArrayList<Assignment> getAssignments() {
        if (assignments == null) {
            load();
        }
        return assignments;
    }

    public void load() {
        assignments = Assignment.getAssignments(categoryID);
    }

    public void shift(int newIndex) {
        String update = String.format("update category set place = %d where categoryID = %d", newIndex, categoryID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.index = newIndex;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String categoryName, double weightGR, double weightUG, int index) {
        String update = String.format("update category set categoryName = '%s', weightGR = %f, weightUG = %f, place = %d" +
                "where categoryID = %d", categoryName, weightGR, weightUG, index, categoryID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.categoryName = categoryName;
            this.weightGR = weightGR;
            this.weightUG = weightUG;
            this.index = index;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Assignment addAssignment(String assignmentName, double totalPoints,
                                    boolean optional, boolean extracredit, double weightUG,
                                    double weightGR, Date startDate, Date endDate,
                                    String comment) {
        Assignment newAssignment = Assignment.create(assignmentName, totalPoints, optional,
                extracredit, weightUG, weightGR, startDate, endDate, comment, assignments.size(), categoryID);
        assignments.add(newAssignment);
        return newAssignment;
    }

    public void shiftAssignment(Assignment assignment, int newIndex) {
        for (int i = newIndex; i < assignments.size(); i++) {
            assignments.get(i).shift(i+1);
        }
        assignment.shift(newIndex);
        assignments.add(newIndex, assignment);
    }

    public void moveAssignment(Category toCategory, Assignment assignment) {
        assignment.move(toCategory.categoryID, toCategory.getNumAssignments());
        toCategory.assignments.add(assignments.remove(assignments.indexOf(assignment)));
    }

    public void deleteAssignment(Assignment assignment) {
        deleteAssignment(assignments.indexOf(assignment));
    }

    public void deleteAssignment(int index) {
        Assignment.delete(assignments.remove(index));
        for (int i = index; i < assignments.size(); i++) {
            assignments.get(i).shift(i-1);
        }
    }

    public ArrayList<Grade> getGrades(Student student) {
        ArrayList<Grade> grades = new ArrayList<>();
        for (Assignment assignment : assignments) {
            grades.add(assignment.getGrade(student));
        }

        return grades;
    }
}