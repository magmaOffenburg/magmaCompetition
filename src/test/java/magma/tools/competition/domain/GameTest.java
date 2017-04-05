package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import magma.tools.competition.domain.ChangeHandler;
import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameResult;
import magma.tools.competition.domain.GameState;
import magma.tools.competition.domain.ITeam;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Provider;

@RunWith(MockitoJUnitRunner.class)
public class GameTest
{
	@Mock
	private Provider<Date> dateProvider;

	@Mock
	private GameResult result;

	@Mock
	private ITeam homeTeam;

	@Mock
	private ITeam guestTeam;

	@Mock
	private ChangeHandler<Game> handler;

	private Game game;

	@Before
	public void setUp() throws Exception
	{
		game = new Game(dateProvider, result, homeTeam, guestTeam);
		game.addChangeHandler(handler);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertSame(homeTeam, game.getHomeTeam());
		assertSame(guestTeam, game.getGuestTeam());
		assertEquals(GameState.PLANNED, game.getState());
		assertSame(result, game.getResult());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorHomeTeamNull() throws Exception
	{
		new Game(dateProvider, result, null, guestTeam);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorGuestTeamNull() throws Exception
	{
		new Game(dateProvider, result, homeTeam, null);
	}

	@Test
	public void testSetState() throws Exception
	{
		Date realStartTime = mock(Date.class);
		when(dateProvider.get()).thenReturn(realStartTime);
		assertEquals(GameState.PLANNED, game.getState());
		assertNull(game.getRealStartTime());
		game.setState(GameState.STARTED);
		verify(handler, times(1)).onChange(game);
		assertEquals(GameState.STARTED, game.getState());
		assertSame(realStartTime, game.getRealStartTime());
		game.setState(GameState.FINISHED);
		verify(handler, times(2)).onChange(game);
		assertEquals(GameState.FINISHED, game.getState());
		assertSame(realStartTime, game.getRealStartTime());
	}

	@Test(expected = NullPointerException.class)
	public void testSetStateNull() throws Exception
	{
		game.setState(null);
	}

	@Test
	public void testSetPlannedStartTime() throws Exception
	{
		Date date = mock(Date.class);
		game.setPlannedStartTime(date);
		assertSame(date, game.getPlannedStartTime());
		verify(handler, times(1)).onChange(game);
	}

	@Test
	public void testAddChangeHandler() throws Exception
	{
		game.addChangeHandler(handler);
		game.setPlannedStartTime(new Date());
		verify(handler, times(1)).onChange(game);
	}

	@Test
	public void testRemoveChangeHandler() throws Exception
	{
		game.removeChangeHandler(handler);
		game.setPlannedStartTime(new Date());
		verify(handler, times(0)).onChange(game);
	}

	@Test
	public void testOnChange() throws Exception
	{
		game.onChange(mock(GameResult.class));
		verify(handler, times(1)).onChange(game);
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(Game.class).usingGetClass().verify();
	}
}
