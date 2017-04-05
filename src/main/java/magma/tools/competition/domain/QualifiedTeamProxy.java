package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class QualifiedTeamProxy extends TeamProxy
{
	private static final long serialVersionUID = -6253748894590560410L;

	@JsonProperty
	private final Group group;

	@JsonProperty
	private final int rank;

	@Inject
	@JsonCreator
	QualifiedTeamProxy(
			@Assisted("group") @JsonProperty("group") Group group, @Assisted("rank") @JsonProperty("rank") int rank)
	{
		checkGroup(group);
		checkRank(rank, group);
		this.group = group;
		this.rank = rank;
	}

	@Override
	protected ITeam resolve()
	{
		GroupResult result = group.getResult();
		if (result.isFinal()) {
			List<ITeam> teamsOnRank = result.getTeamsOnRank(rank);
			if (teamsOnRank.size() == 1) {
				return teamsOnRank.get(0);
			}
		}
		return null;
	}

	@Override
	protected String getProxiedName()
	{
		if (rank == 1) {
			return String.format("1st Group %s", group.getName());
		} else if (rank == 2) {
			return String.format("2nd Group %s", group.getName());
		} else if (rank == 3) {
			return String.format("3rd Group %s", group.getName());
		} else {
			return String.format("%dth Group %s", rank, group.getName());
		}
	}

	private void checkGroup(Group group)
	{
		checkNotNull(group);
	}

	private void checkRank(int rank, Group group)
	{
		checkArgument(rank > 0, "A rank must be non-negative.");
		checkArgument(rank <= group.getTeams().size(),
				String.format("The rank '%s' cannot occur in the given group with only '%s' teams.", rank,
						group.getTeams().size()));
	}

	@Override
	public String toString()
	{
		return getProxiedName();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(group).append(rank).toHashCode();
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
		QualifiedTeamProxy other = (QualifiedTeamProxy) obj;
		return new EqualsBuilder().append(group, other.group).append(rank, other.rank).isEquals();
	}
}
