package magma.tools.competition.runner;

import java.io.File;
import java.io.IOException;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameNotFoundInTournamentException;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;
import magma.tools.competition.json.JsonHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class GameRunnerSimulation extends AbstractGameRunner
{
	File jsonFileRobocup2014;

	Game gameToReplay;

	String phaseName;

	public GameRunnerSimulation(Game gameToReplay, String phaseName)
	{
		this.phaseName = phaseName;
		this.gameToReplay = gameToReplay;
		this.jsonFileRobocup2014 = new File("assets/robocup2014results.json");
	}

	@Override
	public void run()
	{
		try {
			// Find the game in the tournament.
			// !!! Can lead to problems, if the games state != FINISHED in json !!!
			// -> wrong result calculation on reading the file
			Game game = findGameByPhaseName();

			// Game was found
			this.gameToReplay.setState(GameState.FINISHED);
			this.gameToReplay.getResult().setGuestTeamPoints(game.getResult().getGuestTeamPoints());
			this.gameToReplay.getResult().setHomeTeamPoints(game.getResult().getHomeTeamPoints());

			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Game findGameByPhaseName()
			throws GameNotFoundInTournamentException, JsonParseException, JsonMappingException, IOException
	{
		List<Group> groups;
		List<Game> games;

		Tournament tournament = JsonHandler.createFromFile(this.jsonFileRobocup2014, Tournament.class);

		for (Phase phase : tournament.getPhases()) {
			if (phase.getName().equals(this.phaseName)) {
				if (phase instanceof GroupPhase) {
					groups = ((GroupPhase) phase).getGroups();
					for (Group group : groups) {
						games = group.getPlan().getGames();
						for (Game tempGame : games) {
							// Comparison:
							// 1. isTieBreakGame equal?
							// 2. homeTeam.getName() equal?
							// 3. guestTeam.getName() equal?
							// -> return found game
							if (tempGame.isDecisionGame() == this.gameToReplay.isDecisionGame() &&
									tempGame.getHomeTeam().getName().equals(
											this.gameToReplay.getHomeTeam().getName()) &&
									tempGame.getGuestTeam().getName().equals(
											this.gameToReplay.getGuestTeam().getName())) {
								return tempGame;
							}
						}
					}
				} else {
					games = ((KoPhase) phase).getGames();
					for (Game tempGame : games) {
						// Comparison:
						// 1. isTieBreakGame equal?
						// 2. homeTeam.getName() equal?
						// 3. guestTeam.getName() equal?
						// -> return found game
						if (tempGame.isDecisionGame() == this.gameToReplay.isDecisionGame() &&
								tempGame.getHomeTeam().getName().equals(this.gameToReplay.getHomeTeam().getName()) &&
								tempGame.getGuestTeam().getName().equals(this.gameToReplay.getGuestTeam().getName())) {
							return tempGame;
						}
					}
				}
			}
		}
		throw new GameNotFoundInTournamentException(this.gameToReplay);
	}

	@Override
	public void startGame()
	{
		this.start();
	}

	@Override
	public SimulationEvent getSimulationEvent()
	{
		return null;
	}

	@Override
	public void setFinished(boolean b)
	{
	}
}
