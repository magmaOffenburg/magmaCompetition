package magma.tools.competition.presentation.view;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class JTableComboBoxEditor extends DefaultCellEditor
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public JTableComboBoxEditor(String[] items)
	{
		super(new JComboBox(items));
	}
}
