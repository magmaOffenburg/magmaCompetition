package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameUtil;
import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPlan;
import magma.tools.competition.domain.GroupPlanFactory;
import magma.tools.competition.domain.GroupResult;
import magma.tools.competition.domain.GroupResultFactory;
import magma.tools.competition.domain.ITeam;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupTest
{
	@Mock
	private GameUtil gameUtil;

	@Mock
	private GroupPlanFactory planFactory;

	@Mock
	private GroupResultFactory resultFactory;

	@Mock
	private ITeam team1;

	@Mock
	private ITeam team2;

	@Mock
	private GroupPlan plan;

	@Mock
	List<Game> games;

	LinkedHashSet<ITeam> teams;

	@Before
	public void setUp() throws Exception
	{
		teams = new LinkedHashSet<>();
		teams.add(team1);
		teams.add(team2);
		when(gameUtil.createGroupPhaseGames(teams)).thenReturn(games);
		when(planFactory.create(games)).thenReturn(plan);
	}

	@Test
	public void testConstructor() throws Exception
	{
		Group group = new Group(gameUtil, planFactory, resultFactory, "group", teams);
		assertEquals("group", group.getName());
		assertNotNull(group.getTeams());
		assertSame(group.getTeams().get(0), team1);
		assertSame(group.getTeams().get(1), team2);
		assertSame(group.getPlan(), plan);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNameNull() throws Exception
	{
		new Group(gameUtil, planFactory, resultFactory, null, teams);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNameInvalid() throws Exception
	{
		new Group(gameUtil, planFactory, resultFactory, " ", teams);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorTeamsNull() throws Exception
	{
		new Group(gameUtil, planFactory, resultFactory, "name", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTeamsEmpty() throws Exception
	{
		teams = new LinkedHashSet<>();
		new Group(gameUtil, planFactory, resultFactory, "name", teams);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTeamsOneTeam() throws Exception
	{
		teams = new LinkedHashSet<>();
		teams.add(team1);
		new Group(gameUtil, planFactory, resultFactory, "name", teams);
	}

	@Test
	public void testJsonConstructor() throws Exception
	{
		Group group = new Group(resultFactory, "group", teams, plan);
		assertEquals("group", group.getName());
		assertNotNull(group.getTeams());
		assertSame(group.getTeams().get(0), team1);
		assertSame(group.getTeams().get(1), team2);
		assertSame(group.getPlan(), plan);
	}

	@Test(expected = NullPointerException.class)
	public void testJsonConstructorPlanNull() throws Exception
	{
		new Group(resultFactory, "group", teams, null);
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(Group.class).usingGetClass().verify();
	}

	@Test
	public void testGetResult() throws Exception
	{
		Group group = new Group(gameUtil, planFactory, resultFactory, "group", teams);
		GroupResult result = mock(GroupResult.class);
		when(resultFactory.create(Matchers.anyListOf(Game.class))).thenReturn(result);
		assertSame(result, group.getResult());
	}
}
