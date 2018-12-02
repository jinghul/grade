package twelve.team;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    /* Connection Config */
    public static final String DATABASE_HOST = "jdbc:mysql://localhost/";
    public static final String DATABASE_NAME = "gradesystem";

    /* Local Credentials */
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    /* Database Variables */
    private static Database database;
    private Connection connection;

    /**
     * Initialize the database and create a shutdown
     * hook to stop the database connection on exit.
     */
    private Database() {
        initDB();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> close()));
    }

    public static void init() {
        if (database == null) {
            database = new Database();
        }
    }

    /**
     * Gets the singleton Database instance for the Application.
     * @return database
     */
    public static Database getDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }


    private void initDB() {
        String url = DATABASE_HOST;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to MySQL server...");
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();

            // Check if the database exists on the server...
            ResultSet  resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equals(DATABASE_NAME)) {
                    System.out.println("\'" + DATABASE_NAME + "\' successfully found on server, connecting...\n");
                    connection = DriverManager.getConnection(DATABASE_HOST + DATABASE_NAME, USERNAME, PASSWORD);
                    return;
                }
            }

            /* Database was not found, have to create the database and tables. */
            System.out.println("Database \'" + DATABASE_NAME + "\' not found, creating...");

            String sql = "CREATE DATABASE " + DATABASE_NAME + ";";
            statement.executeUpdate(sql);

            sql = "USE " + DATABASE_NAME + ";";
            statement.execute(sql);
            
            sql = ("create table teacher(\n"
                    + "teacherID varchar(20) NOT NULL,\n"
                    + "password varchar(20) NOT NULL,\n"
                    + "teacherName varchar(20) NOT NULL,\n"
                    + "PRIMARY KEY (teacherID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable teacher created successfully...");

            sql = ("create table semester(\n"
                    + "semesterID int NOT NULL,\n"
                    + "teacherID varchar(20) NOT NULL,\n"
                    + "semesterName varchar(20) NOT NULL,\n"
                    + "PRIMARY KEY (semesterID),\n"
                    + "FOREIGN KEY (teacherID) references teacher(teacherID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable semester created successfully...");

            sql = ("create table course (\n"
                    + "courseID int NOT NULL,\n"
                    + "courseName varchar(20) NOT NULL,\n"
                    + "semesterID int NOT NULL,\n"
                    + "teacherID int NOT NULL,\n"
                    + "PRIMARY KEY (courseID),\n"
                    + "FOREIGN KEY (teacherID) references teacher(teacherID),\n"
                    + "FOREIGN KEY (semesterID) references semester(semesterID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable course created successfully...");

            sql = ("create table student (\n"
                    + "studentID int NOT NULL,\n"
                    + "studentName varchar(20) NOT NULL,\n"
                    + "degree int NOT NULL,\n"
                    + "courseID int NOT NULL,\n"
                    + "PRIMARY KEY (studentID),\n"
                    + "FOREIGN KEY (courseID) REFERENCES course(courseID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable student created successfully...");

            sql = ("create table category(\n"
                    + "categoryID int NOT NULL,\n"
                    + "categoryName varchar(20) NOT NULL,\n"
                    + "courseID int NOT NULL,\n"
                    + "PRIMARY KEY (categoryID),\n"
                    + "FOREIGN KEY (courseID) \tREFERENCES course(courseID));");
            statement.execute(sql);
            System.out.println("\tTable category created successfully...");

            sql = ("create table assignment(\n"
                    + "assignmentID int NOT NULL,\n"
                    + "assignmentName varchar(20) NOT NULL,\n"
                    + "totalPoints float,\n"
                    + "extraCredit float,\n"
                    + "tag varchar(10),\n"
                    + "status bit default 0,\n"
                    + "categoryID int NOT NULL,\n"
                    + "PRIMARY KEY (assignmentID),\n"
                    + "FOREIGN KEY (categoryID) REFERENCES category(categoryID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable assignment created successfully...");

            sql = ("create table grade (\n"
                    + "studentID int NOT NULL,\n"
                    + "assignmentID int NOT NULL,\n"
                    + "grade int,\n"
                    + "foreign key (studentID) REFERENCES student(studentID),\n"
                    + "foreign key (assignmentID) REFERENCES assignment(assignmentID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable grade created successfully...");

            sql = ("create table comment (\n"
                    + "studentID int NOT NULL,\n"
                    + "assignmentID int NOT NULL,\n"
                    + "comment TEXT,\n"
                    + "foreign key (studentID) REFERENCES student(studentID),\n"
                    + "foreign key (assignmentID) REFERENCES assignment(assignmentID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable comment created successfully...");

            sql = ("CREATE TABLE weight (\n"
                    + "categoryID int,\n"
                    + "assignmentID int,\n"
                    + "degree int NOT NULL,\n"
                    + "weight float\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable weights created successfully...");
            System.out.println("Database \'" + DATABASE_NAME + "\' successfully created.\n");
            connection = DriverManager.getConnection(DATABASE_HOST + DATABASE_NAME, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace(); // DEBUG
        }
    }
    
    public ResultSet getQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace(); // DEBUG
            return null;
        }
    }
    
    public PreparedStatement prepareStatement(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace(); // DEBUG
            return null;
        }
    }
    
    public boolean execute(String sql) {
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // DEBUG
            return false;
        }
    }

    private void close() {
        try {
            System.out.println("\nClosing database server connection...");
            if (connection != null) {
                connection.close();
            }
            System.out.println("Database \'" + DATABASE_NAME + "\' connection closed.");
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
