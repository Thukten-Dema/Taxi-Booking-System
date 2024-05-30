package ui;

import BookingSystem.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import BookingSystem.UserFactory;
import factory.PassengerFactory;
import factory.DriverFactory;
import BookingSystem.User;

public class SignupPage extends JFrame {

    private static SignupPage instance;
    private PasswordHasher passwordHasher;
 
    // Private constructor for singleton pattern
    private SignupPage() {
        setTitle("Signup Form");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

         // Create the main panel with grid layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add form fields and labels to the panel
        JLabel nameLabel = new JLabel("Username:");
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();
        panel.add(phoneLabel);
        panel.add(phoneField);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        panel.add(emailLabel);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordLabel);
        panel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);

        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"Passenger", "Driver"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setSelectedIndex(0);
        panel.add(roleLabel);
        panel.add(roleComboBox);

        // Add signup button with action listener
        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(new ActionListener() {
            private PassengerFactory passengerFactory = new PassengerFactory();
            private DriverFactory driverFactory = new DriverFactory();

            @Override
            public void actionPerformed(ActionEvent e) {
                // Gather user input
                String username = nameField.getText();
                String phoneNumber = phoneField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();

                // Validate input
                if (username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!username.matches("[a-zA-Z]+")) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Username must contain only letters", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!phoneNumber.matches("\\d{8}")) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Phone number must contain exactly 8 digits", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Password must be at least 8 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Password and confirm password do not match", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if username or email already exists
                if (userExists(username, email, role)) {
                    JOptionPane.showMessageDialog(SignupPage.this, "Username or email already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Hash the password
                String hashedPassword = passwordHasher.hashPassword(password);

                // Create the user object using the factory
                User user;
                if (role.equals("Passenger")) {
                    user = passengerFactory.createUser(username, phoneNumber, email, hashedPassword);
                } else {
                    user = driverFactory.createUser(username, phoneNumber, email, hashedPassword);
                }

                // Perform database operations
                try {
                    Connection conn = DatabaseConnection.getInstance().getConnection();
                    String insertQuery;
                    if (role.equals("Passenger")) {
                        insertQuery = "INSERT INTO passenger (username, phone_number, email, password) VALUES (?, ?, ?, ?)";
                    } else {
                        insertQuery = "INSERT INTO driver (username, phone_number, email, password) VALUES (?, ?, ?, ?)";
                    }
                    PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                    pstmt.setString(1, user.getUsername());
                    pstmt.setString(2, user.getPhoneNumber());
                    pstmt.setString(3, user.getEmail());
                    pstmt.setString(4, user.getHashedPassword());
                    pstmt.executeUpdate();
                    pstmt.close();

                    JOptionPane.showMessageDialog(SignupPage.this, "Signup successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Redirect to login page
                    dispose();
                    LoginPage loginPage = new LoginPage();
                    loginPage.setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(SignupPage.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(signupButton);

        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Redirect to login page
                dispose(); // Close the current window
                LoginPage loginPage = new LoginPage(); // Create a new login page
                loginPage.setVisible(true); // Show the login page
            }
        });
        panel.add(loginLabel);

        add(panel);

        // Set the default password hasher
        passwordHasher = new SHA256PasswordHasher();
    }

    // Singleton pattern to ensure only one instance of SignupPage exists
    public static synchronized SignupPage getInstance() {
        if (instance == null) {
            instance = new SignupPage();
        }
        return instance;
    }

     // Method to check if a user with the given username or email already exists
    private boolean userExists(String username, String email, String role) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            String query;
            if (role.equals("Passenger")) {
                query = "SELECT COUNT(*) FROM passenger WHERE username = ? OR email = ?";
            } else {
                query = "SELECT COUNT(*) FROM driver WHERE username = ? OR email = ?";
            }
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                pstmt.close();
                return count > 0;
            }
            rs.close();
            pstmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignupPage.getInstance().setVisible(true);
        });
    }
}
