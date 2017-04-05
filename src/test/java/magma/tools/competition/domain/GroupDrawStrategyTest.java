package magma.tools.competition.domain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.DrawingBowl;
import magma.tools.competition.domain.DrawingBowlFactory;
import magma.tools.competition.domain.GroupDrawStrategy;
import magma.tools.competition.domain.ITeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class GroupDrawStrategyTest
{
	@Mock
	private DrawingBowlFactory factory;

	@Mock
	private DrawingBowl setTeamsBowl;

	@Mock
	private DrawingBowl otherTeamsBowl;

	private List<ITeam> teams;

	private GroupDrawStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
		strategy = new GroupDrawStrategy(factory);
		teams = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			ITeam team = mock(ITeam.class);
			teams.add(team);
			if (i < 2) {
				when(team.isSetTeam()).thenReturn(true);
			}
		}
		when(factory.create(Matchers.anyObject())).thenReturn(setTeamsBowl).thenReturn(otherTeamsBowl);
		when(setTeamsBowl.draw()).thenReturn(teams.get(0), teams.get(1));
		when(setTeamsBowl.isEmpty()).thenReturn(false, false, true);
		when(otherTeamsBowl.draw()).thenReturn(teams.get(2), teams.get(3));
		when(otherTeamsBowl.isEmpty()).thenReturn(false, false, true);
	}

	@Test
	public void testDraw() throws Exception
	{
		List<LinkedHashSet<ITeam>> drawResult = strategy.draw(2, Sets.newLinkedHashSet(teams));
		assertSame(teams.get(0), Lists.newArrayList(drawResult.get(0)).get(0));
		assertSame(teams.get(2), Lists.newArrayList(drawResult.get(0)).get(1));
		assertSame(teams.get(1), Lists.newArrayList(drawResult.get(1)).get(0));
		assertSame(teams.get(3), Lists.newArrayList(drawResult.get(1)).get(1));
	}

	@Test(expected = NullPointerException.class)
	public void testDrawTeamsNull() throws Exception
	{
		strategy.draw(2, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDrawTeamsInvalid() throws Exception
	{
		teams.remove(0);
		teams.remove(0);
		teams.remove(0); // 1 Element
		strategy.draw(2, Sets.newLinkedHashSet(teams));
	}
}
