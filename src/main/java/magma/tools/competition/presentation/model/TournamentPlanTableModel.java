package magma.tools.competition.presentation.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;

public class TournamentPlanTableModel extends AbstractTableModel
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1970106848379144550L;

	private Vector<TableModelListener> listeners;

	private Tournament tournament;

	private Phase currentPhase;

	public TournamentPlanTableModel()
	{
		super();
		listeners = new Vector<TableModelListener>();
	}

	@Override
	public int getRowCount()
	{
		int rowCount = 0;
		int groupPhases = 0;
		int koPhases = 0;
		if (tournament != null) {
			List<Phase> phases = tournament.getPhases();
			for (int i = 0; i < phases.size(); i++) {
				Phase p = phases.get(i);
				if (p instanceof GroupPhase) {
					groupPhases++;
				}

				if (p instanceof KoPhase) {
					koPhases++;
				}
			}

			rowCount = groupPhases + koPhases;

			if (koPhases == 1) {
				rowCount += 1;
			} else if (koPhases == 2) {
				rowCount += 3;
			} else if (koPhases == 3) {
				rowCount += 4;
			} else if (koPhases == 4) {
				rowCount += 8;
			} else if (koPhases == 5) {
				rowCount += 16;
			} else if (koPhases == 6) {
				rowCount += 32;
			}
		}
		return rowCount;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	private String getPhaseState(Phase phase)
	{
		String state = "";
		boolean started = false;
		boolean finished = false;
		boolean planned = false;

		if (phase instanceof GroupPhase) {
			for (int i = 0; i < ((GroupPhase) phase).getGroups().size(); i++) {
				Group group = ((GroupPhase) phase).getGroups().get(i);

				for (int j = 0; j < group.getPlan().getGames().size(); j++) {
					Game game = group.getPlan().getGames().get(j);
					if (game.getState() == GameState.PLANNED) {
						planned = true;
					} else if (game.getState() == GameState.STARTED) {
						started = true;
					} else {
						finished = true;
					}
				}

				if (group.getResult().isTieBreakNeeded() == true) {
					started = true;
				}
			}
		} else {
			for (int i = 0; i < ((KoPhase) phase).getGames().size(); i++) {
				Game koGame = ((KoPhase) phase).getGames().get(i);
				if (koGame.getState() == GameState.PLANNED) {
					planned = true;
				} else if (koGame.getState() == GameState.STARTED) {
					started = true;

				} else {
					finished = true;
				}
			}
		}

		if (((finished == true) && (planned == true)) ||
				((finished == true) && (started == true) && (planned == true)) ||
				(finished == true && started == true)) {
			state = "Started";
		} else if (finished == false && started == false) {
			state = "Planned";
		} else if (planned == false && started == false) {
			state = "Finished";
		} else {
			state = "Started";
		}

		return state;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String value = null;
		ArrayList<String> phaseEntries = new ArrayList<String>();
		ArrayList<String> stateEntries = new ArrayList<String>();

		if (tournament != null) {
			List<Phase> phases = tournament.getPhases();

			for (int i = 0; i < phases.size(); i++) {
				Phase phase = phases.get(i);
				stateEntries.add(getPhaseState(phase));
				phaseEntries.add(phase.getName());
				if (phase instanceof KoPhase) {
					for (int j = 0; j < ((KoPhase) phase).getGames().size(); j++) {
						Game game = ((KoPhase) phase).getGames().get(j);

						String guestTeam = game.getGuestTeam().getName();
						String homeTeam = game.getHomeTeam().getName();
						phaseEntries.add("  " + homeTeam + " : " + guestTeam);

						String homeTeamPoints;
						String guestTeamPoints;

						if (game.getState() == GameState.PLANNED) {
							homeTeamPoints = "-";
							guestTeamPoints = "-";
						} else {
							homeTeamPoints =
									String.valueOf(((KoPhase) phase).getGames().get(j).getResult().getHomeTeamPoints());
							guestTeamPoints = String.valueOf(
									((KoPhase) phase).getGames().get(j).getResult().getGuestTeamPoints());
						}

						stateEntries.add(homeTeamPoints + " : " + guestTeamPoints);
					}
				}
			}

			switch (columnIndex) {
			case 0: {
				value = phaseEntries.get(rowIndex);
				break;
			}

			case 1: {
				value = stateEntries.get(rowIndex);
				break;
			}
			default: {
				value = null;
				break;
			}
			}
		}

		return value;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch (columnIndex) {
		case 0:
			return "Phase";
		case 1:
			return "State";
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

	public LinkedList<Phase> getPhases()
	{
		return (LinkedList<Phase>) tournament.getPhases();
	}

	public void setTournament(Tournament tournament)
	{
		if (tournament != null) {
			this.tournament = tournament;
			this.currentPhase = tournament.getPhases().get(0);
		}
	}

	public void refresh()
	{
		if (!hasTournament()) {
			return;
		}
		int size = tournament.getPhases().size();

		for (int i = 0; i < size; i++) {
			int index = size;

			TableModelEvent e =
					new TableModelEvent(this, 0, index, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT);

			for (int j = 0; j < listeners.size(); j++) {
				listeners.get(j).tableChanged(e);
			}
		}
	}

	public boolean hasTournament()
	{
		return tournament != null;
	}

	public Phase getPhase(int index, int[] inRange)
	{
		Phase ret = null;

		if (tournament != null) {
			ArrayList<Object> phaseEntries = new ArrayList<Object>();
			List<Phase> phases = tournament.getPhases();

			for (int i = 0; i < phases.size(); i++) {
				Phase phase = phases.get(i);
				phaseEntries.add(phase);
				if (phase instanceof KoPhase) {
					for (int j = 0; j < ((KoPhase) phase).getGames().size(); j++) {
						phaseEntries.add(new String(""));
					}
				}
			}

			if (index >= 0 && index < phaseEntries.size()) {
				Object object = phaseEntries.get(index);

				if (object instanceof Phase) {
					ret = (Phase) object;
				}
			} else {
				inRange[0] = 1;
			}
		}

		return ret;
	}

	public void setCurrentPhase(Phase phase)
	{
		this.currentPhase = phase;
	}

	public Phase getCurrentPhase()
	{
		return currentPhase;
	}
}
