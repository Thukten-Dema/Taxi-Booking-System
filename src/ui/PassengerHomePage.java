package ui;
import BookingSystem.Observer;

import observer.BookingManager;
import state.BookingContext;
import state.BookingState;
import state.PendingState;
import state.AcceptedState;
import state.CompletedState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PassengerHomePage extends JFrame implements Observer {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/taxi_booking";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private String username;
    private JTable statusTable;
    private DefaultTableModel tableModel;
    private BookingContext bookingContext;
    private String lastStatus;

    public PassengerHomePage(String username) {
        this.username = username;
        setTitle("Passenger Home Page");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome back, " + username + "!");
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new BorderLayout());

        String[] columns = {"Pickup Point", "Destination", "Date/Time", "Taxi Type", "Status", "Driver Email", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only the Action column is editable
            }
        };
        statusTable = new JTable(tableModel);
        statusTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        statusTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), username, statusTable));
        statusPanel.add(new JScrollPane(statusTable), BorderLayout.CENTER);

        panel.add(statusPanel, BorderLayout.CENTER);

        JButton bookNowButton = new JButton("Book Now");
        bookNowButton.addActionListener(e -> {
            new BookingForm(username).setVisible(true);
            dispose();
        });
        panel.add(bookNowButton, BorderLayout.SOUTH);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(80, 30));
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(PassengerHomePage.this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        add(panel);
        BookingManager.getInstance().registerObserver(this);
        bookingContext = new BookingContext();
        fetchBookingDetails();
    }

    public void fetchBookingDetails() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT pickup_point, destination, date_time, taxi_type, status, driver_email FROM bookings WHERE passenger_email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);
            boolean stateChanged = false;

            while (rs.next()) {
                String pickupPoint = rs.getString("pickup_point");
                String destination = rs.getString("destination");
                String dateTime = rs.getString("date_time");
                String taxiType = rs.getString("taxi_type");
                String status = rs.getString("status");
                String driverEmail = rs.getString("driver_email");

                JButton payButton = new JButton("Pay Now");
                Font buttonFont = payButton.getFont();
                payButton.setFont(new Font(buttonFont.getName(), Font.BOLD, 10));
                payButton.setEnabled("Accepted".equals(status));
                payButton.addActionListener(e -> {
                    int selectedRow = statusTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String selectedPickupPoint = (String) tableModel.getValueAt(selectedRow, 0);
                        String selectedDestination = (String) tableModel.getValueAt(selectedRow, 1);
                        String selectedDriverEmail = (String) tableModel.getValueAt(selectedRow, 5);
                        double totalFare = calculateTotalFare();
                        new PaymentForm(username, selectedPickupPoint, selectedDestination, selectedDriverEmail, totalFare).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(PassengerHomePage.this, "Please select a booking to pay for.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                });

                // Add the row to the table model
                tableModel.addRow(new Object[]{pickupPoint, destination, dateTime, taxiType, status, driverEmail, payButton});

                // Set the state based on the status and check for state changes
                if (!status.equals(lastStatus)) {
                    stateChanged = true;
                    lastStatus = status;
                    switch (status) {
                        case "Pending":
                            bookingContext.setState(new PendingState());
                            break;
                        case "Accepted":
                            bookingContext.setState(new AcceptedState());
                            break;
                        case "Completed":
                            bookingContext.setState(new CompletedState());
                            break;
                    }
                }
            }

            // Display the state message if there was a state change
            if (stateChanged) {
                bookingContext.handle();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching booking details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void update(String message) {
        fetchBookingDetails();
    }

    private double calculateTotalFare() {
        double fare = 50 + Math.random() * (200 - 50);
        return Math.round(fare * 100.0) / 100.0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PassengerHomePage("exampleUser").setVisible(true));
    }
}
