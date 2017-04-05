package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameLooserProxy;
import magma.tools.competition.domain.GameResult;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.ITeam;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GameLooserProxyTest
{
	@Mock
	private Game game;

	@Mock
	private GameResult result;

	@Mock
	private ITeam homeTeam;

	@Mock
	private ITeam guestTeam;

	private GameLooserProxy proxy;

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
		proxy = new GameLooserProxy(game);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertEquals("Looser 'home : guest'", proxy.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorGameNull() throws Exception
	{
		new GameLooserProxy(null);
	}

	@Test
	public void testGetNameHomeLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("home", proxy.getName());
	}

	@Test
	public void testGetNameGuestLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("guest", proxy.getName());
	}

	@Test
	public void testGetNameDraw() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(2);
		assertEquals("Looser 'home : guest'", proxy.getName());
	}

	@Test
	public void testGetNameProxied() throws Exception
	{
		assertEquals("Looser 'home : guest'", proxy.getName());
	}

	@Test
	public void testGetUsernameHomeLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("homeusername", proxy.getUsername());
	}

	@Test
	public void testGetUsernameGuestLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
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
	public void testGetStartScriptFileNameHomeLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("homescript", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetStartScriptFileNameGuestLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
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
	public void testGetPathToScriptFileNameHomeLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(2);
		when(result.getGuestTeamPoints()).thenReturn(4);
		assertEquals("homepath", proxy.getPathToScriptFile());
	}

	@Test
	public void testGetPathToScriptFileNameGuestLooser() throws Exception
	{
		when(game.getState()).thenReturn(GameState.FINISHED);
		when(result.getHomeTeamPoints()).thenReturn(4);
		when(result.getGuestTeamPoints()).thenReturn(2);
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
		EqualsVerifier.forClass(GameLooserProxy.class).usingGetClass().verify();
	}
}
