package magma.tools.competition.presentation.model;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;

public class SetPhaseComboBoxModel implements ComboBoxModel<Phase>
{
	private Tournament tournamet;

	private Phase newPhase;

	int index = -1;

	public SetPhaseComboBoxModel(Tournament tournamet)
	{
		this.tournamet = tournamet;
		newPhase = null;
	}

	@Override
	public Object getSelectedItem()
	{
		String ret = " ";
		if (tournamet != null) {
			if (index >= 0) {
				newPhase = tournamet.getPhases().get(index);
				ret = newPhase.getName();
			}
		}
		return ret;
	}

	@Override
	public void setSelectedItem(Object anItem)
	{
		if ((tournamet != null) && (anItem != null)) {
			List<Phase> phases = tournamet.getPhases();

			for (int i = 0; i < phases.size(); i++) {
				String phaseName = new String(phases.get(i).getName());

				String compTo = new String(anItem.toString());

				if (phaseName.equals(compTo)) {
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

		if (tournamet != null) {
			size = tournamet.getPhases().size();
		}
		return size;
	}

	@Override
	public Phase getElementAt(int index)
	{
		Phase ret = null;
		if (tournamet != null) {
			ret = tournamet.getPhases().get(index);
		}
		return ret;
	}

	@Override
	public void addListDataListener(ListDataListener l)
	{
	}

	@Override
	public void removeListDataListener(ListDataListener l)
	{
	}

	public Phase getNewPhase()
	{
		return newPhase;
	}
}
