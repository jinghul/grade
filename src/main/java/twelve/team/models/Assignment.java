package twelve.team.models;

import twelve.team.Database;
import twelve.team.controllers.MainPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class Assignment implements Commentable, Gradable, Weightable {

    private int categoryID;

    private int index;
    private final int assignmentID;
    private String assignmentName;
    private double totalPoints;
    private boolean optional;
    private boolean extracredit;
    private double weightUG;
    private double weightGR;
    private Date startDate;
    private Date endDate;
    private String comment;
    private HashMap<Section, ArrayList<Grade>> grades;

    public Assignment(int assignmentID, String assignmentName, double totalPoints,
                      boolean optional, boolean extracredit, double weightUG,
                      double weightGR, Date startDate, Date endDate,
                      String comment, int index, int categoryID) {
        this.assignmentID = assignmentID;
        this.assignmentName = assignmentName;
        this.totalPoints = totalPoints;
        this.optional = optional;
        this.extracredit = extracredit;
        this.weightUG = weightUG;
        this.weightGR = weightGR;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
        this.index = index;
        this.categoryID = categoryID;
    }

    public static Assignment create(String assignmentName, double totalPoints,
                                    boolean optional, boolean extracredit, double weightUG,
                                    double weightGR, Date startDate, Date endDate,
                                    String comment, int index, int categoryID) {
        String query = "insert into assignment (assignmentName, totalPoints, optional, extracredit, weightUG, weightGR, startDate, endDate, comment, place, categoryID)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, assignmentName);
            prpst.setDouble(2, totalPoints);
            prpst.setBoolean(3, optional);
            prpst.setBoolean(4, extracredit);
            prpst.setDouble(5, weightUG);
            prpst.setDouble(6, weightGR);
            prpst.setDate(7, new java.sql.Date(startDate.getTime()));
            prpst.setDate(8, new java.sql.Date(endDate.getTime()));
            prpst.setString(9, comment);
            prpst.setInt(10, index);
            prpst.setInt(11, categoryID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    return new Assignment(key.getInt(1), assignmentName, totalPoints,
                            optional, extracredit, weightUG,
                            weightGR, startDate, endDate,
                            comment, index, categoryID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Assignment> getAssignments(int categoryID) {
        String query = String.format("select * from assignment where categoryID = %d", categoryID);

        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                ArrayList<Assignment> assignments = new ArrayList<>();
                while (result.next()) {
                    assignments.add(new Assignment(result.getInt("assignmentID"), result.getString("assignmentName"),
                            result.getDouble("totalPoints"), result.getBoolean("optional"),
                            result.getBoolean("extracredit"), result.getDouble("weightUG"),
                            result.getDouble("weightGR"), new Date((result.getDate("startDate")).getTime()),
                            new Date((result.getDate("endDate")).getTime()), result.getString("comment"),
                            result.getInt("place"), result.getInt("categoryID")));
                }

                assignments.sort(Comparator.comparingInt(Assignment::getIndex));
                return assignments;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clear(ArrayList<Assignment> assignments) {
        if(assignments.size() == 0) {
            return;
        }

        ArrayList<String> ids = new ArrayList<>();
        for (Assignment assignment : assignments) {
            ids.add(String.valueOf(assignment.getAssignmentID()));
        }

        Grade.clearAssignments(ids);
        String query = String.format("delete from assignment where assignmentID in (%s)", String.join(",", ids));
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void delete(Assignment assignment) {
        Grade.clearAssignment(assignment.getAssignmentID());
        String query = String.format("delete from assignment where assignmentID = %d", assignment.assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getAssignmentID() {
        return assignmentID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getName() {
        return assignmentName;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isExtracredit() {
        return extracredit;
    }

    public int getIndex() {
        return index;
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
    public void setName(String name) {
        String update = String.format("update assignment set assignmentName = '%s' where assignmentID = %d", name, assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.assignmentName = name;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWeightUG(double weightUG) {
        String update = String.format("update assignment set weightUG = %f where assignmentID = %d", weightUG, assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.weightUG = weightUG;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setWeightGR(double weightGR) {
        String update = String.format("update assignment set weightGR = %f where assignmentID = %d", weightGR, assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.weightGR = weightGR;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void editComment(String comment) {
        String update = String.format("update assignment set comment = '%s' where assignmentID = %d", comment, assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.comment = comment;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isGradeDue(java.util.Date date) {
        return date.compareTo(endDate) > 0;
    }

    public boolean isActive(java.util.Date date) {
        return date.compareTo(startDate) >= 1 && date.compareTo(endDate) < 1;
    }

    public void update(String assignmentName, int totalPoints,
                       boolean optional, boolean extracredit, double weightUG,
                       double weightGR, Date startDate, Date endDate,
                       String comment, int index) {
        String update = "update assignment set assignmentName = ?, totalPoints = ?, optional = ?, " +
                "extracredit = ?, weightUG = ?, weightGR = ?, startDate = ?, endDate = ?, comment = ?, place = ? where assignmentID = ?";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatement(update)) {
            prpst.setString(1, assignmentName);
            prpst.setDouble(2, totalPoints);
            prpst.setBoolean(3, optional);
            prpst.setBoolean(4, extracredit);
            prpst.setDouble(5, weightUG);
            prpst.setDouble(6, weightGR);
            prpst.setDate(7, new java.sql.Date(startDate.getTime()));
            prpst.setDate(8, new java.sql.Date(endDate.getTime()));
            prpst.setString(9, comment);
            prpst.setInt(10, index);
            prpst.setInt(11, assignmentID);
            prpst.executeUpdate();

            this.assignmentName = assignmentName;
            this.totalPoints = totalPoints;
            this.optional = optional;
            this.extracredit = extracredit;
            this.weightUG = weightUG;
            this.weightGR = weightGR;
            this.startDate = startDate;
            this.endDate = endDate;
            this.comment = comment;
            this.index = index;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeWeight(double newWeight, int degree) {
        if (degree == 0) {
            setWeightUG(newWeight);
        } else {
            setWeightGR(newWeight);
        }
    }

    public void shift(int newIndex) {
        String update = String.format("update assignment set place = %d where assignmentID = %d", newIndex, assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.index = newIndex;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void move(int categoryID, int newIndex) {
        String update = String.format("update assignment set place = %d, categoryID = %d where assignmentID = %d", newIndex, categoryID, assignmentID);
        try (Statement statement = Database.getDatabase().getStatement()) {
            this.categoryID = categoryID;
            this.index = newIndex;
            statement.executeUpdate(update);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Grade getGrade(Student student) {
        String query = String.format("select * from grade where assignmentID = %d AND studentID = %d", assignmentID, student.getStudentID());
        try (Statement statement = Database.getDatabase().getStatement()) {
            try (ResultSet result = statement.executeQuery(query)) {
                if (result.next()) {
                    return new Grade(this, student, result.getDouble("grade"), result.getString("comment"), result.getBoolean("graded"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<Section, ArrayList<Grade>> getGrades() {
        grades = new HashMap<>();
        for (Section section : MainPane.teacher.getCurrentCourse().getSections()) {
            grades.put(section, section.getGrades(this));
        }
        return grades;
    }

    public double getAverage() {
        if (grades == null) grades = getGrades();
        double average = 0;
        for (Section section : grades.keySet()) {
            for (Grade grade : grades.get(section)) {
                if (!grade.isGraded()) {
                    continue;
                }

                average += grade.getGrade() < 0 ? (totalPoints + grade.getGrade()) / totalPoints : grade.getGrade() / totalPoints;
            }
        }

        return average;
    }

    public double getAverage(Section section) {
        if (grades == null) grades = getGrades();

        double average = 0;
        for (Grade grade : grades.get(section)) {
            if (!grade.isGraded()) {
                continue;
            }

            average += grade.getGrade() < 0 ? (totalPoints + grade.getGrade()) / totalPoints : grade.getGrade() / totalPoints;
        }

        return average;
    }
}