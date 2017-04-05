package magma.tools.competition.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for the calculation of the result of a Group.
 *
 * @author Simon Gutjahr
 *
 */
public class GroupResultCalculationStrategy
{
	private ArrayList<Game> games;

	private ArrayList<Game> tieBreakGames;

	private ArrayList<Object[]> groupResult;

	private TeamResultCalculationStrategy teamResultCalculator;

	public GroupResultCalculationStrategy()
	{
		teamResultCalculator = new TeamResultCalculationStrategy();
		groupResult = null;
	}

	/**
	 * Calculate the standing of the teams included in the games of the given
	 * list.
	 *
	 * @param games List of the games for the result calculation.
	 *
	 * @return An Array list with the teams in the order of there standing.
	 *         Structure of the Object Array for each team: [0] = Team (Team
	 *         Object), [1] = Position (int), [2] = Games (int), [3] = Goals
	 *         (int), [4] = Goals against (int), [5] = Points (int)
	 */
	public List<Object[]> calculateResult(List<Game> games)
	{
		this.games = new ArrayList<Game>();
		this.tieBreakGames = new ArrayList<Game>();

		for (int i = 0; i < games.size(); i++) {
			Game game = games.get(i);

			if (game.getState() == GameState.FINISHED && (game.isDecisionGame() == false)) {
				this.games.add(game);
			}

			if (game.isDecisionGame() == true && game.getState() == GameState.FINISHED) {
				tieBreakGames.add(game);
			}
		}

		if (this.games.size() > 0) {
			ArrayList<Object[]> unsortetResult = teamResultCalculator.getTeamData(this.games);

			groupResult = unsortetResult;

			sort(unsortetResult);
		}

		if (groupResult == null) {
			return Collections.emptyList();
		} else {
			return groupResult;
		}
	}

	private void sort(ArrayList<Object[]> unsortedList)
	{
		/* Sort the teams by points */
		sortByPoints(unsortedList);

		/* Sort the teams by goal difference */
		sortByGoalDifference(unsortedList);

		/* Set the temporary position of the teams */
		for (int i = 0; i < unsortedList.size(); i++) {
			Object[] a = unsortedList.get(i);
			a[1] = i + 1;
		}

		/*
		 * Check if there are teams with the same number of points and goals. If
		 * there are such teams, sort them
		 */
		sortByDirectComparison(unsortedList);
	}

	private void sortByPoints(ArrayList<Object[]> unsortedList)
	{
		for (int i = unsortedList.size(); i > 1; i = i - 1) {
			for (int j = 0; j < (i - 1); j = j + 1) {
				Object[] a = unsortedList.get(j);
				Object[] b = unsortedList.get((j + 1));
				if ((int) a[5] < (int) b[5]) {
					unsortedList.set(j, b);
					unsortedList.set(j + 1, a);
				}
			}
		}
	}

	private void sortByGoalDifference(ArrayList<Object[]> unsortedList)
	{
		for (int i = unsortedList.size(); i > 1; i = i - 1) {
			for (int j = 0; j < (i - 1); j = j + 1) {
				Object[] a = unsortedList.get(j);
				Object[] b = unsortedList.get((j + 1));

				if ((int) a[5] == (int) b[5]) {
					int goalDifferenceTeamA = (int) a[3] - (int) a[4];
					int goalDifferenceTeamB = (int) b[3] - (int) b[4];

					if (goalDifferenceTeamA < goalDifferenceTeamB) {
						unsortedList.set(j, b);
						unsortedList.set(j + 1, a);
					}
				}
			}
		}
	}

	private void sortByDirectComparison(ArrayList<Object[]> unsortedList)
	{
		ArrayList<ITeam> teamList = new ArrayList<ITeam>();
		int pos = 0;
		int unsortedListPos = 0;

		for (int i = 0; i < unsortedList.size() - 1; i++) {
			/*
			 * Search for identical teams. Identical means teams with the same
			 * number of points and goals
			 */
			Object[] a = unsortedList.get(i);
			Object[] b = unsortedList.get((i + 1));
			int goalDifferenceTeamA = (int) a[3] - (int) a[4];
			int goalDifferenceTeamB = (int) b[3] - (int) b[4];

			/*
			 * Check if there are two teams with the same number of points and
			 * goals
			 */
			if ((goalDifferenceTeamA == goalDifferenceTeamB) && ((int) a[5] == (int) b[5])) {
				/*
				 * There are at least two teams with the same number of points and
				 * goals
				 */

				if (!teamList.contains(a[0])) {
					teamList.add((ITeam) a[0]);
					pos = (int) a[1];
					unsortedListPos = i;
				}

				if (!teamList.contains(b[0])) {
					teamList.add((ITeam) b[0]);
				}

				int points = (int) a[5];
				int goals = (int) a[3] - (int) a[4];

				/*
				 * Check if there are more teams with the same number of points and
				 * goals
				 */
				for (int k = (i + 2); k < unsortedList.size(); k++) {
					Object[] c = unsortedList.get(k);
					int goalDifferenceTeamC = (int) c[3] - (int) c[4];

					if ((goalDifferenceTeamC == goals) && ((int) c[5] == points)) {
						if (!teamList.contains(c[0])) {
							teamList.add((ITeam) c[0]);
						}
					}
				}
			}

			/* Check if there are identical teams */
			if (teamList.size() > 0) {
				/* There are identical teams */

				/* Get only the games played between the identical teams */
				ArrayList<Game> newGroupGames = getGames(teamList);

				/* Calculate the result based of the reduced game list */
				ArrayList<Object[]> newGroupTeams = teamResultCalculator.getTeamData(newGroupGames);

				/* Check if there are more then two identical teams */
				if (newGroupTeams.size() > 2) {
					/* There are more then two identical teams */

					/* Check if all teams are identical */
					if (newGroupTeams.size() < unsortedList.size()) {
						/* Not all teams are identical, sort them. */

						sort(newGroupTeams);
					} else {
						/* All teams are identical. Stop the sort. */

						/* Get the position for the teams */
						int teamPos = getPosBestTeam(newGroupTeams, groupResult);

						/* Set position for all teams */
						for (int z = 0; z < newGroupTeams.size(); z++) {
							int idx = getIndexFromObjectTeam((ITeam) newGroupTeams.get(z)[0], groupResult);
							groupResult.get(idx)[1] = teamPos;
						}
					}
				} else if (newGroupTeams.size() == 2) {
					/* There are only two identical teams */
					Object[] teamA = newGroupTeams.get(0);
					Object[] teamB = newGroupTeams.get(1);

					/* Check the direct Comparison */
					if ((int) teamA[5] > (int) teamB[5]) {
						/* Team a wins */
						swapVictoryTeamA(teamA, teamB, unsortedList);
					} else if ((int) teamA[5] < (int) teamB[5]) {
						/* Team b wins */
						swapVictoryTeamB(teamA, teamB, unsortedList);
					} else {
						/* Draw */
						Game game = null;

						/*
						 * Check if there was played a tie break games between this
						 * teams
						 */
						for (int z = 0; z < tieBreakGames.size(); z++) {
							if (areTeamsInGame(tieBreakGames.get(z), (ITeam) teamA[0], (ITeam) teamB[0])) {
								game = tieBreakGames.get(z);
								break;
							}
						}

						/* There is a tie break team */
						if (game != null) {
							ArrayList<Game> tempGames = new ArrayList<Game>();
							tempGames.add(game);

							/* Get the winner */
							ArrayList<Object[]> tempResult = teamResultCalculator.getTeamData(tempGames);

							Object[] tieBreakTeamA = tempResult.get(0);
							Object[] tieBreakTeamB = tempResult.get(1);

							if ((int) tieBreakTeamA[5] == 3) {
								int idxA = getIndexFromObjectTeam((ITeam) tieBreakTeamA[0], unsortedList);
								int idxB = getIndexFromObjectTeam((ITeam) tieBreakTeamB[0], unsortedList);

								if (idxB < idxA) {
									Object[] temp = unsortedList.get(idxA);
									unsortedList.set(idxA, unsortedList.get(idxB));
									unsortedList.set(idxB, temp);
								}

								unsortedList.get(idxA)[1] = idxA + 1;
								unsortedList.get(idxB)[1] = idxB + 1;

							} else if ((int) tieBreakTeamB[5] == 3) {
								int idxA = getIndexFromObjectTeam((ITeam) tieBreakTeamA[0], unsortedList);
								int idxB = getIndexFromObjectTeam((ITeam) tieBreakTeamB[0], unsortedList);

								if (idxA < idxB) {
									Object[] temp = unsortedList.get(idxA);
									unsortedList.set(idxA, unsortedList.get(idxB));
									unsortedList.set(idxB, temp);
								}

								unsortedList.get(idxA)[1] = idxA + 1;
								unsortedList.get(idxB)[1] = idxB + 1;
							}
						} else {
							unsortedList.get(unsortedListPos)[1] = pos;
							unsortedList.get((unsortedListPos + 1))[1] = pos;
						}
					}
				}

				i += (teamList.size() - 1);

				teamList.clear();
			}
		}
	}

	private int getPosBestTeam(ArrayList<Object[]> teamList, ArrayList<Object[]> objectTeamList)
	{
		int pos = 0xFFFF;
		for (int i = 0; i < teamList.size(); i++) {
			ITeam team = (ITeam) teamList.get(i)[0];
			int index = getIndexFromObjectTeam(team, objectTeamList);

			int teamPos = (int) objectTeamList.get(index)[1];
			if (teamPos < pos) {
				pos = teamPos;
			}
		}

		return pos;
	}

	private void swapVictoryTeamA(Object[] teamA, Object[] teamB, ArrayList<Object[]> unsortedList)
	{
		int indexA = getIndexFromObjectTeam((ITeam) teamA[0], unsortedList);
		int indexB = getIndexFromObjectTeam((ITeam) teamB[0], unsortedList);

		if (indexA > indexB) {
			Object tA[] = unsortedList.get(indexA);
			Object tB[] = unsortedList.get(indexB);

			tA[1] = indexB + 1;
			tB[1] = indexA + 1;

			unsortedList.set(indexA, tB);
			unsortedList.set(indexB, tA);
		}
	}

	private void swapVictoryTeamB(Object[] teamA, Object[] teamB, ArrayList<Object[]> unsortedList)
	{
		int indexA = getIndexFromObjectTeam((ITeam) teamA[0], unsortedList);
		int indexB = getIndexFromObjectTeam((ITeam) teamB[0], unsortedList);

		if (indexA < indexB) {
			Object tA[] = unsortedList.get(indexA);
			Object tB[] = unsortedList.get(indexB);

			tA[1] = indexB + 1;
			tB[1] = indexA + 1;

			unsortedList.set(indexA, tB);
			unsortedList.set(indexB, tA);
		}
	}

	private int getIndexFromObjectTeam(ITeam team, ArrayList<Object[]> unsortedList)
	{
		int ret = -1;

		for (int i = 0; i < unsortedList.size(); i++) {
			if (unsortedList.get(i)[0].equals(team)) {
				ret = i;
				break;
			}
		}

		return ret;
	}

	private ArrayList<Game> getGames(ArrayList<ITeam> teamList)
	{
		ArrayList<Game> partGames = new ArrayList<Game>();

		for (int i = 0; i < teamList.size() - 1; i++) {
			ITeam t1 = teamList.get(i);

			for (int k = 0; k < teamList.size(); k++) {
				ITeam t2 = teamList.get(k);

				for (int j = 0; j < games.size(); j++) {
					Game game = games.get(j);

					if (areTeamsInGame(game, t1, t2) == true) {
						if (!partGames.contains(game)) {
							partGames.add(game);
						}
						break;
					}
				}
			}
		}

		return partGames;
	}

	private boolean areTeamsInGame(Game game, ITeam a, ITeam b)
	{
		boolean ret = false;

		if ((game.getHomeTeam().equals(a) && game.getGuestTeam().equals(b)) ||
				(game.getHomeTeam().equals(b) && game.getGuestTeam().equals(a))) {
			ret = true;
		}

		return ret;
	}
}
