package magma.tools.competition.domain;

import java.util.ArrayList;

/**
 * This class counts the points, goals and goals against for each team included
 * in a list of games.
 *
 * @author Simon Gutjahr
 *
 */
public class TeamResultCalculationStrategy
{
	public TeamResultCalculationStrategy()
	{
	}

	/**
	 * Returns a List of Object Arrays. Each Array represents a team.
	 *
	 * @param games A List of games
	 * @return An List of Object Arrays. Structure of the Object Array for each
	 *         team: [0] = Team (Team Object), [1] = Position (int), [2] = Games
	 *         (int), [3] = Goals (int), [4] = Goals against (int), [5] = Points
	 *         (int)
	 */
	public ArrayList<Object[]> getTeamData(ArrayList<Game> games)
	{
		ArrayList<Object[]> unsortetResult = new ArrayList<Object[]>();

		for (int i = 0; i < games.size(); i++) {
			/*
			 * [0] = Team [1] = Pos [2] = Games [3] = Goals [4] = Goals against [5]
			 * = Points
			 */
			Object[] homeTeam = new Object[6];
			Object[] guestTeam = new Object[6];

			Game game = games.get(i);

			ITeam t1 = game.getHomeTeam();
			ITeam t2 = game.getGuestTeam();

			homeTeam = initTeamArray(t1, unsortetResult);
			guestTeam = initTeamArray(t2, unsortetResult);

			if (game.getResult().getHomeTeamPoints() > game.getResult().getGuestTeamPoints()) {
				victoryHomeTeam(homeTeam, guestTeam, game);
			} else if (game.getResult().getHomeTeamPoints() < game.getResult().getGuestTeamPoints()) {
				victoryGuestTeam(guestTeam, homeTeam, game);
			} else {
				draw(homeTeam, guestTeam, game);
			}
		}

		return unsortetResult;
	}

	private Object[] initTeamArray(ITeam team, ArrayList<Object[]> unsortedList)
	{
		Object[] teamArray = new Object[6];

		boolean isKnown = false;
		for (int j = 0; j < unsortedList.size(); j++) {
			Object[] tmp = unsortedList.get(j);
			if (((ITeam) tmp[0]).equals(team)) {
				teamArray = tmp;
				isKnown = true;
				break;
			}
		}

		if (isKnown == false) {
			initObjectArray(teamArray, team);
			unsortedList.add(teamArray);
		}

		return teamArray;
	}

	private void initObjectArray(Object[] teamArray, ITeam team)
	{
		teamArray[0] = team;
		teamArray[1] = 0;
		teamArray[2] = 0;
		teamArray[3] = 0;
		teamArray[4] = 0;
		teamArray[5] = 0;
	}

	private void victoryHomeTeam(Object winningTeam[], Object loosingTeam[], Game game)
	{
		winningTeam[2] = (int) winningTeam[2] + 1;
		winningTeam[3] = (int) winningTeam[3] + game.getResult().getHomeTeamPoints();
		winningTeam[4] = (int) winningTeam[4] + game.getResult().getGuestTeamPoints();
		winningTeam[5] = (int) winningTeam[5] + 3;

		loosingTeam[2] = (int) loosingTeam[2] + 1;
		loosingTeam[3] = (int) loosingTeam[3] + game.getResult().getGuestTeamPoints();
		loosingTeam[4] = (int) loosingTeam[4] + game.getResult().getHomeTeamPoints();
	}

	private void victoryGuestTeam(Object winningTeam[], Object loosingTeam[], Game game)
	{
		winningTeam[2] = (int) winningTeam[2] + 1;
		winningTeam[3] = (int) winningTeam[3] + game.getResult().getGuestTeamPoints();
		winningTeam[4] = (int) winningTeam[4] + game.getResult().getHomeTeamPoints();
		winningTeam[5] = (int) winningTeam[5] + 3;

		loosingTeam[2] = (int) loosingTeam[2] + 1;
		loosingTeam[3] = (int) loosingTeam[3] + game.getResult().getHomeTeamPoints();
		loosingTeam[4] = (int) loosingTeam[4] + game.getResult().getGuestTeamPoints();
	}

	private void draw(Object hometeam[], Object guestTeam[], Game game)
	{
		hometeam[2] = (int) hometeam[2] + 1;
		hometeam[3] = (int) hometeam[3] + game.getResult().getHomeTeamPoints();
		hometeam[4] = (int) hometeam[4] + game.getResult().getGuestTeamPoints();
		hometeam[5] = (int) hometeam[5] + 1;

		guestTeam[2] = (int) guestTeam[2] + 1;
		guestTeam[3] = (int) guestTeam[3] + game.getResult().getGuestTeamPoints();
		guestTeam[4] = (int) guestTeam[4] + game.getResult().getHomeTeamPoints();
		guestTeam[5] = (int) guestTeam[5] + 1;
	}
}
