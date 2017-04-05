package magma.tools.competition.domain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameFactory;
import magma.tools.competition.domain.GameUtil;
import magma.tools.competition.domain.ITeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GameUtilTest
{
	@Mock
	private GameFactory factory;

	private List<ITeam> teams;

	private List<Game> games;

	@InjectMocks
	private GameUtil gameUtil;

	@Before
	public void setUp() throws Exception
	{
		teams = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			teams.add(mock(ITeam.class));
		}
		games = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			games.add(mock(Game.class));
		}
	}

	@Test
	public void testCreateKoPhaseGames() throws Exception
	{
		when(factory.create(teams.get(0), teams.get(1))).thenReturn(games.get(0));
		when(factory.create(teams.get(2), teams.get(3))).thenReturn(games.get(1));
		when(factory.create(teams.get(4), teams.get(5))).thenReturn(games.get(2));
		when(factory.create(teams.get(6), teams.get(7))).thenReturn(games.get(3));
		List<Game> createdGames = gameUtil.createKoPhaseGames(new LinkedHashSet<>(teams));
		verify(factory).create(teams.get(0), teams.get(1));
		verify(factory).create(teams.get(2), teams.get(3));
		verify(factory).create(teams.get(4), teams.get(5));
		verify(factory).create(teams.get(6), teams.get(7));
		assertSame(games.get(0), createdGames.get(0));
		assertSame(games.get(1), createdGames.get(1));
		assertSame(games.get(2), createdGames.get(2));
		assertSame(games.get(3), createdGames.get(3));
	}

	@Test(expected = NullPointerException.class)
	public void testCreateKoPhaseTeamsNull() throws Exception
	{
		gameUtil.createKoPhaseGames(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKoPhaseEmptyTeams() throws Exception
	{
		teams.clear();
		gameUtil.createKoPhaseGames(new LinkedHashSet<>(teams));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKoPhaseLessThanFour() throws Exception
	{
		teams.remove(0);
		teams.remove(0);
		teams.remove(0);
		teams.remove(0);
		teams.remove(0); // 3 Elements
		gameUtil.createKoPhaseGames(new LinkedHashSet<>(teams));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKoPhaseUnevenNumberOfTeams() throws Exception
	{
		teams.remove(0); // 7 Elements
		gameUtil.createKoPhaseGames(new LinkedHashSet<>(teams));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKoPhaseNotPowerOfTwo() throws Exception
	{
		teams.remove(0); // 7 Elements
		teams.remove(0); // 6 Elements
		gameUtil.createKoPhaseGames(new LinkedHashSet<>(teams));
	}
}
