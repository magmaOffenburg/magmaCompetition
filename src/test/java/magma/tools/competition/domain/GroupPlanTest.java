package magma.tools.competition.domain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import magma.tools.competition.domain.ChangeHandler;
import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GroupPlan;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupPlanTest
{

	@Mock
	private Game game1;

	@Mock
	private Game game2;

	@Mock
	private ChangeHandler<GroupPlan> handler;

	private List<Game> games;

	private GroupPlan plan;

	@Before
	public void setUp() throws Exception
	{
		games = new ArrayList<Game>();
		games.add(game1);
		games.add(game2);
		plan = new GroupPlan(games);
		plan.addChangeHandler(handler);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertSame(plan.getGames().get(0), game1);
		assertSame(plan.getGames().get(1), game2);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorGamesNull() throws Exception
	{
		new GroupPlan(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorGamesEmpty() throws Exception
	{
		games.clear();
		new GroupPlan(games);
	}

	@Test
	public void testAddGame() throws Exception
	{
		Game game = mock(Game.class);
		plan.addGame(game);
		verify(handler, times(1)).onChange(plan);
		assertSame(game, plan.getGames().get(2));
	}

	@Test(expected = NullPointerException.class)
	public void testAddGameNull() throws Exception
	{
		plan.addGame(null);
	}

	@Test
	public void testAddChangeHandler() throws Exception
	{
		plan.addChangeHandler(handler);
		plan.addGame(mock(Game.class));
		verify(handler, times(1)).onChange(plan);
	}

	@Test
	public void testRemoveChangeHandler() throws Exception
	{
		plan.removeChangeHandler(handler);
		plan.addGame(mock(Game.class));
		verify(handler, times(0)).onChange(plan);
	}

}
