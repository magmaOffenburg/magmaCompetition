package magma.tools.competition.presentation.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import magma.tools.competition.presentation.model.ConfigurationWizardModel;

import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;

public class WizardPageOne extends WizardPage
{
	private static final long serialVersionUID = 3809877207839319156L;

	private ConfigurationWizardModel model;

	public WizardPageOne(String title, String description,
			ConfigurationWizardModel model)
	{
		super(title, description);

		this.model = model;

		createGui();
	}

	private void createGui()
	{
		String[] comboBoxGroupePhasesValues = { "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "10" };

		String[] comboBoxNumGroups = { "1", "2", "3", "4", "5", "6", "7", "8",
				"9", "10" };

		String[] comboBoxTerminatedTeams = { "1", "2", "3", "4", "5", "6", "7",
				"8", "9", "10" };

		JTable table = new JTable(model.getPageOneTableModel());
		TableColumn col = table.getColumnModel().getColumn(1);
		col.setCellEditor(new JTableComboBoxEditor(comboBoxNumGroups));
		col = table.getColumnModel().getColumn(2);
		col.setCellEditor(new JTableComboBoxEditor(comboBoxTerminatedTeams));
		// col.setCellRenderer(new
		// JTableComboBoxRenderer(comboBoxTerminatedTeams));

		JScrollPane scrollPane = new JScrollPane(table);

		JComboBox<String> comboBoxNumPhases = new JComboBox<String>();

		JLabel label = new JLabel("Number of Groupe phases:");

		Container pane = this;

		for (int i = 0; i < 10; i++) {
			comboBoxNumPhases.addItem(comboBoxGroupePhasesValues[i]);
		}

		comboBoxNumPhases.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String tmp = (String) ((JComboBox) e.getSource()).getSelectedItem();
				model.createPhases(Integer.parseInt(tmp));
			}
		});

		scrollPane.setPreferredSize(new Dimension(450, 150));

		pane.setLayout(new GridBagLayout());
		GridBagConstraints girdBagConstraint = new GridBagConstraints();
		girdBagConstraint.insets = new Insets(10, 10, 10, 10);

		girdBagConstraint.fill = GridBagConstraints.HORIZONTAL;
		girdBagConstraint.gridx = 0;
		girdBagConstraint.gridy = 0;
		pane.add(label, girdBagConstraint);

		girdBagConstraint.fill = GridBagConstraints.HORIZONTAL;
		girdBagConstraint.weightx = 0.5;
		girdBagConstraint.gridx = 1;
		girdBagConstraint.gridy = 0;
		pane.add(comboBoxNumPhases, girdBagConstraint);

		girdBagConstraint.fill = GridBagConstraints.HORIZONTAL;
		girdBagConstraint.ipady = 30;
		girdBagConstraint.weightx = 0.0;
		girdBagConstraint.gridwidth = 3;
		girdBagConstraint.gridx = 0;
		girdBagConstraint.gridy = 1;

		pane.add(scrollPane, girdBagConstraint);
	}

	@Override
	public void updateSettings(WizardSettings settings)
	{
		super.updateSettings(settings);
	}
}
