package ui;

import BookingSystem.*;
import observer.BookingManager;
import observer.DriverObserver;
import observer.PassengerObserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DriverListForm extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String passengerEmail;
    private String pickupPoint;
    private String destination;
    private String dateTime;
    private String taxiType;
    private DefaultTableModel tableModel;
    private JTable driverTable;

    public DriverListForm(String passengerEmail, String pickupPoint, String destination, String dateTime, String taxiType, ResultSet driverResultSet) {
        this.passengerEmail = passengerEmail;
        this.pickupPoint = pickupPoint;
        this.destination = destination;
        this.dateTime = dateTime;
        this.taxiType = taxiType;

        setTitle("Driver List");
        setSize(800, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new Object[]{"Username", "Email", "Taxi Type", "Status", "Action"}, 0);
        try {
            while (driverResultSet.next()) {
                String driverUsername = driverResultSet.getString("username");
                String driverEmail = driverResultSet.getString("email");
                String driverTaxiType = driverResultSet.getString("taxi_type");
                String status = driverResultSet.getString("status");
                tableModel.addRow(new Object[]{driverUsername, driverEmail, driverTaxiType, status, "Book"});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (driverResultSet != null) driverResultSet.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        driverTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only "Action" column is editable
            }
        };

        driverTable.getColumn("Action").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton("Book");
            button.addActionListener(e -> bookDriver(row));
            return button;
        });

        driverTable.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton button = new JButton("Book");
                button.addActionListener(e -> bookDriver(row));
                return button;
            }
        });

        JScrollPane scrollPane = new JScrollPane(driverTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
    }

    private void bookDriver(int row) {
        String driverUsername = (String) tableModel.getValueAt(row, 0);
        String driverEmail = (String) tableModel.getValueAt(row, 1);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO bookings (passenger_email, pickup_point, destination, date_time, taxi_type, status, driver_email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, passengerEmail);
            pstmt.setString(2, pickupPoint);
            pstmt.setString(3, destination);
            pstmt.setString(4, dateTime);
            pstmt.setString(5, taxiType);
            pstmt.setString(6, "Pending");
            pstmt.setString(7, driverEmail);
            pstmt.executeUpdate();

            // Register and notify observers
            BookingManager bookingManager = BookingManager.getInstance();
            PassengerObserver passengerObserver = new PassengerObserver(passengerEmail);
            DriverObserver driverObserver = new DriverObserver(driverEmail);
            bookingManager.registerObserver(passengerObserver);
            bookingManager.registerObserver(driverObserver);
            bookingManager.notifyObservers("New booking request from " + passengerEmail + " to " + driverUsername);

            JOptionPane.showMessageDialog(this, "Booking request sent to " + driverUsername);
            dispose();
            new PassengerHomePage(passengerEmail).setVisible(true); // Open passenger home page
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT username, email, taxi_type, status FROM drivers";
            ResultSet rs = conn.createStatement().executeQuery(query);
            SwingUtilities.invokeLater(() -> new DriverListForm("passenger@example.com", "Point A", "Point B", "2022-12-31 10:00:00", "Sedan", rs).setVisible(true));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
