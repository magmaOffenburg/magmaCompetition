package magma.tools.competition.domain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.PhaseBuilder;
import magma.tools.competition.domain.Tournament;
import magma.tools.competition.domain.TournamentBuilder;
import magma.tools.competition.domain.TournamentFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.inject.Provider;

@RunWith(MockitoJUnitRunner.class)
public class TournamentBuilderTest
{

	@Mock
	private TournamentFactory factory;

	@Mock
	private Provider<PhaseBuilder> builderProvider;

	@Mock
	private List<ITeam> gp2QualifyingTeams;

	@Mock
	private List<ITeam> semiFinalRetiringTeams;

	private List<ITeam> teams;

	private List<PhaseBuilder> builders;

	private List<Phase> phases;

	private TournamentBuilder builder;

	@Before
	public void setUp() throws Exception
	{
		teams = Lists.newArrayList();
		builders = Lists.newArrayList();
		phases = Lists.newArrayList();
		for (int i = 0; i < 8; i++) {
			teams.add(mock(ITeam.class));
		}
		for (int i = 0; i < 5; i++) {
			builders.add(mock(PhaseBuilder.class));
			phases.add(mock(Phase.class));
		}
		builder = new TournamentBuilder(factory, builderProvider);
	}

	@Test
	public void testBuild() throws Exception
	{
		when(builderProvider.get()).thenReturn(builders.get(0), builders.get(1),
				builders.get(2), builders.get(3), builders.get(4));
		builder.numberOfClusters(2).gameDuration(5).withTeams(teams);
		builder.addGroupPhase(2, 2);
		verify(builders.get(0)).numberOfGroups(2);
		verify(builders.get(0)).numberOfQualifyingTeams(2);
		builder.addGroupPhase(1, 4);
		verify(builders.get(1)).numberOfGroups(1);
		verify(builders.get(1)).numberOfQualifyingTeams(4);
		when(builders.get(0).buildGroupPhase("Group Phase 1")).thenReturn(
				phases.get(0));
		when(builders.get(1).buildGroupPhase("Group Phase 2")).thenReturn(
				phases.get(1));
		when(phases.get(1).getQualifyingTeams()).thenReturn(gp2QualifyingTeams);
		when(gp2QualifyingTeams.size()).thenReturn(4);
		when(builders.get(2).buildKoPhase("Semifinals"))
				.thenReturn(phases.get(2));
		when(semiFinalRetiringTeams.size()).thenReturn(2);
		when(phases.get(2).getRetiringTeams()).thenReturn(semiFinalRetiringTeams);
		when(builders.get(3).buildKoPhase("Third Place Play-Off")).thenReturn(
				phases.get(3));
		when(builders.get(4).buildKoPhase("Final")).thenReturn(phases.get(4));
		Tournament mockTournament = mock(Tournament.class);
		when(
				factory.create(Matchers.matches("tournamentname"), Matchers.eq(2),
						Matchers.eq(5.0), (LinkedHashSet<ITeam>) Matchers
								.argThat(new CollectionArgumentMatcher<>(teams)),
						(LinkedHashSet<Phase>) Matchers
								.argThat(new CollectionArgumentMatcher<>(phases))))
				.thenReturn(mockTournament);
		Tournament tournament = builder.build("tournamentname");
		verify(builders.get(0)).teams(Matchers.anyObject());
		verify(builders.get(1)).previousPhase(phases.get(0));
		verify(builders.get(2)).previousPhase(phases.get(1));
		assertSame(mockTournament, tournament);
	}

	@Test(expected = IllegalStateException.class)
	public void testBuildTeamsNotUnique() throws Exception
	{
		teams.remove(0);
		teams.add(teams.get(0)); // Duplicate
		builder.withTeams(teams);
		builder.build("tournament");
	}

	private static class CollectionArgumentMatcher<T> extends
			ArgumentMatcher<Collection<T>>
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
