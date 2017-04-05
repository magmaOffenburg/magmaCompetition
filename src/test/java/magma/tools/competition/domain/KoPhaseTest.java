package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameUtil;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.TeamFactory;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class KoPhaseTest
{
	@Mock
	private TeamFactory factory;

	@Mock
	private GameUtil util;

	@Mock
	private LinkedHashSet<ITeam> teams;

	private List<Game> games;

	@Before
	public void setUp() throws Exception
	{
		games = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			games.add(mock(Game.class));
		}
		when(util.createKoPhaseGames(teams)).thenReturn(games);
	}

	@Test
	public void testConstructor() throws Exception
	{
		KoPhase phase = new KoPhase(factory, util, "name", teams);
		verify(util).createKoPhaseGames(teams);
		assertEquals("name", phase.getName());
		assertSame(games, phase.getGames());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNameNull() throws Exception
	{
		new KoPhase(factory, util, null, teams);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNameInvalid() throws Exception
	{
		new KoPhase(factory, util, " ", teams);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorTeamsNull() throws Exception
	{
		new KoPhase(factory, util, "name", null);
	}

	@Test
	public void testJsonConstructor() throws Exception
	{
		KoPhase phase = new KoPhase(factory, "name", games);
		assertEquals("name", phase.getName());
		assertSame(games, phase.getGames());
	}

	@Test(expected = NullPointerException.class)
	public void testJsonConstructorNameNull() throws Exception
	{
		new KoPhase(factory, null, games);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testJsonConstructorNameInvalid() throws Exception
	{
		new KoPhase(factory, " ", games);
	}

	@Test(expected = NullPointerException.class)
	public void testJsonConstructorGamesNull() throws Exception
	{
		new KoPhase(factory, "name", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testJsonConstructorGamesInvalid() throws Exception
	{
		games.clear();
		new KoPhase(factory, "name", games);
	}

	@Test
	public void testGetQualifyingTeams() throws Exception
	{
		KoPhase phase = new KoPhase(factory, util, "name", teams);
		List<ITeam> teams = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			teams.add(mock(ITeam.class));
		}
		when(factory.createWinnerProxy(games.get(0))).thenReturn(teams.get(0));
		when(factory.createWinnerProxy(games.get(1))).thenReturn(teams.get(1));
		when(factory.createWinnerProxy(games.get(2))).thenReturn(teams.get(2));
		when(factory.createWinnerProxy(games.get(3))).thenReturn(teams.get(3));
		List<ITeam> qualifiedTeams = phase.getQualifyingTeams();
		for (int i = 0; i < 4; i++) {
			assertSame(teams.get(i), qualifiedTeams.get(i));
		}
	}

	@Test
	public void testGetRetiringTeams() throws Exception
	{
		KoPhase phase = new KoPhase(factory, util, "name", teams);
		List<ITeam> teams = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			teams.add(mock(ITeam.class));
		}
		when(factory.createLooserProxy(games.get(0))).thenReturn(teams.get(0));
		when(factory.createLooserProxy(games.get(1))).thenReturn(teams.get(1));
		when(factory.createLooserProxy(games.get(2))).thenReturn(teams.get(2));
		when(factory.createLooserProxy(games.get(3))).thenReturn(teams.get(3));
		List<ITeam> retiring = phase.getRetiringTeams();
		for (int i = 0; i < 4; i++) {
			assertSame(teams.get(i), retiring.get(i));
		}
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(KoPhase.class).usingGetClass().verify();
	}
}
