package magma.tools.competition.presentation.model;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class WizardPageOneTableModel implements TableModel
{
	private Vector<TableModelListener> listeners;

	private Vector<String> phasesNames;

	private Vector<String> numOfGroupsInPhase;

	private Vector<String> numOfTeamsPassed;

	public WizardPageOneTableModel()
	{
		super();
		phasesNames = new Vector<String>();
		numOfGroupsInPhase = new Vector<String>();
		numOfTeamsPassed = new Vector<String>();
		listeners = new Vector<TableModelListener>();
	}

	public void createPhases(int num)
	{
		phasesNames.clear();
		numOfGroupsInPhase.clear();
		numOfTeamsPassed.clear();

		for (int i = 0; i < num; i++) {
			phasesNames.add("Group Phase " + (i + 1));
			numOfGroupsInPhase.add("0");
			numOfTeamsPassed.add("0");

			int index = phasesNames.size();

			TableModelEvent e =
					new TableModelEvent(this, index, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

			for (int j = 0, n = listeners.size(); j < n; j++) {
				((TableModelListener) listeners.get(j)).tableChanged(e);
			}
		}
	}

	@Override
	public int getRowCount()
	{
		return phasesNames.size();
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		switch (columnIndex) {
		case 0:
			return phasesNames.get(rowIndex);
		case 1:
			return numOfGroupsInPhase.get(rowIndex);
		case 2:
			return numOfTeamsPassed.get(rowIndex);
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex) {
		case 0:
			return "Phase";
		case 1:
			return "Number of groups";
		case 2:
			return "Teams next round (each group)";
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
		boolean ret = false;

		if (columnIndex > 0) {
			ret = true;
		}

		return ret;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (phasesNames.size() != 0 && phasesNames.size() >= rowIndex) {
			if (aValue.equals("") == false) {
				if (columnIndex == 1) {
					numOfGroupsInPhase.set(rowIndex, (String) aValue);
				} else {
					numOfTeamsPassed.set(rowIndex, (String) aValue);
				}

				TableModelEvent e = new TableModelEvent(
						this, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

				for (int i = 0, n = listeners.size(); i < n; i++) {
					((TableModelListener) listeners.get(i)).tableChanged(e);
				}
			}
		}
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

	public Vector<String> getPhaseNames()
	{
		return phasesNames;
	}

	public Vector<String> getNumOfGroupsInPhases()
	{
		return numOfGroupsInPhase;
	}

	public Vector<String> getNumOfPassedTeams()
	{
		return numOfTeamsPassed;
	}
}
