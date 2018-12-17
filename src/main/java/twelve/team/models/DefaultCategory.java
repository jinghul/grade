package twelve.team.models;

import twelve.team.Database;
import twelve.team.controllers.MainPane;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultCategory extends Category {

    public DefaultCategory(int categoryID, String categoryName, double weightGR, double weightUG, int index, int courseID) {
        super(categoryID, categoryName, weightGR, weightUG, index, courseID);
    }

    public static Category create(String categoryName, double weightGR, double weightUG, int index, int courseID) {
        String query = "insert into category (categoryName, weightGR, weightUG, isDefault, place, courseID) values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement prpst = Database.getDatabase().prepareStatementWithKeyReturn(query)) {
            prpst.setString(1, categoryName);
            prpst.setDouble(2, weightGR);
            prpst.setDouble(3, weightUG);
            prpst.setBoolean(4, true); // Default
            prpst.setInt(5, index);
            prpst.setInt(6, courseID);
            prpst.executeUpdate();

            try (ResultSet key = prpst.getGeneratedKeys()) {
                if (key.next()) {
                    return new DefaultCategory(key.getInt(1), categoryName, weightGR, weightUG, index, courseID);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void changeWeight(double newWeight, int degree) {
        double oldWeight = degree == 0 ? this.getWeightUG() : this.getWeightGR();
        double change = newWeight / oldWeight;

        super.changeWeight(newWeight, degree);

        for (Assignment assignment : super.getAssignments()) {
            assignment.changeWeight(degree == 0 ? change * assignment.getWeightUG() : change * assignment.getWeightGR(), degree);
        }
    }

    @Override
    public boolean isDefault() {
        return true;
    }
}
