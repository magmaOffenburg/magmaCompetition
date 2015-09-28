package magma.tools.competition.json;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameResult;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.GroupResultFactory;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.TeamFactory;
import magma.tools.competition.domain.Tournament;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class JsonHandler
{

	private static ObjectMapper mapper;

	@Inject
	private static Provider<Date> dateProvider;

	@Inject
	private static TeamFactory teamFactory;

	@Inject
	private static GroupResultFactory resultFactory;

	public static void exportToFile(Object objectToExport, File file)
			throws IOException
	{
		ObjectMapper objMapper = getMapper();
		objMapper.writeValue(file, objectToExport);
	}

	public static <T> T createFromFile(File file, Class<T> theClass)
			throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper objMapper = getMapper();
		T objectFromJson = objMapper.readValue(file, theClass);
		return objectFromJson;
	}

	private static ObjectMapper getMapper()
	{
		if (mapper == null) {
			mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			InjectableValues values = new InjectableValues.Std()
					.addValue("dateProvider", dateProvider)
					.addValue("groupResultFactory", resultFactory)
					.addValue("teamFactory", teamFactory);
			mapper.setInjectableValues(values);
		}
		return mapper;
	}

	public static void mergeTournaments(Tournament localTournament,
			Tournament mergeTournament) throws JsonParseException,
			JsonMappingException, IOException
	{
		List<Phase> phases;
		List<Group> groups;
		List<Game> games;

		phases = localTournament.getPhases();

		for (int iPhases = 0; iPhases < phases.size(); iPhases++) {

			if (phases.get(iPhases) instanceof GroupPhase) {

				GroupPhase groupPhase = (GroupPhase) phases.get(iPhases);

				groups = groupPhase.getGroups();

				for (int iGroups = 0; iGroups < groups.size(); iGroups++) {

					games = groups.get(iGroups).getPlan().getGames();

					for (int iGames = 0; iGames < games.size(); iGames++) {

						Game game = games.get(iGames);

						if (!(game.getState().equals(GameState.STARTED) || game
								.getState().equals(GameState.FINISHED))
								&& game.getResult().getHomeTeamPoints() == 0
								&& game.getResult().getGuestTeamPoints() == 0) {
							// Game status is NOT started and NOT finished AND stand is
							// 0 : 0
							// --> not played on this cluster yet --> take game from
							// other cluster
							GameState otherClusterGameState = ((GroupPhase) mergeTournament
									.getPhases().get(iPhases)).getGroups().get(iGroups)
									.getPlan().getGames().get(iGames).getState();
							GameResult otherClusterGameResult = ((GroupPhase) mergeTournament
									.getPhases().get(iPhases)).getGroups().get(iGroups)
									.getPlan().getGames().get(iGames).getResult();

							game.setState(otherClusterGameState);
							game.getResult().setHomeTeamPoints(
									otherClusterGameResult.getHomeTeamPoints());
							game.getResult().setGuestTeamPoints(
									otherClusterGameResult.getGuestTeamPoints());
						}
					}
				}
			} else {
				KoPhase koPhase = (KoPhase) phases.get(iPhases);

				games = koPhase.getGames();

				for (int iGames = 0; iGames < games.size(); iGames++) {

					Game game = games.get(iGames);

					if (!(game.getState().equals(GameState.STARTED) || game
							.getState().equals(GameState.FINISHED))
							&& game.getResult().getHomeTeamPoints() == 0
							&& game.getResult().getGuestTeamPoints() == 0) {
						// Game status is NOT started and NOT finished AND stand is 0
						// : 0
						// --> not played on this cluster yet --> take game from other
						// cluster
						GameState otherClusterGameState = ((KoPhase) mergeTournament
								.getPhases().get(iPhases)).getGames().get(iGames)
								.getState();
						GameResult otherClusterGameResult = ((KoPhase) mergeTournament
								.getPhases().get(iPhases)).getGames().get(iGames)
								.getResult();

						game.setState(otherClusterGameState);
						game.getResult().setHomeTeamPoints(
								otherClusterGameResult.getHomeTeamPoints());
						game.getResult().setGuestTeamPoints(
								otherClusterGameResult.getGuestTeamPoints());
					}
				}
			}
		}
	}
}
