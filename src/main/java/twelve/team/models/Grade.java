package twelve.team.models;

public class Grade implements Commentable {
    private Assignment assignment;
    private Student student;
    private double grade;

    private String comment;

    public Grade(Assignment assignment, Student student, double grade) {
        this.assignment = assignment;
        this.student = student;
        this.grade = grade;
    }

    public static void create(int assignmentID, int studentID, double grade) {

    }

    public static void create(int assignmentID, int studentID) {

    }

    public void updateGrade



    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void editComment(String comment) {
        this.comment = comment;
    }
}
