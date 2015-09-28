package magma.tools.competition.domain;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class TournamentBuilder
{

	private static final String[] KO_PHASE_NAMES = { "Final", "Semifinals",
			"Quarterfinals", "Round of last 16", "Round of last 32", };

	private static final String THIRD_PLACE_PLAYOFF_NAME = "Third Place Play-Off";

	private final TournamentFactory factory;

	private final Provider<PhaseBuilder> builderProvider;

	private int numberOfClusters;

	private double gameDuration;

	private List<ITeam> teams;

	private List<PhaseBuilder> builders;

	@Inject
	public TournamentBuilder(TournamentFactory tournamentFactory,
			Provider<PhaseBuilder> builderProvider)
	{
		this.factory = tournamentFactory;
		this.builderProvider = builderProvider;
		teams = Lists.newArrayList();
		builders = Lists.newArrayList();
	}

	public TournamentBuilder numberOfClusters(int numberOfClusters)
	{
		this.numberOfClusters = numberOfClusters;
		return this;
	}

	public TournamentBuilder gameDuration(double gameDuration)
	{
		this.gameDuration = gameDuration;
		return this;
	}

	public void withTeams(Collection<ITeam> teams)
	{
		this.teams.addAll(teams);
	}

	public void addGroupPhase(int numberOfGroups, int numberOfQualifyingTeams)
	{
		PhaseBuilder builder = builderProvider.get();
		builder.numberOfGroups(numberOfGroups);
		builder.numberOfQualifyingTeams(numberOfQualifyingTeams);
		builders.add(builder);
	}

	public Tournament build(String name)
	{
		LinkedHashSet<ITeam> uniqueTeams = getUniqueTeams();
		List<Phase> phases = createGroupPhases(uniqueTeams);
		addKoPhases(phases);
		return factory.create(name, numberOfClusters, gameDuration, uniqueTeams,
				Sets.newLinkedHashSet(phases));
	}

	private List<Phase> createGroupPhases(LinkedHashSet<ITeam> uniqueTeams)
	{
		List<Phase> phases = Lists.newArrayList();
		for (int i = 0; i < builders.size(); i++) {
			PhaseBuilder builder = builders.get(i);
			if (i == 0) {
				builder.teams(uniqueTeams);
			} else {
				builder.previousPhase(phases.get(phases.size() - 1));
			}
			phases.add(builder.buildGroupPhase("Group Phase " + (i + 1)));
		}
		return phases;
	}

	private void addKoPhases(List<Phase> phases)
	{
		Phase previousPhase = phases.get(phases.size() - 1);
		int numTeams = previousPhase.getQualifyingTeams().size();
		int numKoPhases = (int) (Math.log(numTeams) / Math.log(2));
		while (numKoPhases-- > 0) {
			PhaseBuilder builder = builderProvider.get();
			builder.previousPhase(previousPhase);
			phases.add(builder.buildKoPhase(KO_PHASE_NAMES[numKoPhases]));
			previousPhase = phases.get(phases.size() - 1);
			if (numKoPhases == 1) {
				if (previousPhase.getRetiringTeams().size() == 2) {
					builder = builderProvider.get();
					builder.previousPhase(previousPhase);
					builder.useRetiringTeams(true);
					phases.add(builder.buildKoPhase(THIRD_PLACE_PLAYOFF_NAME));
				} else {
					throw new IllegalStateException(
							"Invalid configuration: A third-place playoff cannot be created with more than two teams.");
				}
			}
		}
	}

	private LinkedHashSet<ITeam> getUniqueTeams()
	{
		LinkedHashSet<ITeam> uniqueTeams = Sets.newLinkedHashSet();
		for (ITeam team : teams) {
			if (!uniqueTeams.contains(team)) {
				uniqueTeams.add(team);
			} else {
				throw new IllegalStateException(
						"The team name must be unique in a tournament.");
			}
		}
		return uniqueTeams;
	}

}
