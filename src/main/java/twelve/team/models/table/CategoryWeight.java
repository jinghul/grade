package twelve.team.models.table;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
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

    public void refresh() {
        this.name = new SimpleStringProperty(weightable.getName());
        this.weightUG = new SimpleStringProperty(String.valueOf(weightable.getWeightUG()));
        this.weightGR = new SimpleStringProperty(String.valueOf(weightable.getWeightGR()));
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
