package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import magma.tools.competition.domain.ChangeHandler;
import magma.tools.competition.domain.GameResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GameResultTest
{

	@Mock
	private ChangeHandler<GameResult> handler;

	private GameResult result;

	@Before
	public void setUp() throws Exception
	{
		result = new GameResult();
		result.addChangeHandler(handler);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertEquals(0, result.getHomeTeamPoints());
		assertEquals(0, result.getGuestTeamPoints());
	}

	@Test
	public void testJsonConstructor() throws Exception
	{
		result = new GameResult(5, 4);
		assertEquals(5, result.getHomeTeamPoints());
		assertEquals(4, result.getGuestTeamPoints());
	}

	@Test
	public void testSetHomeTeamPoints() throws Exception
	{
		result.setHomeTeamPoints(4);
		verify(handler).onChange(result);
		assertEquals(4, result.getHomeTeamPoints());
	}

	@Test
	public void testSetGuestTeamPoints() throws Exception
	{
		result.setGuestTeamPoints(4);
		verify(handler).onChange(result);
		assertEquals(4, result.getGuestTeamPoints());
	}

	@Test
	public void testAddChangeHandler() throws Exception
	{
		result.addChangeHandler(handler);
		result.setHomeTeamPoints(4);
		verify(handler, times(1)).onChange(result);
	}

	@Test
	public void testRemoveChangeHandler() throws Exception
	{
		result.removeChangeHandler(handler);
		result.setHomeTeamPoints(4);
		verify(handler, times(0)).onChange(result);
	}

}
