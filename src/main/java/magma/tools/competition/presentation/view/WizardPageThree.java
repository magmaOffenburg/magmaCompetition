package magma.tools.competition.presentation.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import magma.tools.competition.presentation.model.ConfigurationWizardModel;

import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;

public class WizardPageThree extends WizardPage
{
	private static final long serialVersionUID = -6663709634095249686L;

	private ConfigurationWizardModel model;

	private String[] comboBoxClusterValue = { "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10" };

	private JLabel label;

	private JLabel labelGameDuration;

	private JComboBox<String> comboBoxCluster;

	private Container pane;

	final private JFormattedTextField gameDuration;

	private NumberFormat gameDurationDisplayFormat;

	private NumberFormat gameDurationEditFormat;

	public WizardPageThree(String title, String description,
			ConfigurationWizardModel model)
	{
		super(title, description);

		this.model = model;
		pane = this;

		label = new JLabel("Number of Clusters:");
		labelGameDuration = new JLabel("Total playing time:");
		comboBoxCluster = new JComboBox<String>();

		gameDurationDisplayFormat = NumberFormat.getInstance();
		gameDurationDisplayFormat.setMinimumFractionDigits(0);
		gameDurationEditFormat = NumberFormat.getNumberInstance();
		gameDuration = new JFormattedTextField(new DefaultFormatterFactory(
				new NumberFormatter(gameDurationDisplayFormat),
				new NumberFormatter(gameDurationDisplayFormat),
				new NumberFormatter(gameDurationEditFormat)));

		createGui();
	}

	private void createGui()
	{
		for (int i = 0; i < 10; i++) {
			comboBoxCluster.addItem(comboBoxClusterValue[i]);
		}

		comboBoxCluster.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String tmp = (String) ((JComboBox) e.getSource()).getSelectedItem();
				model.setNumOfCluster(Integer.parseInt(tmp));
			}
		});

		gameDuration.setValue(new Integer(0));
		gameDuration.setColumns(10);
		gameDuration.addPropertyChangeListener("value",
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt)
					{
						Object source = evt.getSource();
						if (source == gameDuration) {
							model.setGameDuration(((Number) gameDuration.getValue())
									.doubleValue());
						}
					}
				});

		gameDuration.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e)
			{
				Object source = e.getSource();
				if (source == gameDuration) {
					model.setGameDuration(((Number) gameDuration.getValue())
							.doubleValue());
				}
			}

			@Override
			public void focusGained(FocusEvent e)
			{
			}
		});

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
		pane.add(comboBoxCluster, girdBagConstraint);

		girdBagConstraint.fill = GridBagConstraints.HORIZONTAL;
		girdBagConstraint.ipady = 30;
		girdBagConstraint.weightx = 0.0;
		girdBagConstraint.gridwidth = 1;
		girdBagConstraint.gridx = 0;
		girdBagConstraint.gridy = 1;
		pane.add(labelGameDuration, girdBagConstraint);

		girdBagConstraint.fill = GridBagConstraints.HORIZONTAL;
		girdBagConstraint.ipady = 0;
		girdBagConstraint.weightx = 0.0;
		girdBagConstraint.gridwidth = 1;
		girdBagConstraint.gridx = 1;
		girdBagConstraint.gridy = 1;
		pane.add(gameDuration, girdBagConstraint);
	}

	@Override
	public void updateSettings(WizardSettings settings)
	{
		super.updateSettings(settings);
	}

	/**
	 * This is the last page in the wizard, so we will enable the finish button
	 * and disable the "Next >" button just before the page is displayed:
	 */
	public void rendering(List<WizardPage> path, WizardSettings settings)
	{
		super.rendering(path, settings);
		setFinishEnabled(true);
		setNextEnabled(false);
	}
}
