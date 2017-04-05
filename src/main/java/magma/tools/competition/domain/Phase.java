package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = GroupPhase.class, name = "groupPhase")
	, @Type(value = KoPhase.class, name = "koPhase")
})
@JsonIgnoreProperties({"teamFactory", "qualifyingTeams", "retiringTeams"})
public abstract class Phase implements Serializable
{
	private static final long serialVersionUID = -2952708493051846366L;

	private final String name;

	Phase(String name)
	{
		super();
		checkName(name);
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public abstract List<ITeam> getQualifyingTeams();

	public abstract List<ITeam> getRetiringTeams();

	private void checkName(String name)
	{
		checkNotNull(name);
		checkArgument(!name.trim().isEmpty(), "A phase must have an non-empty name.");
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(name).toHashCode();
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
		Phase other = (Phase) obj;
		return new EqualsBuilder().append(name, other.name).isEquals();
	}
}
