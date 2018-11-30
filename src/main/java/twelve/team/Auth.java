package twelve.team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    public static boolean authenticate(String teacherId, String password) {
        Database db = Database.getDatabase();
        try {
            PreparedStatement prpst = db.prepareStatement("select * from teacher where teacherID = ? and password = ?");
            prpst.setString(1, teacherId);
            prpst.setString(2, password);
            ResultSet result = prpst.executeQuery();
        
            if (result.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // DEBUG
            return false;
        }
    }
}
