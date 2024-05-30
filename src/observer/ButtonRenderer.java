package observer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JPanel implements TableCellRenderer {

    private final JButton acceptButton = new JButton("Accept");
    private final JButton cancelButton = new JButton("Cancel");

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        add(acceptButton);
        add(cancelButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        return this;
    }
}
