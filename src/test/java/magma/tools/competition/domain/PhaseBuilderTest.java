package magma.tools.competition.domain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupDrawStrategy;
import magma.tools.competition.domain.GroupFactory;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.PhaseBuilder;
import magma.tools.competition.domain.PhaseFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class PhaseBuilderTest
{
	@Mock
	private PhaseFactory phaseFactory;

	@Mock
	private GroupFactory groupFactory;

	@Mock
	private GroupDrawStrategy drawStrategy;

	private List<ITeam> teamList;

	private LinkedHashSet<ITeam> teamSet;

	private PhaseBuilder builder;

	@Before
	public void setUp() throws Exception
	{
		teamList = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			teamList.add(mock(ITeam.class));
		}
		teamSet = Sets.newLinkedHashSet();
		builder = new PhaseBuilder(phaseFactory, groupFactory, drawStrategy);
	}

	@Test
	public void testBuildGroupPhaseInitial() throws Exception
	{
		List<LinkedHashSet<ITeam>> buckets = Lists.newArrayList();
		buckets.add(Sets.newLinkedHashSet());
		buckets.add(Sets.newLinkedHashSet());
		buckets.get(0).add(teamList.get(0));
		buckets.get(0).add(teamList.get(1));
		buckets.get(1).add(teamList.get(2));
		buckets.get(1).add(teamList.get(3));
		List<Group> groups = Lists.newArrayList();
		groups.add(mock(Group.class));
		groups.add(mock(Group.class));
		when(drawStrategy.draw(2, teamSet)).thenReturn(buckets);
		when(groupFactory.create("A", buckets.get(0))).thenReturn(groups.get(0));
		when(groupFactory.create("B", buckets.get(1))).thenReturn(groups.get(1));
		Phase mockPhase = mock(Phase.class);
		when(phaseFactory.createGroupPhase(Matchers.matches("groupphase"),
					 (LinkedHashSet<Group>) Matchers.argThat(new CollectionArgumentMatcher<Group>(groups)),
					 Matchers.eq(2)))
				.thenReturn(mockPhase);
		Phase phase = builder.teams(teamSet).numberOfGroups(2).numberOfQualifyingTeams(2).buildGroupPhase("groupphase");
		assertSame(mockPhase, phase);
	}

	@Test
	public void testBuildGroupPhaseFromGroupPhase() throws Exception
	{
		GroupPhase previousPhase = mock(GroupPhase.class);
		when(previousPhase.getNumberOfQualifyingTeams()).thenReturn(1);
		List<Group> groups = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			groups.add(mock(Group.class));
		}
		when(previousPhase.getGroups()).thenReturn(groups);
		when(previousPhase.getQualifyingTeams()).thenReturn(teamList);

		List<Group> newGroups = Lists.newArrayList();
		Group newGroup1 = mock(Group.class);
		newGroups.add(newGroup1);
		Group newGroup2 = mock(Group.class);
		newGroups.add(newGroup2);
		LinkedHashSet<ITeam> teamsGroup1 = Sets.newLinkedHashSet();
		teamsGroup1.add(teamList.get(0));
		teamsGroup1.add(teamList.get(2));
		LinkedHashSet<ITeam> teamsGroup2 = Sets.newLinkedHashSet();
		teamsGroup2.add(teamList.get(0));
		teamsGroup2.add(teamList.get(2));

		when(groupFactory.create(Matchers.matches("A"), Matchers.anyObject())).thenReturn(newGroup1);
		when(groupFactory.create(Matchers.matches("B"), Matchers.anyObject())).thenReturn(newGroup2);

		builder.previousPhase(previousPhase);
		builder.numberOfGroups(2);
		builder.numberOfQualifyingTeams(1);

		Phase mockPhase = mock(Phase.class);
		when(phaseFactory.createGroupPhase(Matchers.matches("groupphase"),
					 (LinkedHashSet<Group>) Matchers.argThat(new CollectionArgumentMatcher<Group>(newGroups)),
					 Matchers.eq(1)))
				.thenReturn(mockPhase);
		Phase phase = builder.buildGroupPhase("groupphase");
		assertSame(mockPhase, phase);
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildGroupPhaseFromKoPhase() throws Exception
	{
		builder.previousPhase(mock(KoPhase.class));
		builder.buildGroupPhase("groupphase");
	}

	@Test
	public void testBuildKoPhaseFromGroupPhase() throws Exception
	{
		GroupPhase previousPhase = mock(GroupPhase.class);
		when(previousPhase.getNumberOfQualifyingTeams()).thenReturn(1);
		List<Group> groups = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			groups.add(mock(Group.class));
		}
		when(previousPhase.getGroups()).thenReturn(groups);
		when(previousPhase.getQualifyingTeams()).thenReturn(teamList);

		Phase mockPhase = mock(Phase.class);
		when(phaseFactory.createKoPhase(Matchers.matches("kophase"),
					 (LinkedHashSet<ITeam>) Matchers.argThat(new CollectionArgumentMatcher<ITeam>(teamList))))
				.thenReturn(mockPhase);

		builder.previousPhase(previousPhase);
		Phase phase = builder.buildKoPhase("kophase");
		assertSame(mockPhase, phase);
	}

	@Test
	public void testBuildKoPhaseFromKoPhase() throws Exception
	{
		KoPhase previousPhase = mock(KoPhase.class);
		when(previousPhase.getQualifyingTeams()).thenReturn(teamList);
		Phase mockPhase = mock(Phase.class);
		when(phaseFactory.createKoPhase(Matchers.matches("kophase"),
					 (LinkedHashSet<ITeam>) Matchers.argThat(new CollectionArgumentMatcher<ITeam>(teamList))))
				.thenReturn(mockPhase);

		builder.previousPhase(previousPhase);
		Phase phase = builder.buildKoPhase("kophase");
		assertSame(mockPhase, phase);
	}

	@Test
	public void testBuildThirdPlacePlayOffFromKoPhase() throws Exception
	{
		teamList.remove(0);
		teamList.remove(0);
		KoPhase previousPhase = mock(KoPhase.class);
		when(previousPhase.getRetiringTeams()).thenReturn(teamList);
		Phase mockPhase = mock(Phase.class);
		when(phaseFactory.createKoPhase(Matchers.matches("thirdplaceplayoff"),
					 (LinkedHashSet<ITeam>) Matchers.argThat(new CollectionArgumentMatcher<ITeam>(teamList))))
				.thenReturn(mockPhase);

		builder.previousPhase(previousPhase);
		builder.useRetiringTeams(true);
		Phase phase = builder.buildKoPhase("thirdplaceplayoff");
		assertSame(mockPhase, phase);
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildKoPhaseFromNull() throws Exception
	{
		builder.buildKoPhase("kophase");
	}

	private static class CollectionArgumentMatcher<T> extends ArgumentMatcher<Collection<T>>
	{
		private List<T> toMatch;

		CollectionArgumentMatcher(Collection<T> toMatch)
		{
			this.toMatch = Lists.newArrayList(toMatch);
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean matches(Object argument)
		{
			List<T> givenCollection = Lists.newArrayList((Collection<T>) argument);
			if (toMatch.size() != givenCollection.size()) {
				return false;
			}
			for (int i = 0; i < toMatch.size(); i++) {
				if (givenCollection.get(i) != toMatch.get(i)) {
					return false;
				}
			}
			return true;
		}
	}
}
