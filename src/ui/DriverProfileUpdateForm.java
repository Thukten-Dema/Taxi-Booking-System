package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DriverProfileUpdateForm extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String driverEmail;
    private JComboBox<String> taxiTypeComboBox;
    private JTextField accountNumberField; // New field for account number

    public DriverProfileUpdateForm(String driverEmail) {
        this.driverEmail = driverEmail;

        setTitle("Update Profile");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10)); // Updated to accommodate the new field
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        JLabel emailLabel = new JLabel(driverEmail);
        panel.add(emailLabel);

        panel.add(new JLabel("Taxi Type:"));
        String[] taxiTypes = {"four seated", "seven seated", "Ev taxi"};
        taxiTypeComboBox = new JComboBox<>(taxiTypes);
        panel.add(taxiTypeComboBox);

        // New field for account number
        panel.add(new JLabel("Account Number:"));
        accountNumberField = new JTextField();
        panel.add(accountNumberField);

        JButton updateButton = new JButton("Update");
        panel.add(updateButton);

        JLabel statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(statusLabel);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTaxiType = (String) taxiTypeComboBox.getSelectedItem();
                String accountNumber = accountNumberField.getText(); // Retrieve account number
                updateDriverProfile(driverEmail, selectedTaxiType, accountNumber); // Updated method call
                statusLabel.setText("Profile updated successfully!");
                JOptionPane.showMessageDialog(DriverProfileUpdateForm.this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the profile update form
                DriverHomePage driverHomePage = new DriverHomePage(driverEmail); // Open the driver's home page
                driverHomePage.setVisible(true);
            }
        });

        add(panel);
    }

    private void updateDriverProfile(String email, String taxiType, String accountNumber) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "UPDATE driver SET taxi_type = ?, driver_acc_no = ? WHERE email = ?"; // Updated query
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, taxiType);
            pstmt.setString(2, accountNumber); // Set account number parameter
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to calculate total fare (for demonstration purposes)
    private double calculateTotalFare() {
        // Generate a random total fare between 10 and 100
        return Math.random() * 90 + 10;
    }

    public static void main(String[] args) {
        // Example driver email, replace this with the actual logged-in email
        String exampleDriverEmail = "driver@example.com";
        SwingUtilities.invokeLater(() -> new DriverProfileUpdateForm(exampleDriverEmail).setVisible(true));
    }
}
