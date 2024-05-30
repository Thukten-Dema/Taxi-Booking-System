package ui;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * BookingForm class for handling taxi bookings.
 */
public class BookingForm extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /**
     * Constructor for BookingForm.
     * @param passengerEmail the email of the passenger
     */
    public BookingForm(String passengerEmail) {
        setTitle("Booking Form");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Pickup Point:"));
        JTextField pickupPointField = new JTextField();
        panel.add(pickupPointField);

        panel.add(new JLabel("Destination:"));
        JTextField destinationField = new JTextField();
        panel.add(destinationField);

        panel.add(new JLabel("Date/Time (yyyy-MM-dd HH:mm):"));
        JTextField dateTimeField = new JTextField();
        panel.add(dateTimeField);

        panel.add(new JLabel("Taxi Type:"));
        JComboBox<String> taxiTypeComboBox = new JComboBox<>(new String[]{"four seated", "seven seated", "Eve"});
        panel.add(taxiTypeComboBox);

        JButton submitButton = new JButton("Search");
        panel.add(submitButton);

         // Add action listener for the submit button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pickupPoint = pickupPointField.getText();
                String destination = destinationField.getText();
                String dateTime = dateTimeField.getText();
                String taxiType = (String) taxiTypeComboBox.getSelectedItem();

                if (!isValidDateTime(dateTime)) {
                    JOptionPane.showMessageDialog(BookingForm.this, "Invalid date/time format.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (pickupPoint.isEmpty() || destination.isEmpty() || dateTime.isEmpty() || taxiType.isEmpty()) {
                    JOptionPane.showMessageDialog(BookingForm.this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!pickupPoint.matches("[a-zA-Z ]+") || !destination.matches("[a-zA-Z ]+")) {
                    JOptionPane.showMessageDialog(BookingForm.this, "Pickup Point and Destination must contain only letters and spaces.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                 // Database operation to find available drivers
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String query = "SELECT username, email, taxi_type, status FROM driver WHERE status = 'available' AND taxi_type = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    pstmt.setString(1, taxiType);
                    ResultSet rs = pstmt.executeQuery();

                    // Check if there are available drivers
                    if (rs.next()) {
                        rs.beforeFirst(); // Reset cursor to before the first row
                        new DriverListForm(passengerEmail, pickupPoint, destination, dateTime, taxiType, rs).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(BookingForm.this, "No drivers available for the selected taxi type.", "No Drivers", JOptionPane.INFORMATION_MESSAGE);
                    }                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(BookingForm.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(panel);
    }

    private boolean isValidDateTime(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false);
        try {
            sdf.parse(dateTime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String exampleEmail = "example@example.com";
        SwingUtilities.invokeLater(() -> new BookingForm(exampleEmail).setVisible(true));
    }
}
