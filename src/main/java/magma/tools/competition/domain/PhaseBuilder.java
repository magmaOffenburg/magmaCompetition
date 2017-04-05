package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.util.ShiftableMatrix;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class PhaseBuilder
{
	private final PhaseFactory phaseFactory;

	private final GroupFactory groupFactory;

	private final GroupDrawStrategy drawStrategy;

	private Phase previousPhase;

	private LinkedHashSet<ITeam> teams;

	private int numberOfGroups;

	private int numberOfQualifyingTeams;

	private boolean useRetiringTeams;

	@Inject
	public PhaseBuilder(PhaseFactory phaseFactory, GroupFactory groupFactory, GroupDrawStrategy drawStrategy)
	{
		this.phaseFactory = phaseFactory;
		this.groupFactory = groupFactory;
		this.drawStrategy = drawStrategy;
	}

	public PhaseBuilder teams(LinkedHashSet<ITeam> teams)
	{
		this.teams = teams;
		return this;
	}

	public PhaseBuilder previousPhase(Phase previousPhase)
	{
		this.previousPhase = previousPhase;
		return this;
	}

	public PhaseBuilder numberOfGroups(int numberOfGroups)
	{
		this.numberOfGroups = numberOfGroups;
		return this;
	}

	public PhaseBuilder numberOfQualifyingTeams(int numberOfQualifyingTeams)
	{
		this.numberOfQualifyingTeams = numberOfQualifyingTeams;
		return this;
	}

	public PhaseBuilder useRetiringTeams(boolean useRetiringTeams)
	{
		this.useRetiringTeams = useRetiringTeams;
		return this;
	}

	public Phase buildGroupPhase(String name)
	{
		if (previousPhase == null) {
			return buildInitialGroupPhase(name);
		} else if (GroupPhase.class.isAssignableFrom(previousPhase.getClass())) {
			return buildGroupPhase(name, (GroupPhase) previousPhase);
		} else {
			throw new IllegalStateException("Illegal configuration: A Group Phase cannot be preceded by a Ko Phase.");
		}
	}

	public Phase buildKoPhase(String name)
	{
		if (previousPhase != null) {
			if (GroupPhase.class.isAssignableFrom(previousPhase.getClass())) {
				return buildKoPhase(name, (GroupPhase) previousPhase);
			} else if (KoPhase.class.isAssignableFrom(previousPhase.getClass())) {
				return buildKoPhase(name, (KoPhase) previousPhase);
			}
		}
		throw new IllegalStateException("Illegal configuration: A Ko Phase must be preceded by a Group Phase.");
	}

	private Phase buildInitialGroupPhase(String name)
	{
		checkState(useRetiringTeams == false,
				"Illegal configuration: Cannot build initial group phase using retiring teams.");
		List<LinkedHashSet<ITeam>> teamBuckets = drawStrategy.draw(numberOfGroups, teams);
		LinkedHashSet<Group> groups = createGroups(teamBuckets);
		return phaseFactory.createGroupPhase(name, groups, numberOfQualifyingTeams);
	}

	private Phase buildGroupPhase(String name, GroupPhase previousPhase)
	{
		ShiftableMatrix<ITeam> matrix = createTeamsMatrix(previousPhase);
		matrix.reorder();
		List<LinkedHashSet<ITeam>> teamBuckets = sortToBuckets(numberOfGroups, matrix);
		LinkedHashSet<Group> groups = createGroups(teamBuckets);
		return phaseFactory.createGroupPhase(name, groups, numberOfQualifyingTeams);
	}

	private Phase buildKoPhase(String name, GroupPhase previousPhase)
	{
		ShiftableMatrix<ITeam> matrix = createTeamsMatrix(previousPhase);
		matrix.reorder();
		Iterator<ITeam> iterator = matrix.iteratorColumnsFirst();
		LinkedHashSet<ITeam> teams = Sets.newLinkedHashSet();
		Iterators.addAll(teams, iterator);
		return phaseFactory.createKoPhase(name, teams);
	}

	private Phase buildKoPhase(String name, KoPhase previousPhase)
	{
		List<ITeam> qualifyingTeams;
		if (useRetiringTeams == true) {
			qualifyingTeams = previousPhase.getRetiringTeams();
		} else {
			qualifyingTeams = previousPhase.getQualifyingTeams();
		}
		LinkedHashSet<ITeam> teams = Sets.newLinkedHashSet(qualifyingTeams);
		return phaseFactory.createKoPhase(name, teams);
	}

	private LinkedHashSet<Group> createGroups(List<LinkedHashSet<ITeam>> teamBuckets)
	{
		LinkedHashSet<Group> groups = Sets.newLinkedHashSet();
		for (int i = 0; i < teamBuckets.size(); i++) {
			Group group = groupFactory.create(String.valueOf((char) (65 + i)), teamBuckets.get(i));
			groups.add(group);
		}
		return groups;
	}

	private ShiftableMatrix<ITeam> createTeamsMatrix(GroupPhase previousPhase)
	{
		List<Group> previousGroups = previousPhase.getGroups();
		ShiftableMatrix<ITeam> matrix;
		List<ITeam> qualifyingTeams;
		if (useRetiringTeams == true) {
			matrix = new ShiftableMatrix<>(previousGroups.size(), previousPhase.getNumberOfRetiringTeams());
			qualifyingTeams = previousPhase.getRetiringTeams();
		} else {
			matrix = new ShiftableMatrix<>(previousGroups.size(), previousPhase.getNumberOfQualifyingTeams());
			qualifyingTeams = previousPhase.getQualifyingTeams();
		}
		matrix.putAllColumnsFirst(qualifyingTeams);
		return matrix;
	}

	private List<LinkedHashSet<ITeam>> sortToBuckets(int numBuckets, ShiftableMatrix<ITeam> matrix)
	{
		List<LinkedHashSet<ITeam>> buckets = Lists.newArrayList();
		for (int i = 0; i < numBuckets; i++) {
			buckets.add(Sets.newLinkedHashSet());
		}
		Iterator<ITeam> teamIterator = matrix.iteratorRowsFirst();
		Iterator<LinkedHashSet<ITeam>> bucketIterator = Iterators.cycle(buckets);
		while (teamIterator.hasNext()) {
			LinkedHashSet<ITeam> bucket = bucketIterator.next();
			bucket.add(teamIterator.next());
		}
		return buckets;
	}
}
