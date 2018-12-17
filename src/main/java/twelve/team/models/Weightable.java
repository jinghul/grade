package twelve.team.models;

public interface Weightable {
    double getWeightUG();
    double getWeightGR();
    void setWeightUG(double weight);
    void setWeightGR(double weight);
    void setName(String name);
    String getName();
    void changeWeight(double newWeight, int degree);
    // getWeight(int - weight identifier)
}
