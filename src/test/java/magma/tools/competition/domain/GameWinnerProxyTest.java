package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameResult;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.GameWinnerProxy;
import magma.tools.competition.domain.ITeam;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GameWinnerProxyTest
{
	@Mock
	private Game game;

	@Mock
	private GameResult result;

	@Mock
	private ITeam homeTeam;

	@Mock
	private ITeam guestTeam;

	private GameWinnerProxy proxy;

	@Before
	public void setUp() throws Exception
	{
		when(game.getResult()).thenReturn(result);
		when(game.getHomeTeam()).thenReturn(homeTeam);
		when(game.getGuestTeam()).thenReturn(guestTeam);
		when(homeTeam.getName()).thenReturn("home");
		when(homeTeam.getUsername()).thenReturn("homeusername");
		when(homeTeam.getStartScriptFileName()).thenReturn("homescript");
		when(homeTeam.getPathToScriptFile()).thenReturn("homepath");
		when(guestTeam.getName()).thenReturn("guest");
		when(guestTeam.getUsername()).thenReturn("guestusername");
		when(guestTeam.getStartScriptFileName()).thenReturn("guestscript");
		when(guestTeam.getPathToScriptFile()).thenReturn("guestpath");
		proxy = new GameWinnerProxy(game);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertEquals("Winner 'home : guest'", proxy.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorGameNull() throws Exception
	{
		new GameWinnerProxy(null);
	}

	@Test
	public void testGetNameHomeWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("home", proxy.getName());
	}

	@Test
	public void testGetNameGuestWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("guest", proxy.getName());
	}

	@Test
	public void testGetNameDraw() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("Winner 'home : guest'", proxy.getName());
	}

	@Test
	public void testGetNameProxied() throws Exception
	{
		assertEquals("Winner 'home : guest'", proxy.getName());
	}

	@Test
	public void testGetUsernameHomeWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("homeusername", proxy.getUsername());
	}

	@Test
	public void testGetUsernameGuestWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("guestusername", proxy.getUsername());
	}

	@Test
	public void testGetUsernameDraw() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("-", proxy.getUsername());
	}

	@Test
	public void testGetUsernameProxied() throws Exception
	{
		assertEquals("-", proxy.getUsername());
	}

	@Test
	public void testGetStartScriptFileNameHomeWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("homescript", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetStartScriptFileNameGuestWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("guestscript", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetStartScriptFileNameDraw() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("-", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetStartScriptFileNameProxied() throws Exception
	{
		assertEquals("-", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetPathToScriptFileNameHomeWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("homepath", proxy.getPathToScriptFile());
	}

	@Test
	public void testGetPathToScriptFileNameGuestWinner() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("guestpath", proxy.getPathToScriptFile());
	}

	@Test
	public void testGetPathToScriptFileNameDraw() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("-", proxy.getPathToScriptFile());
	}

	@Test
	public void testGetPathToScriptFileNameProxied() throws Exception
	{
		assertEquals("-", proxy.getPathToScriptFile());
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(GameWinnerProxy.class).usingGetClass().verify();
	}
}
