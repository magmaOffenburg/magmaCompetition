package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.inject.Inject;

public class GameUtil
{
	private GameFactory factory;

	@Inject
	public GameUtil(GameFactory factory)
	{
		this.factory = factory;
	}

	public List<Game> createGroupPhaseGames(LinkedHashSet<ITeam> teams)
	{
		List<Game> games = generateGroupPhaseGames(teams);
		return reorder(games, teams.size());
	}

	public List<Game> createKoPhaseGames(LinkedHashSet<ITeam> teams)
	{
		checkNotNull(teams);
		int teamsSize = teams.size();
		checkArgument((teamsSize >= 2) && ((teamsSize & (~teamsSize + 1)) == teamsSize),
				"The number of teams in a KO Phase must be >= 2 and a power of two, given '%s'.", teamsSize);
		List<Game> games = new ArrayList<>();
		Iterator<ITeam> iterator = teams.iterator();
		while (iterator.hasNext()) {
			ITeam homeTeam = iterator.next();
			ITeam guestTeam = iterator.next();
			Game game = factory.create(homeTeam, guestTeam);
			game.setDecisionGame(true);
			games.add(game);
		}
		return games;
	}

	private List<Game> generateGroupPhaseGames(LinkedHashSet<ITeam> teams)
	{
		List<Game> games = new ArrayList<>();
		int groupSize = teams.size();
		ITeam[] arr = teams.toArray(new ITeam[groupSize]);
		for (int i = 0; i < groupSize - 1; i++) {
			for (int j = i + 1; j < groupSize; j++) {
				Game game = factory.create(arr[i], arr[j]);
				games.add(game);
			}
		}
		return games;
	}

	private List<Game> reorder(List<Game> games, int groupSize)
	{
		List<Game> tempList = new ArrayList<>();
		List<Game> orderedGames = new ArrayList<>();
		tempList.addAll(games);
		while (tempList.size() > 0) {
			int index = (int) (Math.random() * tempList.size());
			orderedGames.add(tempList.get(index));
			tempList.remove(index);
		}
		return orderedGames;
	}
}
