package magma.tools.competition.presentation.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import magma.tools.competition.domain.ChangeHandler;
import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;
import magma.tools.competition.domain.TournamentBuilder;
import magma.tools.competition.json.JsonHandler;
import magma.tools.competition.presentation.model.GroupPlanGamesComboBoxModel;
import magma.tools.competition.presentation.model.GroupPlanGamesTableModel;
import magma.tools.competition.presentation.model.GroupPlanStandingTableModel;
import magma.tools.competition.presentation.model.OpenTeamsListener;
import magma.tools.competition.presentation.model.OpenTournamentMouseAdapter;
import magma.tools.competition.presentation.model.TournamentPlanTableModel;
import magma.tools.competition.runner.ISimulationEventHandler;
import magma.tools.competition.runner.SimulationEvent;
import magma.tools.competition.tournamentRunner.ITournamentRunner;
import magma.tools.competition.tournamentRunner.TournamentRunner;
import magma.tools.competition.util.ClusterConfiguration;
import magma.tools.competition.util.HTMLGenerator;

import com.google.inject.Provider;

/**
 *
 * @author sgutjahr
 */
public class MainFrame extends javax.swing.JFrame implements ChangeHandler<Game>, ISimulationEventHandler
{
	private static final long serialVersionUID = -7521014059917069119L;

	private javax.swing.JComboBox jComboBoxGroupPlanGroup;

	private javax.swing.JLabel jLabelGroupPlanGames;

	private javax.swing.JLabel jLabelGroupPlanGroup;

	private javax.swing.JLabel jLabelGroupPlanStanding;

	private javax.swing.JPanel jPanelGroupPlan;

	private javax.swing.JPanel jPanelTournamentPlan;

	private javax.swing.JScrollPane jScrollPaneGroupPlanGames;

	private javax.swing.JScrollPane jScrollPaneGroupPlanStanding;

	private javax.swing.JScrollPane jScrollPaneTournamentPlanPlan;

	private javax.swing.JTable jTableGroupPlanGames;

	private javax.swing.JTable jTableGroupPlanStanding;

	private javax.swing.JTable jtTournamentPlanPlan;

	private GroupPlanGamesComboBoxModel comboBoxModel;

	private ConfigurationWizard configurationWizard;

	private TournamentPlanTableModel tournamentPlanTableModel;

	private GroupPlanGamesTableModel grouPlanGamesTableModel;

	private GroupPlanStandingTableModel tmstanding;

	private Tournament _tournament;

	private OpenTeamsListener openTeamsListener;

	private Provider<TournamentBuilder> tournamentBuilderProvider;

	private ITournamentRunner tournamentRunner;

	private ListSelectionListener tournamentTableListener;

	private JMenuItem jMenuItemTeamsEdit;

	private JMenuItem jMenuItemConfigurationWizard;

	private JMenuItem jMenuItemTournamentStart;

	private JMenuItem jMenuItemTournamentStop;

	private JMenuItem jMenuItemTournamentTieBreak;

	private JMenuItem jMenuItemTournamentMerge;

	private JMenuItem jMenuItemExportTournamentToHTML;

	private JButton jButtonToolBarStart;

	private JButton jButtonToolBarStop;

	/**
	 * Creates new form MAinFrame
	 *
	 * @param tournamentBuilder
	 */
	public MainFrame(Provider<TournamentBuilder> tournamentBuilder)
	{
		this.tournamentBuilderProvider = tournamentBuilder;

		setTitle("SimManager");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		initComponents();
	}

	private List<Game> getSelectedGames()
	{
		List<Game> games = null;
		Phase phase = tournamentPlanTableModel.getCurrentPhase();
		if (phase != null) {
			if (phase instanceof KoPhase) {
				games = ((KoPhase) phase).getGames();
			} else {
				Group group = grouPlanGamesTableModel.getGroup();
				games = group.getPlan().getGames();
			}

			for (Game game : games) {
				game.removeChangeHandler(MainFrame.this);
			}

			for (Game game : games) {
				game.addChangeHandler(MainFrame.this);
			}
		}

		return games;
	}

	private boolean isPhaseBeforeFinished()
	{
		Phase phaseBefore = null;
		boolean isFirstPhase = true;
		boolean isPhaseFinished = true;
		Phase phase = tournamentPlanTableModel.getCurrentPhase();

		if (_tournament != null) {
			for (int i = 0; i < _tournament.getPhases().size(); i++) {
				if (_tournament.getPhases().get(i).equals(phase)) {
					if (i > 0) {
						phaseBefore = _tournament.getPhases().get(i - 1);
						isFirstPhase = false;
						break;
					}
				}
			}

			if (phaseBefore != null && isFirstPhase == false) {
				if (phaseBefore instanceof KoPhase) {
					for (int j = 0; j < ((KoPhase) phaseBefore).getGames().size(); j++) {
						if (((KoPhase) phaseBefore).getGames().get(j).getState() != GameState.FINISHED) {
							isPhaseFinished = false;
							break;
						}
					}
				} else {
					for (int j = 0; j < ((GroupPhase) phaseBefore).getGroups().size(); j++) {
						Group group = ((GroupPhase) phaseBefore).getGroups().get(j);
						List<Game> games = group.getPlan().getGames();

						for (int k = 0; k < games.size(); k++) {
							if (games.get(k).getState() != GameState.FINISHED) {
								isPhaseFinished = false;
							}
						}

						if (group.getResult().isTieBreakNeeded() == true) {
							isPhaseFinished = false;
						}
					}
				}
			}
		} else {
			isPhaseFinished = false;
		}

		return isPhaseFinished;
	}

	public void refresh()
	{
		// update menu items
		boolean haveTeams = haveTeams();
		jMenuItemTeamsEdit.setEnabled(haveTeams);
		jMenuItemConfigurationWizard.setEnabled(haveTeams);

		boolean haveTournament = haveTournament();
		jMenuItemTournamentStart.setEnabled(haveTournament);
		jMenuItemTournamentStop.setEnabled(haveTournament);
		jMenuItemTournamentTieBreak.setEnabled(haveTournament);
		jButtonToolBarStart.setEnabled(haveTournament);
		jButtonToolBarStop.setEnabled(haveTournament);
		jMenuItemTournamentMerge.setEnabled(haveTournament);
		jMenuItemExportTournamentToHTML.setEnabled(haveTournament);

		// update table models
		if (haveTournament) {
			tournamentPlanTableModel.refresh();
			grouPlanGamesTableModel.refresh();
			tmstanding.refresh();
			jComboBoxGroupPlanGroup.repaint();
		}
	}

	private void refreshWithOutTournamentPlan()
	{
		if ((tournamentPlanTableModel != null) && (grouPlanGamesTableModel != null) && (tmstanding != null)) {
			grouPlanGamesTableModel.refresh();
			tmstanding.refresh();
			jComboBoxGroupPlanGroup.repaint();
		}
	}

	@Override
	public void onChange(Game subject)
	{
		saveTournamentToJsonFile();
		refresh();
	}

	private void saveTournamentToJsonFile()
	{
		try {
			JsonHandler.exportToFile(
					this._tournament, new File(ClusterConfiguration.get().getFileStartPath() + "/tournament.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int handleSimulationEvent(SimulationEvent event, String message)
	{
		switch (event) {
		case GAMES_FINISHED:
		case GAME_STOPPED:
		case CONNECTION_PROBLEM:
		case INVALID_CONFIGURATION: {
			showMessage(message);
			break;
		}
		case NOT_ALL_PLAYERS_ON_FILED: {
			String[] buttons = {"Wait", "Restart Game", "Play", "Cancel"};
			int result = JOptionPane.showOptionDialog(
					this, message, "SimManager", JOptionPane.DEFAULT_OPTION, 0, null, buttons, buttons[0]);
			return result;
		}
		case SERVER_ERROR: {
			String[] buttons = {"Continue with next Game", "Restart Game"};
			int result = JOptionPane.showOptionDialog(
					this, message, "SimManager", JOptionPane.DEFAULT_OPTION, 0, null, buttons, buttons[0]);
			return result;
		}
		default: {
			showMessage("Error");
		}
		}
		return 0;
	}

	private void showMessage(String message)
	{
		JTextArea jta = new JTextArea(message);
		@SuppressWarnings("serial")
		JScrollPane jsp = new JScrollPane(jta) {
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(600, 320);
			}
		};
		JOptionPane.showMessageDialog(this, jsp, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	private void initComponents()
	{
		tournamentPlanTableModel = new TournamentPlanTableModel();
		grouPlanGamesTableModel = new GroupPlanGamesTableModel();
		tmstanding = new GroupPlanStandingTableModel();
		comboBoxModel = new GroupPlanGamesComboBoxModel(grouPlanGamesTableModel, tmstanding);
		jPanelTournamentPlan = new javax.swing.JPanel();
		jScrollPaneTournamentPlanPlan = new javax.swing.JScrollPane();
		jtTournamentPlanPlan = new javax.swing.JTable(tournamentPlanTableModel);
		jPanelGroupPlan = new javax.swing.JPanel();
		jLabelGroupPlanGroup = new javax.swing.JLabel();
		jComboBoxGroupPlanGroup = new javax.swing.JComboBox(comboBoxModel);
		jLabelGroupPlanGames = new javax.swing.JLabel();
		jScrollPaneGroupPlanGames = new javax.swing.JScrollPane();
		jTableGroupPlanGames = new javax.swing.JTable(grouPlanGamesTableModel);
		jLabelGroupPlanStanding = new javax.swing.JLabel();
		jScrollPaneGroupPlanStanding = new javax.swing.JScrollPane();
		jTableGroupPlanStanding = new javax.swing.JTable(tmstanding);

		jtTournamentPlanPlan.getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		tournamentTableListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				jtTournamentPlanPlanAddListSelectionListener(e);
			}
		};

		jtTournamentPlanPlan.getSelectionModel().addListSelectionListener(tournamentTableListener);

		jComboBoxGroupPlanGroup.addActionListener(new ActionListener() {
			private Group currentGroup;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				jComoBoxGroupPlanGroupAddActionListener(e, currentGroup);
			}
		});

		jPanelTournamentPlan.setBorder(javax.swing.BorderFactory.createTitledBorder("Tournament plan"));

		jScrollPaneTournamentPlanPlan.setViewportView(jtTournamentPlanPlan);

		GroupLayout jPanelTournamentPlanLayout = new GroupLayout(jPanelTournamentPlan);
		jPanelTournamentPlan.setLayout(jPanelTournamentPlanLayout);
		jPanelTournamentPlanLayout.setHorizontalGroup(
				jPanelTournamentPlanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(jPanelTournamentPlanLayout.createSequentialGroup()
										  .addComponent(jScrollPaneTournamentPlanPlan, GroupLayout.DEFAULT_SIZE, 463,
												  Short.MAX_VALUE)
										  .addGap(0, 0, 0)));
		jPanelTournamentPlanLayout.setVerticalGroup(
				jPanelTournamentPlanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(jPanelTournamentPlanLayout.createSequentialGroup().addContainerGap().addComponent(
								jScrollPaneTournamentPlanPlan, GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)));

		jPanelGroupPlan.setBorder(javax.swing.BorderFactory.createTitledBorder("Group plan"));

		jLabelGroupPlanGroup.setText("Group");

		jLabelGroupPlanGames.setText("Games:");

		jScrollPaneGroupPlanGames.setViewportView(jTableGroupPlanGames);

		jLabelGroupPlanStanding.setText("Standing:");

		jScrollPaneGroupPlanStanding.setViewportView(jTableGroupPlanStanding);
		GroupLayout jPanelGroupPlanLayout = new GroupLayout(jPanelGroupPlan);
		jPanelGroupPlan.setLayout(jPanelGroupPlanLayout);
		jPanelGroupPlanLayout.setHorizontalGroup(
				jPanelGroupPlanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPaneGroupPlanGames, GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
						.addComponent(jScrollPaneGroupPlanStanding)
						.addGroup(jPanelGroupPlanLayout.createSequentialGroup()
										  .addGroup(jPanelGroupPlanLayout
															.createParallelGroup(GroupLayout.Alignment.LEADING)
															.addGroup(jPanelGroupPlanLayout.createSequentialGroup()
																			  .addComponent(jLabelGroupPlanGroup,
																					  GroupLayout.PREFERRED_SIZE, 59,
																					  GroupLayout.PREFERRED_SIZE)
																			  .addGap(24, 24, 24)
																			  .addComponent(jComboBoxGroupPlanGroup,
																					  GroupLayout.PREFERRED_SIZE, 232,
																					  GroupLayout.PREFERRED_SIZE))
															.addComponent(jLabelGroupPlanStanding)
															.addComponent(jLabelGroupPlanGames))
										  .addGap(0, 0, Short.MAX_VALUE)));
		jPanelGroupPlanLayout.setVerticalGroup(
				jPanelGroupPlanLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jPanelGroupPlanLayout.createSequentialGroup()
										.addGap(17, 17, 17)
										.addGroup(
												jPanelGroupPlanLayout
														.createParallelGroup(GroupLayout.Alignment.BASELINE)
														.addComponent(jLabelGroupPlanGroup, GroupLayout.PREFERRED_SIZE,
																22, GroupLayout.PREFERRED_SIZE)
														.addComponent(jComboBoxGroupPlanGroup,
																GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18, 18, 18)
										.addComponent(jLabelGroupPlanGames, GroupLayout.PREFERRED_SIZE, 25,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPaneGroupPlanGames, GroupLayout.PREFERRED_SIZE, 0,
												Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(jLabelGroupPlanStanding)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jScrollPaneGroupPlanStanding, GroupLayout.PREFERRED_SIZE, 265,
												GroupLayout.PREFERRED_SIZE)));

		setJMenuBar(createMenu());
		JToolBar jToolBar1 = createToolbar();

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
										  .addComponent(jPanelTournamentPlan, GroupLayout.DEFAULT_SIZE,
												  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										  .addComponent(jPanelGroupPlan, GroupLayout.DEFAULT_SIZE,
												  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														  .addComponent(jPanelTournamentPlan, GroupLayout.DEFAULT_SIZE,
																  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														  .addComponent(jPanelGroupPlan, GroupLayout.DEFAULT_SIZE,
																  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));

		pack();
	}

	/**
	 * @return the main menu bar to use
	 */
	private JMenuBar createMenu()
	{
		JMenuBar jMenuBar1 = new JMenuBar();
		JMenu jMenuPrepare = new JMenu("Prepare Tournament");
		JMenuItem jMenuItemLoadTeams = new JMenuItem("Load Teams...");
		jMenuItemTeamsEdit = new JMenuItem("Edit Teams...");
		jMenuItemConfigurationWizard = new JMenuItem("Configuration Wizard...");
		JMenuItem jMenuItemLoadTournament = new JMenuItem("Load Tournament...");
		JMenuItem jMenuItemStartExit = new JMenuItem("Exit");

		JMenu jMenuTournament = new JMenu("Run Tournament");
		jMenuItemTournamentStart = new JMenuItem("Start/Continue");
		jMenuItemTournamentStop = new JMenuItem("Stop");
		jMenuItemTournamentTieBreak = new JMenuItem("Tie Break...");
		jMenuItemTournamentMerge = new JMenuItem("Merge tournaments");
		jMenuItemExportTournamentToHTML = new JMenuItem("Export to HTML");
		jMenuItemTournamentMerge.setEnabled(false);
		jMenuItemExportTournamentToHTML.setEnabled(false);

		// menu Prepare
		jMenuItemLoadTeams.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});
		openTeamsListener = new OpenTeamsListener();
		jMenuItemLoadTeams.addActionListener(openTeamsListener);
		jMenuPrepare.add(jMenuItemLoadTeams);

		jMenuItemTeamsEdit.setEnabled(false);
		jMenuItemTeamsEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				jMenuItemTeamsEditListener(e);
			}
		});
		jMenuPrepare.add(jMenuItemTeamsEdit);

		jMenuItemConfigurationWizard.setEnabled(false);
		jMenuItemConfigurationWizard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jMenuItemTournamentConfigurationWizardListener(e);
			}
		});
		jMenuPrepare.add(jMenuItemConfigurationWizard);

		OpenTournamentMouseAdapter openTournamentMouseAdapter = new OpenTournamentMouseAdapter();
		openTournamentMouseAdapter.addChangeHandler(new ChangeHandler<Tournament>() {
			@Override
			public void onChange(Tournament tournament)
			{
				openTournamentMouseAdapterAddChangeHandler(tournament);
			}
		});
		jMenuItemLoadTournament.addMouseListener(openTournamentMouseAdapter);
		jMenuPrepare.add(jMenuItemLoadTournament);

		jMenuItemStartExit.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jMenuItemStartExitActionPerformed(evt);
			}
		});
		jMenuPrepare.add(jMenuItemStartExit);
		jMenuBar1.add(jMenuPrepare);

		// menu run tournament
		jMenuItemTournamentStart.setEnabled(false);
		jMenuItemTournamentStart.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jButtonToolBarStartActionPerformed(evt);
			}
		});
		jMenuTournament.add(jMenuItemTournamentStart);

		jMenuItemTournamentStop.setEnabled(false);
		jMenuItemTournamentStop.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jButtonToolBarStopActionPerformed(evt);
			}
		});
		jMenuTournament.add(jMenuItemTournamentStop);

		jMenuItemTournamentTieBreak.setEnabled(false);
		jMenuItemTournamentTieBreak.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jMenuItemTournamentTieBreakListener(e);
			}
		});
		jMenuTournament.add(jMenuItemTournamentTieBreak);

		jMenuItemTournamentMerge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				jMenuItemTournamentMergeActionPerformed(e);
			}
		});
		jMenuTournament.add(jMenuItemTournamentMerge);

		jMenuItemExportTournamentToHTML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try {
					HTMLGenerator.generateHTML(_tournament);
					JOptionPane.showMessageDialog(null, "Successfully exported tournament to HTML document!");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Error occured on exporting the HTML document!");
					e1.printStackTrace();
				}
			}
		});
		jMenuTournament.add(jMenuItemExportTournamentToHTML);

		jMenuBar1.add(jMenuTournament);

		return jMenuBar1;
	}

	/**
	 * @return the main toolbar to use
	 */
	private JToolBar createToolbar()
	{
		JToolBar jToolBar1 = new JToolBar();
		jButtonToolBarStart = new JButton();
		jButtonToolBarStop = new JButton();

		jToolBar1.setFloatable(false);
		jToolBar1.setRollover(true);

		jButtonToolBarStart.setEnabled(false);
		jButtonToolBarStart.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("startgame.png")));
		// jButtonToolBarStart.setIcon(new ImageIcon("buttonPics/startgame.png"));

		jButtonToolBarStart.setFocusable(false);
		jButtonToolBarStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonToolBarStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonToolBarStart.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jButtonToolBarStartActionPerformed(evt);
			}
		});

		jToolBar1.add(jButtonToolBarStart);

		jButtonToolBarStop.setEnabled(false);
		jButtonToolBarStop.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("stop-button.png")));
		// jButtonToolBarStop.setIcon(new
		// ImageIcon("buttonPics/stop-button.png"));

		jButtonToolBarStop.setFocusable(false);
		jButtonToolBarStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		jButtonToolBarStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		jButtonToolBarStop.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				jButtonToolBarStopActionPerformed(evt);
			}
		});

		jToolBar1.add(jButtonToolBarStop);
		return jToolBar1;
	}

	private void jMenuItemTournamentMergeActionPerformed(ActionEvent evt)
	{
		JFileChooser chooser = new JFileChooser("Verzeichnis wählen");

		String tournamentToMergeFilePath;
		Tournament tournamentFromFile;

		try {
			// TODO: change ClusterConfiguration to not throw IOExceptions
			String path = ClusterConfiguration.get().getFileStartPath();
			final File file = new File(path);

			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setCurrentDirectory(file);
			chooser.setVisible(true);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		final int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File inputVerzFile = chooser.getSelectedFile();
			tournamentToMergeFilePath = inputVerzFile.getPath();
			try {
				tournamentFromFile = JsonHandler.createFromFile(new File(tournamentToMergeFilePath), Tournament.class);

				JsonHandler.mergeTournaments(this._tournament, tournamentFromFile);

				saveTournamentToJsonFile();

				JOptionPane.showMessageDialog(null, "Successfully merged the two tournaments!");

				refresh();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error on loading tournament JSON file for merging!");
			}
		} else {
			chooser.setSelectedFile(null);
		}
		chooser.setVisible(false);
	}

	private void jMenuItemStartExitActionPerformed(java.awt.event.ActionEvent evt)
	{
		System.exit(0);
	}

	private void jButtonToolBarStartActionPerformed(java.awt.event.ActionEvent evt)
	{
		if (isPhaseBeforeFinished() == false) {
			showMessage("Please finish the previous phase first.");
			return;
		}
		if (tournamentRunner == null || tournamentRunner.isRunning() == false) {
			tournamentRunner = new TournamentRunner(this);
		}

		if (tournamentRunner.isRunning()) {
			showMessage("Tournament is running. Wait until the current played games are finished or stop it.");
			return;
		}

		tournamentRunner.setGames(getSelectedGames());
		tournamentRunner.setPhaseName(tournamentPlanTableModel.getCurrentPhase().getName());
		tournamentRunner.setGameDuration(_tournament.getGameDuration());
		tournamentRunner.startTournament();
		refresh();
	}

	private void jButtonToolBarStopActionPerformed(ActionEvent e)
	{
		tournamentRunner.stopTournament();
	}

	private void jtTournamentPlanPlanAddListSelectionListener(ListSelectionEvent e)
	{
		int index = -1;
		index = jtTournamentPlanPlan.getSelectedRow();
		index = jtTournamentPlanPlan.convertRowIndexToModel(index);

		if (index != -1) {
			int[] inRange = new int[1];
			Phase phase = tournamentPlanTableModel.getPhase(index, inRange);
			if (phase != null) {
				if (phase instanceof KoPhase) {
					tournamentPlanTableModel.setCurrentPhase(phase);
					Group group = grouPlanGamesTableModel.getGroup();
					if (group != null) {
						int numOfGames = group.getPlan().getGames().size();
						int numOfTeams = group.getTeams().size();
						grouPlanGamesTableModel.reset(numOfGames);
						tmstanding.reset(numOfTeams);
						comboBoxModel.setPhase(null);
						comboBoxModel.setIndex(0);
					}
				} else {
					tournamentPlanTableModel.setCurrentPhase(phase);
					comboBoxModel.setPhase(phase);
					grouPlanGamesTableModel.setGroup(((GroupPhase) phase).getGroups().get(0));
					tmstanding.setGroup(((GroupPhase) phase).getGroups().get(0));
					comboBoxModel.setIndex(0);
				}
			} else {
				Group group = grouPlanGamesTableModel.getGroup();
				if ((group != null) && (inRange[0] == 0)) {
					int numOfGames = group.getPlan().getGames().size();
					int numOfTeams = group.getTeams().size();
					grouPlanGamesTableModel.reset(numOfGames);
					tmstanding.reset(numOfTeams);
					comboBoxModel.setPhase(null);
					comboBoxModel.setIndex(-1);
				}
			}

			refreshWithOutTournamentPlan();
		}
	}

	private void jComoBoxGroupPlanGroupAddActionListener(ActionEvent e, Group currentGroup)
	{
		int index = comboBoxModel.getSelectedIndex();
		if (index == -1) {
			return;
		}

		Group newGroup = (Group) comboBoxModel.getElementAt(index);

		if (newGroup != null && newGroup != currentGroup) {
			List<Game> games;
			if (currentGroup != null) {
				games = currentGroup.getPlan().getGames();
				for (Game game : games) {
					game.removeChangeHandler(MainFrame.this);
				}
			}
			games = newGroup.getPlan().getGames();
			for (Game game : games) {
				game.addChangeHandler(MainFrame.this);
			}
			currentGroup = newGroup;
		}
	}

	private void openTournamentMouseAdapterAddChangeHandler(Tournament tournament)
	{
		_tournament = tournament;
		if (tournament != null) {
			tournamentPlanTableModel.setTournament(tournament);
			Phase phase = tournament.getPhases().get(0);
			comboBoxModel.setPhase(phase);
			comboBoxModel.setIndex(0);
			jComboBoxGroupPlanGroup.repaint();
			refresh();
		}
	}

	/**
	 * @return the list of teams loaded earlier
	 */
	private LinkedList<ITeam> getTeams()
	{
		// HACK: I think we should not get the teams from a mouse adapter, but
		// from the model
		if (openTeamsListener == null) {
			return null;
		}
		return openTeamsListener.getTeams();
	}

	/**
	 * @return true if we have a tournament available
	 */
	private boolean haveTournament()
	{
		return ((tournamentPlanTableModel != null) && (tournamentPlanTableModel.hasTournament()) &&
				(grouPlanGamesTableModel != null) && (tmstanding != null));
	}

	/**
	 * @return true if we have teams
	 */
	private boolean haveTeams()
	{
		LinkedList<ITeam> teams = getTeams();
		return teams != null && !teams.isEmpty();
	}

	private void jMenuItemTeamsEditListener(ActionEvent e)
	{
		// TEST KDO
		// LinkedHashSet<ITeam> teams = new LinkedHashSet<>(getTeams());
		//
		// TournamentBuilder builder = tournamentBuilderProvider.get();
		// try {
		// builder.withTeams(teams);
		// builder.numberOfClusters(1);
		// builder.gameDuration(60);
		// builder.addGroupPhase(1, 2);
		//
		// Tournament tournament = builder.build("TournamentForGame");
		//
		// TournamentRunner tournamentRunner = new TournamentRunner(
		// MainFrame.this);
		// Phase phase = tournament.getPhases().get(0);
		// tournamentRunner.setGames(((KoPhase) phase).getGames());
		// tournamentRunner.setPhaseName("Single game");
		// tournamentRunner.setGameDuration(tournament.getGameDuration());
		// tournamentRunner.startTournament();
		//
		// } catch (Exception exception) {
		// JOptionPane.showMessageDialog(null, exception.getMessage());
		// }

		LinkedList<ITeam> teams = getTeams();
		if (teams == null) {
			JOptionPane.showMessageDialog(null, "No teams available");
			return;
		}

		TeamEdit win = new TeamEdit(teams, openTeamsListener.getTeamFilePath());
		win.setVisible(true);
	}

	private void jMenuItemTournamentConfigurationWizardListener(ActionEvent e)
	{
		LinkedList<ITeam> teams = getTeams();
		if (teams == null) {
			JOptionPane.showMessageDialog(null, "No teams available.");
			return;
		}

		configurationWizard = new ConfigurationWizard(tournamentBuilderProvider, teams);
		configurationWizard.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e)
			{
				_tournament = configurationWizard.getTournament();
				if (_tournament != null) {
					tournamentPlanTableModel.setTournament(_tournament);
					Phase phase = _tournament.getPhases().get(0);
					comboBoxModel.setPhase(phase);
					saveTournamentToJsonFile();
					refresh();
				}
			}

		});
		configurationWizard.start();
	}

	private void jMenuItemTournamentTieBreakListener(ActionEvent e)
	{
		if (_tournament == null) {
			JOptionPane.showMessageDialog(null, "No Tournament created or loaded");
			return;
		}

		if (isPhaseBeforeFinished() == false) {
			showMessage("Please finish the previous phase first.");
			return;
		}
		if (tournamentRunner == null || tournamentRunner.isRunning() == false) {
			tournamentRunner = new TournamentRunner(MainFrame.this);
		}

		if (tournamentRunner.isRunning()) {
			showMessage("Tournament is running. Wait until the current played games are finished or stop it.");
			return;
		}

		tournamentRunner.setGameDuration(_tournament.getGameDuration());
		TieBreakView tieBreakView =
				new TieBreakView(tournamentPlanTableModel.getCurrentPhase(), tournamentRunner, this);
		tieBreakView.setVisible(true);

		refresh();
	}
}
