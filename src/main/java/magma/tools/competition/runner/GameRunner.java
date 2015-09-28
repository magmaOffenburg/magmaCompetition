package magma.tools.competition.runner;

import java.io.IOException;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.logmanager.LogManager;
import magma.tools.competition.monitor.IMonitorListener;
import magma.tools.competition.monitor.MonitorAdapter;
import magma.tools.competition.monitor.MonitorAdapterConfiguration;
import magma.tools.competition.monitor.MonitorAdapterNotConnectedException;
import magma.tools.competition.monitor.MonitorException;
import magma.tools.competition.processStateChecker.IProcessStateChecker;
import magma.tools.competition.processStateChecker.ProcessStateChecker;
import magma.tools.competition.sshclient.SSHClient;
import magma.tools.competition.sshclient.SSHClientException;
import magma.tools.competition.sshclient.ISSHClient.AuthenticationMethods;
import magma.tools.competition.util.ClusterConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameRunner extends AbstractGameRunner implements IMonitorListener
{

	private static final Logger logger = LoggerFactory
			.getLogger(GameRunner.class);

	private SSHClient server;

	private SSHClient serverStateCheckerConnection;

	private SSHClient conTeam1;

	private SSHClient conTeam2;

	private Game game;

	private String phaseName;

	private MonitorAdapter monitorAdapter;

	private MonitorAdapterConfiguration monitorAdapterConfiguration;

	private SimulationEvent event;

	private GameRunnerState state;

	private String knownHostPath;

	private IProcessStateChecker processStateChecker;

	private ClusterConfiguration configuration;

	private boolean finished;

	private LogManager logManager;

	private ISimulationEventHandler eventHandler;

	private boolean restore;

	public GameRunner(Game gameToReplay, String phaseName,
			ISimulationEventHandler eventHandler)
	{
		try {
			this.eventHandler = eventHandler;
			this.configuration = ClusterConfiguration.get();
			this.state = GameRunnerState.GAME_RUNNER_STATE_INIT;
			this.phaseName = phaseName;
			this.game = gameToReplay;
			this.knownHostPath = this.configuration.getKnownHostPath();
			this.event = null;
			this.finished = false;
		} catch (IOException e) {
			this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_INIT;
		}
	}

	@Override
	public void startGame()
	{
		this.start();
	}

	@Override
	public void run()
	{
		logger.debug("Starting game.");
		GameRunnerConfig conf = new GameRunnerConfig();

		while (!finished) {
			switch (this.state) {
			case GAME_RUNNER_STATE_INIT: {
				init(conf);
				break;
			}
			case GAME_RUNNER_STATE_CONNECT: {
				connect(conf);
				break;
			}
			case GAME_RUNNER_STATE_SERVER_START: {
				serverStart(conf);
				break;
			}
			case GAME_RUNNER_STATE_TEAMS_START: {
				teamsStart(conf);
				break;
			}
			case GAME_RUNNER_STATE_GAME_RUNNING: {
				gameRunning(conf);
				break;
			}
			case GAME_RUNNER_STATE_PENALTY_SHOOTOUT: {
				penaltyShootout(conf);
				break;
			}
			case GAME_RUNNER_STATE_ERROR_INIT: {
				errorInit();
				break;
			}
			case GAME_RUNNER_STATE_ERROR_CONNECT: {
				errorConnect();
				break;
			}
			case GAME_RUNNER_STATE_ERROR_TEAM: {
				errorTeam();
				break;
			}
			case GAME_RUNNER_STATE_ERROR_SERVER: {
				errorServer();
				break;
			}
			case GAME_RUNNER_STATE_GAME_FINISH: {
				gameFinish();
				break;
			}
			case GAME_RUNNER_STATE_RESTART: {
				restart();
				break;
			}
			default:

			}
		}
	}

	private void init(GameRunnerConfig conf)
	{
		logger.debug("Reading cluster configuration.");
		conf.serverIP = configuration.getServerAddress();
		conf.teamLeftIP = configuration.getTeamLeftAddress();
		conf.teamRightIP = configuration.getTeamRightAddress();
		conf.serverUsername = configuration.getServerUserName();
		conf.serverPassword = configuration.getServerUserPassword();
		this.state = GameRunnerState.GAME_RUNNER_STATE_CONNECT;
	}

	private void connect(GameRunnerConfig conf)
	{
		logger.debug("Connecting to the server.");
		server = createConnection(conf.serverIP, conf.serverUsername,
				conf.serverPassword);

		if (isFinished()) {
			event = SimulationEvent.GAME_STOPPED;
			cleanUp();
			return;
		}

		serverStateCheckerConnection = createConnection(conf.serverIP,
				conf.serverUsername, conf.serverPassword);

		if (isFinished()) {
			event = SimulationEvent.GAME_STOPPED;
			cleanUp();
			return;
		}
		processStateChecker = new ProcessStateChecker(
				serverStateCheckerConnection);
		// logManager = new LogManager(server);
		logManager = new LogManager();

		if (isFinished()) {
			event = SimulationEvent.GAME_STOPPED;
			cleanUp();
			return;
		}

		if (server != null) {
			logger.debug("Connecting to team 1.");
			conTeam1 = createConnection(conf.teamLeftIP, conf.serverUsername,
					conf.serverPassword);

			if (isFinished()) {
				event = SimulationEvent.GAME_STOPPED;
				cleanUp();
				return;
			}

			logger.debug("Connecting to team 2.");
			conTeam2 = createConnection(conf.teamRightIP, conf.serverUsername,
					conf.serverPassword);

			if (isFinished()) {
				event = SimulationEvent.GAME_STOPPED;
				cleanUp();
				return;
			}

			if (conTeam1 == null || conTeam2 == null) {
				this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_TEAM;
			} else {
				if (conf.penalty_shootout == false) {
					this.state = GameRunnerState.GAME_RUNNER_STATE_SERVER_START;
				} else {
					this.state = GameRunnerState.GAME_RUNNER_STATE_PENALTY_SHOOTOUT;
				}
			}
		} else {
			this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_CONNECT;
		}
	}

	private void serverStart(GameRunnerConfig conf)
	{
		// if (restore) {
		logger.debug("Storing current log file.");
		try {
			logManager.storeLogFiles(phaseName, game);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// }
		logger.debug("Starting server.");
		startServer(conf.serverIP, conf.serverUsername, conf.serverPassword);
		try {
			startMonitorAdapter();
			this.state = GameRunnerState.GAME_RUNNER_STATE_TEAMS_START;

		} catch (MonitorException e) {
			this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_SERVER;
		}
	}

	private void startMonitorAdapter() throws MonitorException
	{
		monitorAdapterConfiguration = new MonitorAdapterConfiguration();
		monitorAdapterConfiguration
				.setHost(this.configuration.getServerAddress());
		monitorAdapter = new MonitorAdapter(monitorAdapterConfiguration);
		monitorAdapter.startMonitor();
		monitorAdapter.addMonitorListener(this);
	}

	private void teamsStart(GameRunnerConfig conf)
	{
		try {
			ITeam team1 = game.getHomeTeam();
			ITeam team2 = game.getGuestTeam();

			if (conf.secondHalf) {
				team1 = game.getGuestTeam();
				team2 = game.getHomeTeam();
			}
			logger.debug("Starting team '{}'.", team1.getName());
			String scriptName = "start.sh";
			SimulationEventResponse response = startTeam(team1, conTeam1,
					conf.serverPassword, conf.serverIP, 11, scriptName);
			if (response == null || response == SimulationEventResponse.PLAY) {
				logger.debug("Starting team '{}'.", team2.getName());
				response = startTeam(team2, conTeam2, conf.serverPassword,
						conf.serverIP, 11, scriptName);
			}
			if (response == SimulationEventResponse.CANCEL) {
				event = SimulationEvent.GAME_STOPPED;
				this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_TEAM;
			} else if (response == SimulationEventResponse.RESTART_GAME) {
				this.state = GameRunnerState.GAME_RUNNER_STATE_RESTART;
			} else {
				this.state = GameRunnerState.GAME_RUNNER_STATE_GAME_RUNNING;
			}

		} catch (MonitorAdapterNotConnectedException exception) {
			this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_TEAM;
		}
	}

	private void gameRunning(GameRunnerConfig conf)
	{
		try {
			if (restore) {
				logger.debug("Restoring game.");
				try {
					logManager.restoreFromLogFiles(phaseName, game);
					// int left = game.getResult().getHomeTeamPoints();
					// int right = game.getResult().getGuestTeamPoints();
					// monitorAdapter.setScore(left, right);
				} catch (IOException e) {
					// Can never happen
				}
			}

			logger.debug("Waiting for kick-off.");
			while (monitorAdapter.getTime() <= 0 && (isFinished() == false)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.debug("Game running.");

			if (isFinished() == false) {
				game.setState(GameState.STARTED);
				game.getResult().setGuestTeamPoints(0);
				game.getResult().setHomeTeamPoints(0);
			}

			logger.debug("Waiting for game to end.");
			if (conf.extraTime == false) {
				conf.gameDuration = 300.0f;
			} else {
				conf.gameDuration = 150.0f;
			}

			while ((monitorAdapter.getTime() < conf.gameDuration)
					&& (isFinished() == false)) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (isFinished()) {
				event = SimulationEvent.GAME_STOPPED;
				cleanUp();
			} else {

				if (conf.secondHalf) {
					if ((game.isDecisionGame() == true)
							&& (game.getResult().getHomeTeamPoints() == game
									.getResult().getGuestTeamPoints())) {
						this.state = GameRunnerState.GAME_RUNNER_STATE_RESTART;

						if (conf.extraTime == true) {
							conf.penalty_shootout = true;
						}

						conf.extraTime = true;
						conf.secondHalf = false;

					} else {
						this.state = GameRunnerState.GAME_RUNNER_STATE_GAME_FINISH;
					}
				} else {
					conf.secondHalf = true;
					this.state = GameRunnerState.GAME_RUNNER_STATE_RESTART;
				}
			}
		} catch (MonitorAdapterNotConnectedException exception) {
			this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_SERVER;
		}
	}

	private void penaltyShootout(GameRunnerConfig conf)
	{
		int guestTeamPoints = game.getResult().getGuestTeamPoints();
		int homeTeamPoints = game.getResult().getHomeTeamPoints();
		int max = 20;
		int i = 0;

		while (i < max) {
			String scriptTeam1 = "start_penalty_kicker.sh";
			String scriptTeam2 = "start_penalty_goalie.sh";
			ITeam team1 = game.getHomeTeam();
			ITeam team2 = game.getGuestTeam();

			try {
				SimulationEventResponse response = null;
				if (i % 2 != 0) {
					team1 = game.getGuestTeam();
					team2 = game.getHomeTeam();
				}

				startServer(conf.serverIP, conf.serverUsername, conf.serverPassword);

				try {
					startMonitorAdapter();
					monitorAdapter.setScore(homeTeamPoints, guestTeamPoints);
				} catch (MonitorException e1) {
					e1.printStackTrace();
				}

				response = startTeam(team1, conTeam1, conf.serverPassword,
						conf.serverIP, 1, scriptTeam1);

				if (response == null || response == SimulationEventResponse.PLAY) {
					response = startTeam(team2, conTeam2, conf.serverPassword,
							conf.serverIP, 1, scriptTeam2);

					/*
					 * FIXME: Workaround for team detection for first penalty after
					 * server start
					 */
					killTeam(team2, conTeam2, conf.serverPassword);

					response = startTeam(team2, conTeam2, conf.serverPassword,
							conf.serverIP, 1, scriptTeam2);
					/* End FIXME */
				}

				if (response == SimulationEventResponse.CANCEL) {
					event = SimulationEvent.GAME_STOPPED;
					this.state = GameRunnerState.GAME_RUNNER_STATE_ERROR_TEAM;
				} else if (response == SimulationEventResponse.RESTART_GAME) {
					this.state = GameRunnerState.GAME_RUNNER_STATE_RESTART;
				}
			} catch (MonitorAdapterNotConnectedException e) {
				e.printStackTrace();
			}

			try {
				while (monitorAdapter.getTime() <= 0 && (isFinished() == false)) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (MonitorAdapterNotConnectedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			int timeout = 60000;
			long startTime = System.currentTimeMillis();
			while (((System.currentTimeMillis() - startTime) < timeout)
					&& (guestTeamPoints == game.getResult().getGuestTeamPoints())
					&& (homeTeamPoints == game.getResult().getHomeTeamPoints())
					&& (isFinished() == false)) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (isFinished() == false) {
				if (i < 10) {
					int shoots = (int) Math.floor((10 - i) / 2.0);

					logger.debug("Round: {}; Shoots remaining: {}", i, shoots);

					int penaltyGoalsHomeTeam = game.getResult().getHomeTeamPoints();
					int penaltyGoalsGuestTeam = game.getResult()
							.getGuestTeamPoints();

					if ((Math.abs(penaltyGoalsHomeTeam - penaltyGoalsGuestTeam)) > shoots) {
						break;
					}
				} else {
					int penaltyGoalsHomeTeam = game.getResult().getHomeTeamPoints();
					int penaltyGoalsGuestTeam = game.getResult()
							.getGuestTeamPoints();

					if (penaltyGoalsHomeTeam != penaltyGoalsGuestTeam
							&& ((i % 2) != 0)) {
						break;
					}
				}

				homeTeamPoints = game.getResult().getHomeTeamPoints();
				guestTeamPoints = game.getResult().getGuestTeamPoints();
				i++;

				killTeam(team1, conTeam1, conf.serverPassword);
				killTeam(team2, conTeam2, conf.serverPassword);
				try {
					monitorAdapter.stopMonitor();
				} catch (MonitorException e) {
					e.printStackTrace();
				}

				processStateChecker.terminateProcess("rcssserver3d");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				cleanUp();
				event = SimulationEvent.GAME_STOPPED;
				break;
			}
		}

		if (isFinished() == false) {
			this.state = GameRunnerState.GAME_RUNNER_STATE_GAME_FINISH;
		}
	}

	private void errorInit()
	{
		event = SimulationEvent.INVALID_CONFIGURATION;
		setFinished(true);
	}

	private void errorConnect()
	{
		event = SimulationEvent.CONNECTION_PROBLEM;
		setFinished(true);
	}

	private void errorTeam()
	{
		cleanUp();
		setFinished(true);
	}

	private void errorServer()
	{
		cleanUp();
		SimulationEventResponse response = SimulationEventResponse.values()[eventHandler
				.handleSimulationEvent(SimulationEvent.SERVER_ERROR,
						"Server Error: Do you want to restart the game?")];
		if (response == SimulationEventResponse.RESTART_GAME) {
			restore = true;
			this.state = GameRunnerState.GAME_RUNNER_STATE_RESTART;
		} else {
			event = SimulationEvent.SERVER_ERROR;
			setFinished(true);
		}
	}

	private void gameFinish()
	{
		cleanUp();
		game.setState(GameState.FINISHED);
		setFinished(true);
	}

	private void restart()
	{
		cleanUp();
		this.state = GameRunnerState.GAME_RUNNER_STATE_CONNECT;
	}

	private void cleanUp()
	{
		try {
			if (monitorAdapter != null) {
				monitorAdapter.stopMonitor();
			}
		} catch (IllegalStateException | MonitorException exception) {
			logger.debug("Monitor Adapter is already disconnected.");
		}
		String serverPassword = configuration.getServerUserPassword();

		if (processStateChecker != null) {
			processStateChecker.terminateProcess("rcssserver3d");
		}

		if (conTeam1 != null) {
			killTeam(game.getGuestTeam(), conTeam1, serverPassword);
		}

		if (conTeam2 != null) {
			killTeam(game.getHomeTeam(), conTeam2, serverPassword);
		}

		stopConnection(server);
		stopConnection(conTeam1);
		stopConnection(conTeam2);
		stopConnection(serverStateCheckerConnection);
	}

	private void stopConnection(SSHClient sshConnection)
	{
		if (sshConnection != null && sshConnection.isConnected()) {
			sshConnection.disconnect();
		}
	}

	private void startServer(String ip, String username, String password)
	{
		if (server.isConnected() == true) {
			server.sendCmd("rcssserver3d");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private SSHClient createConnection(String ip, String username, String pasword)
	{
		SSHClient connection = null;

		try {
			connection = new SSHClient(username, ip);
			connection.setPreferredAuthentication(AuthenticationMethods.PASSWORD);
			connection.setPassword(pasword);
			connection.setKnownHosts(knownHostPath);
			connection.connect();
		} catch (SSHClientException e1) {
			e1.printStackTrace();
		}

		return connection;
	}

	private SimulationEventResponse startTeam(ITeam team, SSHClient connection,
			String serverPassword, String serverIP, int numOfPlayers,
			String scriptName) throws MonitorAdapterNotConnectedException
	{
		String cmd = "echo -e '" + serverPassword + "\n' | sudo -k -S su "
				+ team.getUsername() + " -c 'cd " + team.getPathToScriptFile()
				+ " && ./" + scriptName + " " + serverIP + "'";

		if (connection.isConnected()) {
			connection.sendCmd(cmd);

			int timeout = 1000 + 1000 * numOfPlayers;
			SimulationEventResponse response = SimulationEventResponse.WAIT;

			while (response == SimulationEventResponse.WAIT) {
				long startTime = System.currentTimeMillis();
				int players = 0;
				while (((players = monitorAdapter
						.getNumberOfPlayers(team.getName())) != numOfPlayers)
						&& ((System.currentTimeMillis() - startTime) < timeout)) {
				}
				logger.debug("Team '{}' currently has {} players.", team.getName(),
						players);
				if (players != numOfPlayers) {

					try {
						monitorAdapter.stopMonitor();

						startMonitorAdapter();
					} catch (MonitorException e1) {
						e1.printStackTrace();
					}

					players = monitorAdapter.getNumberOfPlayers(team.getName());
					if (players != numOfPlayers) {
						response = SimulationEventResponse.values()[eventHandler
								.handleSimulationEvent(
										SimulationEvent.NOT_ALL_PLAYERS_ON_FILED,
										String.format(
												"Team '%s' is missing players. Do you want to wait, restart or stop the game?",
												team.getName()))];
					}
				} else {
					response = null;
				}
			}
			return response;
		}
		return null;
	}

	private void killTeam(ITeam team, SSHClient connection, String serverPassword)
	{
		String cmd = "echo -e '" + serverPassword + "\n' | sudo -k -S su "
				+ team.getUsername() + " -c 'cd " + team.getPathToScriptFile()
				+ " && ./kill.sh'";

		if (connection.isConnected()) {
			connection.sendCmd(cmd);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public SimulationEvent getSimulationEvent()
	{
		return event;
	}

	@Override
	public void onUpdate(MonitorAdapter adapter)
	{
		try {
			int guest = adapter.getRightTeamScore();
			int home = adapter.getLeftTeamScore();

			if (guest != game.getResult().getGuestTeamPoints()) {
				game.getResult().setGuestTeamPoints(guest);
			}

			if (home != game.getResult().getHomeTeamPoints()) {
				game.getResult().setHomeTeamPoints(home);
			}
		} catch (MonitorAdapterNotConnectedException exception) {
			// TODO handle me
		}
	}

	public boolean isFinished()
	{
		return finished;
	}

	@Override
	public void setFinished(boolean finished)
	{
		this.finished = finished;
	}

	class GameRunnerConfig
	{
		String serverIP = null;

		String teamLeftIP = null;

		String teamRightIP = null;

		String serverUsername = null;

		String serverPassword = null;

		boolean secondHalf = false;

		boolean penalty_shootout = false;

		boolean extraTime = false;

		float gameDuration = 300.0f;
	}
}
