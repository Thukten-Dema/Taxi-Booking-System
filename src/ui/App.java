package ui;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection
            String url = "jdbc:mysql://localhost:3306/taxi_booking?useSSL=false&serverTimezone=UTC";
            String username = "root";
            String password = "";
            Connection connection = DriverManager.getConnection(url, username, password);

            // Connection successful
            System.out.println("Connected to the database!");

            // Use the connection here...

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed.");
            e.printStackTrace();
        }
    }
}
