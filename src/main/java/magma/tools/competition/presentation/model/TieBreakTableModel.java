package magma.tools.competition.presentation.model;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.Group;

public class TieBreakTableModel extends AbstractTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1970106848379144550L;

	private Vector<TableModelListener> listeners;

	private Group group;

	public TieBreakTableModel()
	{
		super();

		group = null;
		listeners = new Vector<TableModelListener>();
	}

	@Override
	public int getRowCount()
	{
		int rowCount = 0;

		if (group != null) {
			for (int i = 0; i < group.getPlan().getGames().size(); i++) {
				if (group.getPlan().getGames().get(i).isDecisionGame() == true) {
					rowCount++;
				}
			}
		}

		return rowCount;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String value = null;
		ArrayList<Game> tieBreakGames = new ArrayList<Game>();

		for (int i = 0; i < group.getPlan().getGames().size(); i++) {
			Game game = group.getPlan().getGames().get(i);
			if (game.isDecisionGame() == true) {
				tieBreakGames.add(game);
			}
		}

		if (group != null) {
			switch (columnIndex) {
			case 0: {
				value = tieBreakGames.get(rowIndex).getHomeTeam().getName();
				break;
			}

			case 1: {
				value = tieBreakGames.get(rowIndex).getGuestTeam().getName();
				break;
			}

			default:
				value = null;
			}
		}

		return value;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex) {
		case 0:
			return "Team 1";
		case 1:
			return "Team 2";
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	@Override
	public void addTableModelListener(TableModelListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
		listeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
	}

	public Group getGroup()
	{
		return group;
	}

	public void setGroup(Group group)
	{
		this.group = group;
	}

	public void refresh()
	{
		int size = getRowCount();

		for (int i = 0; i < size; i++) {
			int index = size;

			TableModelEvent e = new TableModelEvent(this, 0, index,
					TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

			for (int j = 0, n = listeners.size(); j < n; j++) {
				listeners.get(j).tableChanged(e);
			}
		}
	}
}
