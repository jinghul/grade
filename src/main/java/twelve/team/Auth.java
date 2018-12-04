package twelve.team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    public static boolean authenticate(String teacherId, String password) {
        Database db = Database.getDatabase();
        String query = "select * from teacher where username = ? and password = ?";
        try (PreparedStatement prpst = db.prepareStatement(query)){
            prpst.setString(1, teacherId);
            prpst.setString(2, password);
            try (ResultSet result = prpst.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
