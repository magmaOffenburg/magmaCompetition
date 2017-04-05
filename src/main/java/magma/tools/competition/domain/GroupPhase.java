package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class GroupPhase extends Phase
{
	private static final long serialVersionUID = 1973258013283590086L;

	private final TeamFactory teamFactory;

	private final List<Group> groups;

	private final int numberOfQualifyingTeams;

	@Inject
	@JsonCreator
	GroupPhase(@JacksonInject("teamFactory") TeamFactory teamFactory,
			@Assisted("name") @JsonProperty("name") String name,
			@Assisted("groups") @JsonProperty("groups") LinkedHashSet<Group> groups,
			@Assisted("numberOfQualifyingTeams") @JsonProperty("numberOfQualifyingTeams") int numberOfQualifyingTeams)
	{
		super(name);
		checkGroups(groups);
		checkNumberOfQualifyingTeams(numberOfQualifyingTeams);
		this.teamFactory = teamFactory;
		this.groups = Lists.newArrayList(groups);
		this.numberOfQualifyingTeams = numberOfQualifyingTeams;
	}

	public List<Group> getGroups()
	{
		return Collections.unmodifiableList(groups);
	}

	public int getNumberOfQualifyingTeams()
	{
		return numberOfQualifyingTeams;
	}

	@JsonIgnore
	public int getNumberOfRetiringTeams()
	{
		int numberOfRetiringTeams = 0;
		for (Group group : groups) {
			int groupSize = group.getTeams().size();
			numberOfRetiringTeams += (groupSize - numberOfQualifyingTeams);
		}
		return numberOfRetiringTeams;
	}

	@Override
	public List<ITeam> getQualifyingTeams()
	{
		List<ITeam> teams = Lists.newArrayList();
		for (Group group : groups) {
			for (int i = 1; i <= numberOfQualifyingTeams; i++) {
				ITeam proxy = teamFactory.createProxy(group, i);
				teams.add(proxy);
			}
		}
		return Collections.unmodifiableList(teams);
	}

	@Override
	public List<ITeam> getRetiringTeams()
	{
		List<ITeam> teams = Lists.newArrayList();
		for (Group group : groups) {
			for (int i = numberOfQualifyingTeams; i < group.getTeams().size(); i++) {
				ITeam proxy = teamFactory.createProxy(group, i);
				teams.add(proxy);
			}
		}
		return Collections.unmodifiableList(teams);
	}

	private void checkGroups(LinkedHashSet<Group> groups)
	{
		checkNotNull(groups);
		checkArgument(!groups.isEmpty(), "There must be at least a single group in a group phase.");
	}

	private void checkNumberOfQualifyingTeams(int numberOfQualifyingTeams)
	{
		checkArgument(numberOfQualifyingTeams > 0, "The number of qualifying teams must be > 0.");
	}
}
