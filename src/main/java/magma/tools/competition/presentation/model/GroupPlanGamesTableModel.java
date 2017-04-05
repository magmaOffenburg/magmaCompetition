package magma.tools.competition.presentation.model;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.Group;

public class GroupPlanGamesTableModel extends AbstractTableModel
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1970106848379144550L;

	private Vector<TableModelListener> listeners;

	private Group group;

	private int numOfGamesForReset;

	public GroupPlanGamesTableModel()
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
			rowCount = group.getPlan().getGames().size();
		}

		return rowCount;
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String value = null;

		if (group != null) {
			switch (columnIndex) {
			case 0: {
				value = group.getPlan().getGames().get(rowIndex).getHomeTeam().getName();
				break;
			}

			case 1: {
				value = group.getPlan().getGames().get(rowIndex).getGuestTeam().getName();
				break;
			}

			case 2: {
				Game game = group.getPlan().getGames().get(rowIndex);
				String result;

				if (game.getState() == GameState.PLANNED) {
					result = "- : -";
				} else {
					result = String.valueOf(group.getPlan().getGames().get(rowIndex).getResult().getHomeTeamPoints());
					result += " : ";
					result += String.valueOf(group.getPlan().getGames().get(rowIndex).getResult().getGuestTeamPoints());
				}

				if (group.getPlan().getGames().get(rowIndex).isDecisionGame() == true) {
					result += "*";
				}

				value = result;
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
		case 2:
			return "Result";
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
		case 2:
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
		int size = 0;

		if (group != null) {
			size = group.getPlan().getGames().size();
		} else {
			size = numOfGamesForReset;
		}

		for (int i = 0; i < size; i++) {
			int index = size;

			TableModelEvent e =
					new TableModelEvent(this, 0, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

			for (int j = 0, n = listeners.size(); j < n; j++) {
				listeners.get(j).tableChanged(e);
			}
		}
	}

	public void reset(int numOfGames)
	{
		numOfGamesForReset = numOfGames;
		group = null;
	}
}
