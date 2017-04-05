package magma.tools.competition.presentation.view;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class JTableComboBoxRenderer extends JComboBox<Object> implements TableCellRenderer
{
	private static final long serialVersionUID = 1104526883475801342L;

	public JTableComboBoxRenderer(String[] items)
	{
		super(items);
	}

	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());

			if (hasFocus) {
				setSelectedItem(value);
			}
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}

		return this;
	}
}