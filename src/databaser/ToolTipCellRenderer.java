/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author rcher
 */
public class ToolTipCellRenderer extends JLabel implements TableCellRenderer {

    // Variable Declaration
    private final int primaryKeyIndex;
// End of Variable Declaration

    public ToolTipCellRenderer(int primaryKeyIndex) {
        this.primaryKeyIndex = primaryKeyIndex;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        //
        final String str = String.valueOf(value);

        // Consider primary key index first - paint it pink.
        if (column == primaryKeyIndex) {
            final Font font = getFont();
            setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
            setForeground(new Color(206, 101, 171));

            // Then consider the user-selected value
        } else if ((table.getSelectedRow() == row && table.getSelectedColumn() == column)) {
            setForeground(new Color(0, 103, 191));
            setBackground(table.getBackground());

            // Then change the text color of all the other values in the row that aren't the primary or the value selected
        } else if (table.getSelectedRow() == row) {
            setBackground(table.getBackground());
            setForeground(new Color(0, 132, 0));
        } else {

            // Otherwise when unselected return to normal black text and white background.
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        // Simply allow ToolTipText by override the component in the cell as a JLabel with a ToolTip.
        setText(str);
        setToolTipText(str);

        //
        return this;
    }
}
