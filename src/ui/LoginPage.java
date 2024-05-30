package ui;
import BookingSystem.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;


/**
 * The {@code LoginPage} class represents the login page of the application.
 * It uses the Observer pattern to notify observers about the success or failure of login attempts.
 */
public class LoginPage extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // List of observers to be notified of login events
    private final List<LoginObserver> observers = new ArrayList<>();

    private JLabel statusLabel;

    /**
     * Constructor to set up the login page UI.
     */
    public LoginPage() {
        setTitle("Login Page");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);

        JTextField emailField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");
        
        // Adjust grid bag constraints for the buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        panel.add(loginButton, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        panel.add(signUpButton, gbc);
        
        statusLabel = new JLabel("");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        // Action listener for the sign-up button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the sign-up page when the button is clicked
                SignupPage signUpPage = SignupPage.getInstance();
                signUpPage.setVisible(true);
                dispose(); // Optionally close the login page
            }
        });

        // Action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
        
                // Validate input
                if (email.isEmpty() || password.isEmpty()) {
                    statusLabel.setText("Please fill in all fields");
                    return;
                }
        
                // Hash the password
                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    statusLabel.setText("Error hashing password");
                    return;
                }
        
                // Perform login operation
                try {
                    Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    String passengerQuery = "SELECT * FROM passenger WHERE email=? AND password=?";
                    String driverQuery = "SELECT * FROM driver WHERE email=? AND password=?";
                    PreparedStatement passengerPstmt = conn.prepareStatement(passengerQuery);
                    PreparedStatement driverPstmt = conn.prepareStatement(driverQuery);
        
                    // Set parameters for passenger query
                    passengerPstmt.setString(1, email);
                    passengerPstmt.setString(2, hashedPassword);
        
                    // Set parameters for driver query
                    driverPstmt.setString(1, email);
                    driverPstmt.setString(2, hashedPassword);
        
                    ResultSet passengerRs = passengerPstmt.executeQuery();
                    ResultSet driverRs = driverPstmt.executeQuery();
        
                    if (passengerRs.next()) {
                        // Login successful as passenger
                        notifyObservers("Passenger");
                        JOptionPane.showMessageDialog(LoginPage.this, "Login successful as passenger", "Success", JOptionPane.INFORMATION_MESSAGE);
                        PassengerHomePage passengerHomePage = new PassengerHomePage(email); // Pass username to the home page
                        passengerHomePage.setVisible(true);
                        dispose(); // Close the login page
                    } else if (driverRs.next()) {
                        // Check if taxi type is set for driver
                        String taxiType = driverRs.getString("taxi_type");
                        if (taxiType == null || taxiType.isEmpty()) {
                            // Taxi type not set, redirect to profile update
                            notifyObservers("Driver");
                            JOptionPane.showMessageDialog(LoginPage.this, "Please update your profile with Taxi type and Account number:", "Profile Update Needed", JOptionPane.INFORMATION_MESSAGE);
                            DriverProfileUpdateForm profileUpdateForm = new DriverProfileUpdateForm(email); // Pass email to the profile update form
                            profileUpdateForm.setVisible(true);
                        } else {
                            // Taxi type is set, proceed to driver home page
                            notifyObservers("Driver");
                            JOptionPane.showMessageDialog(LoginPage.this, "Login successful as driver", "Success", JOptionPane.INFORMATION_MESSAGE);
                            DriverHomePage driverHomePage = new DriverHomePage(email); // Pass username to the home page
                            driverHomePage.setVisible(true);
                        }
                        dispose(); // Close the login page
                    } else {
                        // Login failed
                        JOptionPane.showMessageDialog(LoginPage.this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                        notifyObservers(null);
                    }
        
                    passengerRs.close();
                    passengerPstmt.close();
                    driverRs.close();
                    driverPstmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Error: " + ex.getMessage());
                }
            }
        });
        
        add(panel);
    }

    // Method to hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    }

    public void addObserver(LoginObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LoginObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String userType) {
        for (LoginObserver observer : observers) {
            if (userType != null) {
                observer.onLoginSuccess(userType);
            } else {
                observer.onLoginFailure();
            }
        }
    }

    public static void main(String[] args) {
        LoginPage loginPage = new LoginPage();
        
        // Example observer
        LoginObserver observer = new LoginObserver() {
            @Override
            public void onLoginSuccess(String userType) {
                System.out.println("Login successful as " + userType);
            }

            @Override
            public void onLoginFailure() {
                System.out.println("Login failed");
            }
        };
        
        loginPage.addObserver(observer);
        
        SwingUtilities.invokeLater(() -> loginPage.setVisible(true));
    }
}
