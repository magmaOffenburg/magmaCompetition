package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class GroupResult
{

	private List<Object[]> result;

	private GameFactory factory;

	@Inject
	GroupResult(GroupResultCalculationStrategy strategy,
			@Assisted("games") List<Game> games, GameFactory factory)
	{
		result = strategy.calculateResult(games);
		this.factory = factory;
	}

	public List<ITeam> getTeamsOnRank(int rank)
	{
		List<ITeam> teams = new ArrayList<>();
		for (Object[] arr : result) {
			if (rank == (int) arr[1]) {
				teams.add((ITeam) arr[0]);
			}
		}
		return teams;
	}

	public ITeam getTeamAtIndex(int index)
	{
		if (index < result.size()) {
			return (ITeam) result.get(index)[0];
		} else {
			return null;
		}
	}

	public int getRankOfTeam(ITeam team)
	{
		checkNotNull(team);
		for (Object[] arr : result) {
			if (arr[0] == team) {
				return (int) arr[1];
			}
		}
		return -1;
	}

	public boolean isFinal()
	{
		for (int rank = 0; rank < result.size(); rank++) {
			List<ITeam> teamsOnRank = getTeamsOnRank(rank);
			if (teamsOnRank.size() > 1) {
				return false;
			}
		}
		return true;
	}

	public int getPoints(ITeam team)
	{
		int pos = findTeam(team);
		return (int) result.get(pos)[5];
	}

	public int getGoals(ITeam team)
	{
		int pos = findTeam(team);
		return (int) result.get(pos)[3];
	}

	public int getGoalsAgainst(ITeam team)
	{
		int pos = findTeam(team);
		return (int) result.get(pos)[4];
	}

	public int getGames(ITeam team)
	{
		int pos = findTeam(team);
		return (int) result.get(pos)[2];
	}

	private int findTeam(ITeam team)
	{
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i)[0] == team) {
				return i;
			}
		}
		throw new IllegalArgumentException(
				"Given team not found in group results.");
	}

	public List<Game> getTieBreakGames()
	{
		List<Game> games = new ArrayList<Game>();
		List<ITeam> teams = new ArrayList<ITeam>();

		for (int i = 0; i < result.size(); i++) {
			teams = getTeamsOnRank(i);
			if (teams.size() > 1) {
				Game game = factory.create(teams.get(0), teams.get(1));
				game.setDecisionGame(true);
				games.add(game);
			}
		}

		return games;
	}

	public boolean isTieBreakNeeded()
	{
		boolean ret = false;

		List<ITeam> teams = new ArrayList<ITeam>();
		for (int i = 0; i < result.size(); i++) {
			teams = getTeamsOnRank(i);
			if (teams.size() > 1) {
				ret = true;
			}
		}

		return ret;
	}

}
