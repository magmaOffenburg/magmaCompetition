package magma.tools.competition.presentation.model;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupResult;
import magma.tools.competition.domain.ITeam;

public class GroupPlanStandingTableModel extends AbstractTableModel
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1970106848379144555L;

	private Vector<TableModelListener> listeners;

	private Group group;

	private int numOfTeamsForReset;

	public GroupPlanStandingTableModel()
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
			rowCount = group.getTeams().size();
		}

		return rowCount;
	}

	@Override
	public int getColumnCount()
	{
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String value = null;

		if (group != null) {
			GroupResult result = group.getResult();
			ITeam team = null;
			String pos = "";
			String points = "";
			String golas = "";
			String games = "";

			team = result.getTeamAtIndex(rowIndex);
			if (team != null) {
				pos = String.valueOf(result.getRankOfTeam(team));
				points = String.valueOf(result.getPoints(team));
				golas = String.valueOf((result.getGoals(team) - result.getGoalsAgainst(team)));
				games = String.valueOf(result.getGames(team));
			} else {
				team = group.getTeams().get(rowIndex);
				pos = String.valueOf(rowIndex + 1);
				points = String.valueOf(0);
				golas = String.valueOf(0);
				games = String.valueOf(0);
			}
			switch (columnIndex) {
			case 0: {
				value = pos;
				break;
			}

			case 1: {
				value = team.getName();
				break;
			}

			case 2: {
				value = games;

				break;
			}

			case 3: {
				value = golas;
				break;
			}

			case 4: {
				value = points;
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
			return "Pos";
		case 1:
			return "Team";
		case 2:
			return "Games";
		case 3:
			return "Goal Difference";
		case 4:
			return "Points";
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
		case 3:
			return String.class;
		case 4:
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
			size = group.getTeams().size();
		} else {
			size = numOfTeamsForReset;
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

	public void reset(int numOfTeams)
	{
		numOfTeamsForReset = numOfTeams;
		group = null;
	}
}
