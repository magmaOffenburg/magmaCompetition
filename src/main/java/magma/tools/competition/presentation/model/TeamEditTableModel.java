package magma.tools.competition.presentation.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.ProxyNotResolvableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamEditTableModel implements TableModel
{

	private Logger logger = LoggerFactory.getLogger(TeamEditTableModel.class);

	private LinkedList<ITeam> teams;

	private LinkedList<ITeam> tempEditTeams;

	private Vector<TableModelListener> listeners;

	public TeamEditTableModel(LinkedList<ITeam> teams)
	{
		super();
		this.tempEditTeams = new LinkedList<ITeam>();
		this.teams = teams;
		this.listeners = new Vector<TableModelListener>();
	}

	@Override
	public int getRowCount()
	{
		return teams.size();
	}

	@Override
	public int getColumnCount()
	{
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		ITeam team = teams.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return team.getName();
		case 1:
			return team.getUsername();
		case 2:
			return team.getStartScriptFileName();
		case 3:
			return team.getPathToScriptFile();
		case 4:
			return team.isSetTeam();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex) {
		case 0:
			return "Team name";
		case 1:
			return "User name";
		case 2:
			return "Start script";
		case 3:
			return "Path";
		case 4:
			return "Is group head";
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
			return Boolean.class;
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		try {
			if (teams.size() != 0 && teams.size() >= rowIndex) {
				if (aValue.equals("") == false) {

					ITeam p = teams.get(rowIndex);

					switch (columnIndex) {
					case 0: {
						break;
					}
					case 1: {
						p.setUsername((String) aValue);
						break;
					}
					case 2: {
						p.setStartScriptFileName((String) aValue);
						break;
					}
					case 3: {

						p.setPathToScriptFile((String) aValue);
						break;
					}
					case 4: {
						p.setTeam((boolean) aValue);
						break;
					}
					}

					TableModelEvent e = new TableModelEvent(this, rowIndex,
							rowIndex, TableModelEvent.UPDATE, TableModelEvent.UPDATE);

					for (int i = 0, n = listeners.size(); i < n; i++) {
						listeners.get(i).tableChanged(e);
					}
				}
			}
		} catch (ProxyNotResolvableException exception) {
			logger.warn(exception.getMessage());
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

	public LinkedList<ITeam> getTeams()
	{
		return teams;
	}

	public void startEdit()
	{
		tempEditTeams.clear();

		try {
			for (ITeam team : teams) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				new ObjectOutputStream(baos).writeObject(team);

				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());

				tempEditTeams.add((ITeam) new ObjectInputStream(bais).readObject());
			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cancelEdit()
	{
		teams.clear();
		teams.addAll(tempEditTeams);
	}
}
