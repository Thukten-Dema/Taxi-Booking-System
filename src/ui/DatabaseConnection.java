package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// The single instance of the DatabaseConnection class
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Private constructor to prevent instantiation from other classes.
     * This constructor initializes the database connection.
     */
    private DatabaseConnection() {
        try {
             // Establish the database connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     /**
     * Provides the global point of access to the single instance of
     * the {@code DatabaseConnection} class. This method is synchronized
     * to ensure thread safety.
     *
     * @return the single instance of {@code DatabaseConnection}
     */

    public static synchronized DatabaseConnection getInstance() {
         // Create the instance if it doesn't exist
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
