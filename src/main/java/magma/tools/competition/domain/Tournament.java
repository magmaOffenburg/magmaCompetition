package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class Tournament implements Serializable
{
	private static final long serialVersionUID = 8406424837295144630L;

	private final String name;

	private final int numberOfClusters;

	private final double gameDuration;

	private final List<ITeam> teams;

	private final List<Phase> phases;

	@Inject
	@JsonCreator
	Tournament(@Assisted("name") @JsonProperty("name") String name,
			@Assisted("numberOfClusters") @JsonProperty("numberOfClusters") int numberOfClusters,
			@Assisted("gameDuration") @JsonProperty("gameDuration") double gameDuration,
			@Assisted("teams") @JsonProperty("teams") LinkedHashSet<ITeam> teams,
			@Assisted("phases") @JsonProperty("phases") LinkedHashSet<Phase> phases)
	{
		super();
		checkName(name);
		checkNumberOfClusters(numberOfClusters);
		checkGameDuration(gameDuration);
		checkTeams(teams);
		checkPhases(phases);
		this.name = name;
		this.numberOfClusters = numberOfClusters;
		this.gameDuration = gameDuration;
		this.teams = Lists.newArrayList(teams);
		this.phases = Lists.newArrayList(phases);
	}

	public String getName()
	{
		return name;
	}

	public int getNumberOfClusters()
	{
		return numberOfClusters;
	}

	public double getGameDuration()
	{
		return gameDuration;
	}

	public List<ITeam> getTeams()
	{
		return Collections.unmodifiableList(teams);
	}

	public List<Phase> getPhases()
	{
		return Collections.unmodifiableList(phases);
	}

	private void checkName(String tournamentName)
	{
		checkNotNull(tournamentName);
		checkArgument(tournamentName.trim().length() > 0, "A Tournament's name must not be empty.");
	}

	private void checkNumberOfClusters(int numberOfClusters)
	{
		checkArgument(numberOfClusters > 0,
				String.format("There must be at least a single cluster, given: '%s').", numberOfClusters));
	}

	private void checkGameDuration(double gameDuration)
	{
		checkArgument(gameDuration > 0, String.format("A games duration must be > 0, given: '%s'", gameDuration));
	}

	private void checkTeams(Set<ITeam> teams)
	{
		checkNotNull(teams);
		checkArgument(teams.size() > 1,
				String.format("There must be at least two teams in a tournament, given '%s'", teams.size()));
	}

	private void checkPhases(LinkedHashSet<Phase> phases)
	{
		checkNotNull(phases);
		checkArgument(!phases.isEmpty(), "There must be at least a single phase in a tournament.");
	}

	@Override
	public String toString()
	{
		return String.format("{Name: %s, Number of Clusters: %s, Game Duration: %s, Teams: %s, Phases: %s}", name,
				numberOfClusters, gameDuration, teams, phases);
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(19, 23)
				.append(name)
				.append(numberOfClusters)
				.append(gameDuration)
				.append(teams)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Tournament other = (Tournament) obj;
		return new EqualsBuilder()
				.append(name, other.name)
				.append(numberOfClusters, other.numberOfClusters)
				.append(gameDuration, other.gameDuration)
				.append(teams, other.teams)
				.isEquals();
	}
}
