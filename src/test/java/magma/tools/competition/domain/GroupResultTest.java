package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GroupResult;
import magma.tools.competition.domain.GroupResultCalculationStrategy;
import magma.tools.competition.domain.ITeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupResultTest
{

	@Mock
	private List<Game> games;

	@Mock
	private GroupResultCalculationStrategy strategy;

	private Object[][] calculationResultNonFinal = {
			{ mock(ITeam.class), 1, 3, 5, 3, 7 },
			{ mock(ITeam.class), 1, 3, 5, 3, 7 },
			{ mock(ITeam.class), 3, 3, 3, 5, 1 },
			{ mock(ITeam.class), 3, 3, 3, 5, 1 }, };

	private Object[][] calculationResultFinal = {
			{ mock(ITeam.class), 1, 3, 7, 2, 7 },
			{ mock(ITeam.class), 2, 3, 5, 4, 7 },
			{ mock(ITeam.class), 3, 3, 3, 5, 1 },
			{ mock(ITeam.class), 4, 3, 2, 7, 1 }, };

	private GroupResult result;

	@Before
	public void setUp() throws Exception
	{
		when(strategy.calculateResult(games)).thenReturn(
				Arrays.asList(calculationResultNonFinal));
		result = new GroupResult(strategy, games, null);
	}

	@Test
	public void testConstructor() throws Exception
	{
		verify(strategy).calculateResult(games);
	}

	@Test
	public void testGetTeamsOnRank() throws Exception
	{
		List<ITeam> teams = result.getTeamsOnRank(1);
		assertSame(calculationResultNonFinal[0][0], teams.get(0));
		assertSame(calculationResultNonFinal[1][0], teams.get(1));
		teams = result.getTeamsOnRank(3);
		assertSame(calculationResultNonFinal[2][0], teams.get(0));
		assertSame(calculationResultNonFinal[3][0], teams.get(1));
	}

	@Test
	public void testGetTeamAtIndex() throws Exception
	{
		assertSame(calculationResultNonFinal[0][0], result.getTeamAtIndex(0));
		assertSame(calculationResultNonFinal[1][0], result.getTeamAtIndex(1));
		assertSame(calculationResultNonFinal[2][0], result.getTeamAtIndex(2));
		assertSame(calculationResultNonFinal[3][0], result.getTeamAtIndex(3));
	}

	@Test
	public void testGetTeamAtIndexOutOfBound() throws Exception
	{
		assertEquals(null, result.getTeamAtIndex(25));
	}

	@Test
	public void testGetRankOfTeam() throws Exception
	{
		assertEquals(1,
				result.getRankOfTeam((ITeam) calculationResultNonFinal[0][0]));
		assertEquals(-1, result.getRankOfTeam(mock(ITeam.class)));
	}

	@Test(expected = NullPointerException.class)
	public void testGetRankOfTeamNull() throws Exception
	{
		result.getRankOfTeam(null);
	}

	@Test
	public void testIsFinal() throws Exception
	{
		assertEquals(false, result.isFinal());
		when(strategy.calculateResult(games)).thenReturn(
				Arrays.asList(calculationResultFinal));
		result = new GroupResult(strategy, games, null);
		assertEquals(true, result.isFinal());
	}

	@Test
	public void testGetPoints() throws Exception
	{
		assertEquals(7, result.getPoints((ITeam) calculationResultNonFinal[0][0]));
	}

	@Test
	public void testGetGoals() throws Exception
	{
		assertEquals(5, result.getGoals((ITeam) calculationResultNonFinal[0][0]));
	}

	@Test
	public void testGetGames() throws Exception
	{
		assertEquals(3, result.getGames((ITeam) calculationResultNonFinal[0][0]));
	}

	@Test
	public void testGetGoalsAgainst() throws Exception
	{
		assertEquals(3,
				result.getGoalsAgainst((ITeam) calculationResultNonFinal[0][0]));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidTeam() throws Exception
	{
		result.getGoals(mock(ITeam.class));
	}

}
