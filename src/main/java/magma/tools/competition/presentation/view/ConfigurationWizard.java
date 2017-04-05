package magma.tools.competition.presentation.view;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.Tournament;
import magma.tools.competition.domain.TournamentBuilder;
import magma.tools.competition.presentation.model.ConfigurationWizardModel;

import org.ciscavate.cjwizard.PageFactory;
import org.ciscavate.cjwizard.WizardContainer;
import org.ciscavate.cjwizard.WizardListener;
import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;

import com.google.inject.Provider;

public class ConfigurationWizard extends JDialog
{
	private static final long serialVersionUID = -1936334258655248975L;

	private ConfigurationWizardModel model;

	private PageFactory wizardFactory;

	private final WizardContainer wizardContainer;

	private Tournament tournament;

	private LinkedList<ITeam> teams;

	private Provider<TournamentBuilder> builderProvider;

	public ConfigurationWizard(Provider<TournamentBuilder> tournamentBuilder, LinkedList<ITeam> teams)
	{
		this.builderProvider = tournamentBuilder;
		this.teams = teams;
		model = new ConfigurationWizardModel();
		wizardFactory = new WizardFactory(model);
		wizardContainer = new WizardContainer(wizardFactory);

		wizardContainer.addWizardListener(new WizardListener() {
			@Override
			public void onCanceled(List<WizardPage> path, WizardSettings settings)
			{
				ConfigurationWizard.this.dispose();
			}

			@Override
			public void onFinished(List<WizardPage> path, WizardSettings settings)
			{
				TournamentBuilder builder = builderProvider.get();
				try {
					builder.withTeams(ConfigurationWizard.this.teams);
					builder.numberOfClusters(model.getNumOfCluster());
					builder.gameDuration(model.getGameDuration());

					Vector<String> numOfPassedTeams = model.getPageOneTableModel().getNumOfPassedTeams();
					Vector<String> groupsInPhase = model.getPageOneTableModel().getNumOfGroupsInPhases();

					for (int i = 0; i < numOfPassedTeams.size(); i++) {
						int inumberOfGroups = Integer.parseInt(groupsInPhase.get(i));
						int inumOfPassedTeams = Integer.parseInt(numOfPassedTeams.get(i));
						builder.addGroupPhase(inumberOfGroups, inumOfPassedTeams);
					}

					tournament = builder.build("Tournament");
					ConfigurationWizard.this.dispose();
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage());
				}
			}

			@Override
			public void onPageChanged(WizardPage newPage, List<WizardPage> path)
			{
				ConfigurationWizard.this.setTitle(newPage.getDescription());
			}
		});

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
		this.getContentPane().add(wizardContainer);
		this.pack();
	}

	public void start()
	{
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		this.setVisible(true);
	}

	public Tournament getTournament()
	{
		return tournament;
	}
}
