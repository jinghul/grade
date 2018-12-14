package twelve.team.models;

import java.util.ArrayList;
import java.util.HashMap;

public interface Gradable {
    ArrayList<Grade> getGrades();
    double getAverage();
    HashMap<String, String> getStatistics();
}
