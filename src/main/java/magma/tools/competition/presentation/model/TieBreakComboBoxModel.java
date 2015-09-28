package magma.tools.competition.presentation.model;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import javax.swing.table.TableModel;

import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.Phase;

public class TieBreakComboBoxModel implements ComboBoxModel
{
	private Phase phase;

	private TableModel tmGames;

	int index = -1;

	public TieBreakComboBoxModel(TableModel tmGames)
	{
		this.tmGames = tmGames;
	}

	public int getSelectedIndex()
	{
		return index;
	}

	@Override
	public Object getSelectedItem()
	{
		String tmp = " ";
		if (phase != null) {
			if ((index >= 0) && (phase instanceof GroupPhase)) {
				Group group = ((GroupPhase) phase).getGroups().get(index);
				tmp = group.getName();
				group.getTieBreakGames();
				((TieBreakTableModel) tmGames).setGroup(group);
				((TieBreakTableModel) tmGames).refresh();
			}
		}
		return tmp;
	}

	@Override
	public void setSelectedItem(Object anItem)
	{
		if ((phase != null) && (phase instanceof GroupPhase)) {
			for (int i = 0; i < ((GroupPhase) phase).getGroups().size(); i++) {
				String groupName = new String(((GroupPhase) phase).getGroups()
						.get(i).getName());

				String compTo = new String(anItem.toString());

				if (groupName.equals(compTo)) {
					index = i;
					break;
				}
			}
		}
	}

	@Override
	public int getSize()
	{
		int size = 0;
		if (phase != null) {
			if ((phase != null) && (phase instanceof GroupPhase)) {
				size = ((GroupPhase) phase).getGroups().size();
			}
		}
		return size;
	}

	@Override
	public Object getElementAt(int index)
	{
		Object ret = null;
		if ((phase != null) && (phase instanceof GroupPhase)) {
			if (((GroupPhase) phase).getGroups().size() >= index) {
				ret = ((GroupPhase) phase).getGroups().get(index);
			}
		}

		return ret;
	}

	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}

	public void setIndex(int idx)
	{
		index = idx;
	}

	@Override
	public void addListDataListener(ListDataListener l)
	{
	}

	@Override
	public void removeListDataListener(ListDataListener l)
	{
	}
}
