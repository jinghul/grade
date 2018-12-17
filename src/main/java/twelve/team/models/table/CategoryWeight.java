package twelve.team.models.graphic;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import sun.applet.Main;
import twelve.team.controllers.MainPane;
import twelve.team.models.Category;
import twelve.team.models.Weightable;

public class CategoryWeight extends RecursiveTreeObject<CategoryWeight> {

    private Weightable weightable;
    private boolean isCategory;

    private SimpleStringProperty name;
    private SimpleStringProperty weightUG;
    private SimpleStringProperty weightGR;

    public CategoryWeight(Weightable weightable, boolean isCategory) {
        this(weightable.getName(), weightable.getWeightUG(), weightable.getWeightGR());
        this.weightable = weightable;
        this.isCategory = isCategory;
    }

    public CategoryWeight(String name, double weightUG, double weightGR) {
        this.name = new SimpleStringProperty(name);
        this.weightUG = new SimpleStringProperty(String.valueOf(weightUG));
        this.weightGR = new SimpleStringProperty(String.valueOf(weightGR));
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public SimpleStringProperty getWeightUG() {
        return weightUG;
    }

    public SimpleStringProperty getWeightGR() {
        return weightGR;
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
        weightable.setName(name);
    }

    public void setWeightUG(double weightUG) {
        weightable.setWeightUG(weightUG);
//        if (isCategory) {
//            MainPane.teacher.getCurrentCourse().recalculateWeights((Category) weightable, weightUG, 0);
//        } else {
//            weightable.setWeightUG(weightUG);
//        }
    }
    public void setWeightGR(double weightGR) {
        weightable.setWeightGR(weightGR);
//        if (isCategory) {
//            MainPane.teacher.getCurrentCourse().recalculateWeights((Category) weightable, weightGR, 1);
//        } else {
//            weightable.setWeightGR(weightGR);
//        }
    }

    public Category getCategory() {
        if (isCategory) return (Category) weightable;
        else return null;
    }

}
