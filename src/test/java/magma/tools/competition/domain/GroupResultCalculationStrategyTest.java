package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameResult;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.GroupResultCalculationStrategy;
import magma.tools.competition.domain.ITeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupResultCalculationStrategyTest
{

	private List<ITeam> teams;

	private GroupResultCalculationStrategy calculator;

	@Before
	public void setUp() throws Exception
	{
		calculator = new GroupResultCalculationStrategy();
		teams = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ITeam team = mock(ITeam.class);
			teams.add(team);
		}
	}

	@Test
	public void testCalculateResultAllTeamsTied() throws Exception
	{
		List<Game> games = new ArrayList<>();
		games.add(createGame(teams.get(0), teams.get(1), 1, 1));
		games.add(createGame(teams.get(0), teams.get(2), 1, 1));
		games.add(createGame(teams.get(0), teams.get(3), 1, 1));
		games.add(createGame(teams.get(1), teams.get(2), 1, 1));
		games.add(createGame(teams.get(1), teams.get(3), 1, 1));
		games.add(createGame(teams.get(2), teams.get(3), 1, 1));
		List<Object[]> result = calculator.calculateResult(games);
		for (int i = 0; i < 4; i++) {
			Object[] arr = result.get(0);
			assertEquals(1, arr[1]);
			assertEquals(3, arr[2]);
			assertEquals(3, arr[3]);
			assertEquals(3, arr[4]);
			assertEquals(3, arr[5]);
		}
	}

	@Test
	public void testCalculateResultTwoTeamsTied() throws Exception
	{
		List<Game> games = new ArrayList<>();
		games.add(createGame(teams.get(0), teams.get(1), 2, 1));
		games.add(createGame(teams.get(0), teams.get(2), 2, 1));
		games.add(createGame(teams.get(0), teams.get(3), 1, 1));
		games.add(createGame(teams.get(1), teams.get(2), 1, 1));
		games.add(createGame(teams.get(1), teams.get(3), 1, 2));
		games.add(createGame(teams.get(2), teams.get(3), 1, 2));
		List<Object[]> result = calculator.calculateResult(games);
		Object[][] expectedResults = { { teams.get(0), 1, 3, 5, 3, 7 },
				{ teams.get(3), 1, 3, 5, 3, 7 }, { teams.get(1), 3, 3, 3, 5, 1 },
				{ teams.get(2), 3, 3, 3, 5, 1 }, };
		for (int i = 0; i < 4; i++) {
			Object[] arr = result.get(i);
			assertSame(expectedResults[i][0], arr[0]);
			assertEquals(expectedResults[i][1], arr[1]);
			assertEquals(expectedResults[i][2], arr[2]);
			assertEquals(expectedResults[i][3], arr[3]);
			assertEquals(expectedResults[i][4], arr[4]);
			assertEquals(expectedResults[i][5], arr[5]);
		}
	}

	@Test
	public void testCalculateResultWinnerByDirectComparison() throws Exception
	{
		List<Game> games = new ArrayList<>();
		games.add(createGame(teams.get(0), teams.get(1), 3, 1));
		games.add(createGame(teams.get(0), teams.get(2), 2, 1));
		games.add(createGame(teams.get(0), teams.get(3), 1, 1));
		games.add(createGame(teams.get(1), teams.get(2), 1, 1));
		games.add(createGame(teams.get(1), teams.get(3), 1, 2));
		games.add(createGame(teams.get(2), teams.get(3), 1, 2));
		List<Object[]> result = calculator.calculateResult(games);
		Object[][] expectedResults = { { teams.get(0), 1, 3, 6, 3, 7 },
				{ teams.get(3), 2, 3, 5, 3, 7 }, { teams.get(2), 3, 3, 3, 5, 1 },
				{ teams.get(1), 4, 3, 3, 6, 1 } };

		for (int i = 0; i < 4; i++) {
			Object[] arr = result.get(i);
			assertSame(expectedResults[i][0], arr[0]);
			assertEquals(expectedResults[i][1], arr[1]);
			assertEquals(expectedResults[i][2], arr[2]);
			assertEquals(expectedResults[i][3], arr[3]);
			assertEquals(expectedResults[i][4], arr[4]);
			assertEquals(expectedResults[i][5], arr[5]);
		}
	}

	@Test
	public void testCalculateResultWinnerDirectComparison() throws Exception
	{
		List<Game> games = new ArrayList<>();
		games.add(createGame(teams.get(0), teams.get(1), 3, 0));
		games.add(createGame(teams.get(0), teams.get(2), 0, 0));
		games.add(createGame(teams.get(0), teams.get(3), 0, 2));
		games.add(createGame(teams.get(1), teams.get(2), 0, 0));
		games.add(createGame(teams.get(1), teams.get(3), 4, 0));
		games.add(createGame(teams.get(2), teams.get(3), 1, 2));
		List<Object[]> result = calculator.calculateResult(games);
		Object[][] expectedResults = { { teams.get(3), 1, 3, 4, 5, 6 },
				{ teams.get(0), 2, 3, 3, 2, 4 }, { teams.get(1), 3, 3, 4, 3, 4 },
				{ teams.get(2), 4, 3, 1, 2, 2 } };

		for (int i = 0; i < 4; i++) {
			Object[] arr = result.get(i);
			assertSame(expectedResults[i][0], arr[0]);
			assertEquals(expectedResults[i][1], arr[1]);
			assertEquals(expectedResults[i][2], arr[2]);
			assertEquals(expectedResults[i][3], arr[3]);
			assertEquals(expectedResults[i][4], arr[4]);
			assertEquals(expectedResults[i][5], arr[5]);
		}
	}

	private Game createGame(ITeam homeTeam, ITeam guestTeam, int homePoints,
			int guestPoints)
	{
		GameResult result = mock(GameResult.class);
		when(result.getHomeTeamPoints()).thenReturn(homePoints);
		when(result.getGuestTeamPoints()).thenReturn(guestPoints);
		Game game = mock(Game.class);
		when(game.getResult()).thenReturn(result);
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(game.getHomeTeam()).thenReturn(homeTeam);
		when(game.getGuestTeam()).thenReturn(guestTeam);
		return game;
	}

}
