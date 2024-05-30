package ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    private String username;
    private JTable table;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox, String username, JTable table) {
        super(checkBox);
        this.username = username;
        this.table = table;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        JButton button = new JButton();
        button.setText(label);
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                // Trigger payment form
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String pickupPoint = (String) table.getValueAt(selectedRow, 0);
                    String destination = (String) table.getValueAt(selectedRow, 1);
                    String driverEmail = (String) table.getValueAt(selectedRow, 5);
                    double totalFare = calculateTotalFare();

                    // Create PaymentForm instance with additional parameters
                    new PaymentForm(username, pickupPoint, destination, driverEmail, totalFare).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a booking to pay for.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    private double calculateTotalFare() {
        // Generate a random fare between 50 and 200
        double fare = 50 + Math.random() * (200 - 50);
        // Round the fare to two decimal places
        return Math.round(fare * 100.0) / 100.0;
    }
}
