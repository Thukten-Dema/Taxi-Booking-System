package observer;

import ui.DriverHomePage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    protected JPanel panel;
    protected JButton acceptButton;
    protected JButton cancelButton;
    private String label;
    private boolean isPushed;
    private int row;
    private DriverHomePage driverHomePage;

    public ButtonEditor(JCheckBox checkBox, DriverHomePage driverHomePage) {
        super(checkBox);
        this.driverHomePage = driverHomePage;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        acceptButton = new JButton("Accept");
        cancelButton = new JButton("Cancel");

        panel.add(acceptButton);
        panel.add(cancelButton);

        acceptButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                driverHomePage.acceptBooking(row);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                driverHomePage.cancelBooking(row);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        label = (value == null) ? "" : value.toString();
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
