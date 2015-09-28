package magma.tools.competition.tournamentRunner;

import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.runner.AbstractGameRunner;
import magma.tools.competition.runner.GameRunner;
import magma.tools.competition.runner.ISimulationEventHandler;
import magma.tools.competition.runner.SimulationEvent;

public class TournamentRunner extends Thread implements ITournamentRunner
{

	AbstractGameRunner gameRunner;

	private String phaseName;

	private List<Game> games;

	private double duration;

	private ISimulationEventHandler errorHandler;

	private boolean finished;

	public TournamentRunner(ISimulationEventHandler errorHandler)
	{
		phaseName = "";
		games = null;
		duration = 0;
		this.errorHandler = errorHandler;
	}

	@Override
	public void run()
	{
		gameRunner = null;
		if (games != null && duration > 0) {
			for (int i = 0; i < games.size() && isFinished() == false; i++) {
				if (games.get(i).getState() != GameState.FINISHED) {
					gameRunner = new GameRunner(games.get(i), phaseName,
							errorHandler);
					gameRunner.start();
					try {
						gameRunner.join();
						if (gameRunner.getSimulationEvent() == SimulationEvent.GAME_STOPPED
								|| gameRunner.getSimulationEvent() == SimulationEvent.SERVER_ERROR) {
							games.get(i).setState(GameState.PLANNED);
							games.get(i).getResult().setGuestTeamPoints(0);
							games.get(i).getResult().setGuestTeamPoints(0);
						}
					} catch (InterruptedException e) {
						gameRunner.setFinished(true);
						setFinished(true);
					}
				}
			}

			boolean allFinished = true;

			for (int j = 0; j < games.size(); j++) {
				if (games.get(j).getState() != GameState.FINISHED) {
					allFinished = false;
					break;
				}
			}

			if (allFinished == true) {
				errorHandler.handleSimulationEvent(SimulationEvent.GAMES_FINISHED,
						"All Selected Games finished. Select next.");

			} else {
				if (gameRunner.getSimulationEvent() != null) {
					switch (gameRunner.getSimulationEvent()) {
					case GAME_STOPPED:
						errorHandler.handleSimulationEvent(
								SimulationEvent.GAME_STOPPED, "Stopped successfully.");
						break;
					default:
						errorHandler
								.handleSimulationEvent(SimulationEvent.GAME_STOPPED,
										"Stopped with errors. Maybe not all games have been played.");
						break;
					}
				}
			}
		} else {
			errorHandler.handleSimulationEvent(
					SimulationEvent.INVALID_CONFIGURATION, "Invalid configuration");
		}
	}

	@Override
	public void startTournament()
	{
		this.start();
	}

	@Override
	public void stopTournament()
	{
		if (gameRunner != null) {
			gameRunner.setFinished(true);
		}
		setFinished(true);
	}

	@Override
	public void setGames(List<Game> games)
	{
		if (games != null) {
			this.games = games;
		}
	}

	@Override
	public void setGameDuration(double gameDuration)
	{
		if (gameDuration > 0) {
			this.duration = gameDuration;
		}
	}

	@Override
	public boolean isRunning()
	{
		return this.isAlive();
	}

	@Override
	public void setPhaseName(String phaseName)
	{
		this.phaseName = phaseName;
	}

	private boolean isFinished()
	{
		return finished;
	}

	private void setFinished(boolean finished)
	{
		this.finished = finished;
	}

}
