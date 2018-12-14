package twelve.team.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Assignment implements Commentable, Gradable{

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public void editComment(String comment) {

    }

    @Override
    public ArrayList<Grade> getGrades() {
        return null;
    }

    @Override
    public double getAverage() {
        return 0;
    }

    @Override
    public HashMap<String, String> getStatistics() {
        return null;
    }
}