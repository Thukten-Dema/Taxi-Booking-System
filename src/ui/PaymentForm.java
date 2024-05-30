package ui;
import BookingSystem.*;

import strategy.*;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PaymentForm extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String username;
    private String pickupPoint;
    private String destination;
    private String driverEmail;
    private double totalFare;

    private JTextField amountField;
    private JComboBox<String> bankComboBox;
    private JTextField accountNumberField;
    private JTextField bankCodeField;
    private JLabel driverDetailsLabel;

    public PaymentForm(String username, String pickupPoint, String destination, String driverEmail, double totalFare) {
        this.username = username;
        this.pickupPoint = pickupPoint;
        this.destination = destination;
        this.driverEmail = driverEmail;
        this.totalFare = totalFare;

        setTitle("Payment Form");
        setSize(400, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel driverDetailsPanel = new JPanel(new GridLayout(3, 1));
        driverDetailsPanel.setBorder(BorderFactory.createTitledBorder("Driver Details"));

        driverDetailsLabel = new JLabel();
        updateDriverDetails();
        driverDetailsPanel.add(driverDetailsLabel);
        panel.add(driverDetailsPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2));

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);
        formPanel.add(amountLabel);
        formPanel.add(amountField);

        JLabel bankLabel = new JLabel("Bank:");
        bankComboBox = new JComboBox<>(new String[]{"Bank of Bhutan", "Bhutan National Bank"});
        formPanel.add(bankLabel);
        formPanel.add(bankComboBox);

        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberField = new JTextField(10);
        formPanel.add(accountNumberLabel);
        formPanel.add(accountNumberField);

        JLabel bankCodeLabel = new JLabel("Bank Code:");
        bankCodeField = new JTextField(10);
        formPanel.add(bankCodeLabel);
        formPanel.add(bankCodeField);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton payButton = new JButton("Pay Now");
        payButton.addActionListener(e -> processPayment());
        panel.add(payButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void processPayment() {
        double amount = Double.parseDouble(amountField.getText());
        String bank = (String) bankComboBox.getSelectedItem();
        String accountNumber = accountNumberField.getText();
        String bankCode = bankCodeField.getText();

        PaymentStrategy paymentStrategy = null;
        if ("Bank of Bhutan".equals(bank)) {
            paymentStrategy = new BankOfBhutanPayment(accountNumber, bankCode);
        } else if ("Bhutan National Bank".equals(bank)) {
            paymentStrategy = new BhutanNationalBankPayment(accountNumber, bankCode);
        }

        if (paymentStrategy != null && paymentStrategy.pay(amount)) {
            updateBookingStatus();
            JOptionPane.showMessageDialog(this, "Payment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the PaymentForm

        } else {
            JOptionPane.showMessageDialog(this, "Payment failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDriverDetails() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT username, driver_acc_no FROM driver WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, driverEmail);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String driverUsername = rs.getString("username");
                String driverAccountNumber = rs.getString("driver_acc_no");
                String driverDetailsText = "<html><b>Driver Username:</b> " + driverUsername +
                        "<br><b>Driver Account Number:</b> " + driverAccountNumber +
                        "<br><b>Total Fare:</b> Nu." + String.format("%.2f", totalFare) + "</html>";
                driverDetailsLabel.setText(driverDetailsText);
            } else {
                driverDetailsLabel.setText("Driver details not found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching driver details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBookingStatus() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE bookings SET status = ? WHERE passenger_email = ? AND pickup_point = ? AND destination = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "Completed");
            pstmt.setString(2, username);
            pstmt.setString(3, pickupPoint);
            pstmt.setString(4, destination);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating booking status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
