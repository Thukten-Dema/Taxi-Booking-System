package ui;
import BookingSystem.*;

import command.AcceptBookingCommand;
import command.CancelBookingCommand;
import BookingSystem.Command;
import command.CommandInvoker;
import observer.BookingManager;
import observer.ButtonEditor;
import observer.ButtonRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DriverHomePage extends JFrame implements Observer {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String driverEmail;
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private CommandInvoker commandInvoker; // Command Invoker to manage command execution

    public DriverHomePage(String driverEmail) {
        this.driverEmail = driverEmail;
        setTitle("Driver Home Page");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome back, " + driverEmail + "!");
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel bookingPanel = new JPanel(new BorderLayout());
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Bookings"));

        // Initialize bookingTable before setting row height
        String[] columns = {"Pickup Point", "Destination", "Date/Time", "Taxi Type", "Status", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only "Actions" column is editable
            }
        };
        bookingTable = new JTable(tableModel);

        // Set the row height to 60 pixels
        bookingTable.setRowHeight(60);

        bookingTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        bookingTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        bookingPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        panel.add(bookingPanel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout"); // Create a logout button
        logoutButton.setPreferredSize(new Dimension(80, 30)); // Set preferred size
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform logout operation
                int choice = JOptionPane.showConfirmDialog(DriverHomePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // Close the current window and open the login page
                    LoginPage loginPage = new LoginPage();
                    loginPage.setVisible(true);
                    dispose(); // Close the driver home page
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Right-aligned layout
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.SOUTH); // Add the button panel to the bottom of the panel

        add(panel);

        BookingManager.getInstance().registerObserver(this);

        fetchBookings(driverEmail);

        commandInvoker = new CommandInvoker(); // Initialize Command Invoker
    }

    public void fetchBookings(String driverEmail) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT pickup_point, destination, date_time, taxi_type, status FROM bookings WHERE driver_email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, driverEmail);
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);

            while (rs.next()) {
                String pickupPoint = rs.getString("pickup_point");
                String destination = rs.getString("destination");
                String dateTime = rs.getString("date_time");
                String taxiType = rs.getString("taxi_type");
                String status = rs.getString("status");
                tableModel.addRow(new Object[]{pickupPoint, destination, dateTime, taxiType, status, "Actions"});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching booking details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void acceptBooking(int row) {
        executeBookingCommand(new AcceptBookingCommand(this, row));
    }

    public void cancelBooking(int row) {
        executeBookingCommand(new CancelBookingCommand(this, row));
    }

    private void executeBookingCommand(Command command) {
        commandInvoker.addCommand(command);
        commandInvoker.executeCommands();
    }

    public void updateBookingStatus(int row, String status) {
        String pickupPoint = (String) tableModel.getValueAt(row, 0);
        String destination = (String) tableModel.getValueAt(row, 1);
        String dateTime = (String) tableModel.getValueAt(row, 2);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE bookings SET status = ? WHERE driver_email = ? AND pickup_point = ? AND destination = ? AND date_time = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, status);
            pstmt.setString(2, driverEmail);
            pstmt.setString(3, pickupPoint);
            pstmt.setString(4, destination);
            pstmt.setString(5, dateTime);
            pstmt.executeUpdate();

            tableModel.setValueAt(status, row, 4);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating booking status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(String message) {
        JOptionPane.showMessageDialog(this, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
        fetchBookings(driverEmail); // Refresh bookings on notification
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DriverHomePage("driver@example.com").setVisible(true));
    }
}
