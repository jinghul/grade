package twelve.team;

import java.sql.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    /* Connection Config */
    private static final String DATABASE_HOST = "jdbc:mysql://localhost/";
    private static final String DATABASE_NAME = "gradesystem";

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
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to MySQL server...");
            connection = DriverManager.getConnection(DATABASE_HOST, USERNAME, PASSWORD);
            Statement statement = connection.createStatement();

            // Check if the database exists on the server...
            try (ResultSet resultSet = connection.getMetaData().getCatalogs()) {
                while (resultSet.next()) {
                    String databaseName = resultSet.getString(1);
                    if (databaseName.equals(DATABASE_NAME)) {
                        System.out.println("\'" + DATABASE_NAME + "\' successfully found on server, connecting...\n");
                        connection = DriverManager.getConnection(DATABASE_HOST + DATABASE_NAME, USERNAME, PASSWORD);
                        return;
                    }
                }
            }

            /* Database was not found, have to create the database and tables. */
            System.out.println("Database \'" + DATABASE_NAME + "\' not found, creating...");

            String sql = "CREATE DATABASE " + DATABASE_NAME + ";";
            statement.executeUpdate(sql);

            sql = "USE " + DATABASE_NAME + ";";
            statement.execute(sql);
            
            sql = ("create table teacher(\n"
                    + "teacherID int NOT NULL AUTO_INCREMENT,\n"
                    + "username varchar(32) NOT NULL,\n"
                    + "password varchar(32) NOT NULL,\n"
                    + "teacherName varchar(20) NOT NULL,\n"
                    + "PRIMARY KEY (teacherID)\n"
                    + ");");
            statement.execute(sql);

            // Make username and password case sensitive
            sql = "ALTER TABLE teacher modify username varchar(32) NOT NULL COLLATE utf8mb4_bin";
            statement.execute(sql);
            sql = "ALTER TABLE teacher modify password varchar(32) NOT NULL COLLATE utf8mb4_bin";
            statement.execute(sql);
            System.out.println("\tTable teacher created successfully...");

            sql = ("create table semester(\n"
                    + "semesterID int NOT NULL AUTO_INCREMENT,\n"
                    + "teacherID int NOT NULL,\n"
                    + "semesterName varchar(32) NOT NULL,\n"
                    + "semesterYear int NOT NULL,\n"
                    + "PRIMARY KEY (semesterID),\n"
                    + "FOREIGN KEY (teacherID) references teacher(teacherID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable semester created successfully...");

            sql = ("create table course (\n"
                    + "courseID int NOT NULL AUTO_INCREMENT,\n"
                    + "courseDepartment varchar(32) NOT NULL,\n"
                    + "courseNumber int NOT NULL,\n"
                    + "courseCode varchar(32),\n"
                    + "courseName varchar(255),\n"
                    + "courseDescription TEXT,\n"
                    + "semesterID int NOT NULL,\n"
                    + "PRIMARY KEY (courseID),\n"
                    + "FOREIGN KEY (semesterID) references semester(semesterID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable course created successfully...");

            sql = ("create table section (\n"
                    + "sectionID int NOT NULL AUTO_INCREMENT,\n"
                    + "sectionCode varchar(32),\n"
                    + "courseID int NOT NULL,\n"
                    + "PRIMARY KEY (sectionID),\n"
                    + "FOREIGN KEY (courseID) references course(courseID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable section created successfully...");

            sql = ("create table student (\n"
                    + "studentID int NOT NULL AUTO_INCREMENT,\n"
                    + "universityID varchar(10) NOT NULL,\n"
                    + "studentName varchar(32) NOT NULL,\n"
                    + "degree int NOT NULL,\n"
                    + "comment TEXT,\n"
                    + "sectionID int NOT NULL,\n"
                    + "PRIMARY KEY (studentID),\n"
                    + "FOREIGN KEY (sectionID) REFERENCES section(sectionID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable student created successfully...");

            // TODO: Custom weights

            sql = ("create table category(\n"
                    + "categoryID int NOT NULL AUTO_INCREMENT,\n"
                    + "place int NOT NULL,\n"
                    + "categoryName varchar(32) NOT NULL,\n"
                    + "weightUG double not NULL default 0.0,\n"
                    + "weightGR double not NULL default 0.0,\n"
                    + "isDefault BOOLEAN not NULL default 0,\n"
                    + "courseID int NOT NULL,\n"
                    + "PRIMARY KEY (categoryID),\n"
                    + "FOREIGN KEY (courseID) REFERENCES course(courseID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable category created successfully...");

            sql = ("create table assignment(\n"
                    + "assignmentID int NOT NULL AUTO_INCREMENT,\n"
                    + "place int NOT NULL,\n"
                    + "assignmentName varchar(50) NOT NULL,\n"
                    + "totalPoints double NOT NULL,\n"
                    + "comment TEXT,\n"
                    + "optional BOOLEAN not NULL default 0,\n"
                    + "extraCredit BOOLEAN not NULL default 0,\n"
                    + "weightUG double not NULL default 0.0,\n"
                    + "weightGR double not NULL default 0.0,\n"
                    + "startDate DATE,\n"
                    + "endDate DATE,\n"
                    + "categoryID int NOT NULL,\n"
                    + "PRIMARY KEY (assignmentID),\n"
                    + "FOREIGN KEY (categoryID) REFERENCES category(categoryID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable assignment created successfully...");

            sql = ("create table grade (\n"
                    + "studentID int NOT NULL,\n"
                    + "assignmentID int NOT NULL,\n"
                    + "comment TEXT,\n"
                    + "graded BOOLEAN not NULL default 0,\n"
                    + "grade int NOT NULL default 0,\n"
                    + "foreign key (studentID) REFERENCES student(studentID),\n"
                    + "foreign key (assignmentID) REFERENCES assignment(assignmentID)\n"
                    + ");");
            statement.execute(sql);
            System.out.println("\tTable grade created successfully...");

            System.out.println("Database \'" + DATABASE_NAME + "\' successfully created.\n");
            connection = DriverManager.getConnection(DATABASE_HOST + DATABASE_NAME, USERNAME, PASSWORD);

            // Close statement
            statement.close();
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Database.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Statement getStatement() {
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PreparedStatement prepareStatementWithKeyReturn(String query) {
        try {
            return connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public PreparedStatement prepareStatement(String query) {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
