package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
@JsonIgnoreProperties({"resultFactory", "result"})
public class Group implements Serializable
{
	private static final long serialVersionUID = -736123873217880933L;

	private final GroupResultFactory resultFactory;

	private final String name;

	private final List<ITeam> teams;

	private final GroupPlan plan;

	@Inject
	Group(GameUtil gameUtil, GroupPlanFactory planFactory, GroupResultFactory resultFactory,
			@Assisted("name") String name, @Assisted("teams") LinkedHashSet<ITeam> teams)
	{
		super();
		checkName(name);
		checkTeams(teams);
		this.name = name;
		this.teams = Lists.newArrayList(teams);
		List<Game> games = gameUtil.createGroupPhaseGames(teams);
		plan = planFactory.create(games);
		this.resultFactory = resultFactory;
	}

	@JsonCreator
	Group(@JacksonInject("groupResultFactory") GroupResultFactory resultFactory, @JsonProperty("name") String name,
			@JsonProperty("teams") LinkedHashSet<ITeam> teams, @JsonProperty("plan") GroupPlan plan)
	{
		super();
		checkName(name);
		checkTeams(teams);
		checkPlan(plan);
		this.name = name;
		this.teams = Lists.newArrayList(teams);
		this.plan = plan;
		this.resultFactory = resultFactory;
	}

	public String getName()
	{
		return name;
	}

	public List<ITeam> getTeams()
	{
		return Collections.unmodifiableList(teams);
	}

	public GroupPlan getPlan()
	{
		return plan;
	}

	public GroupResult getResult()
	{
		return resultFactory.create(plan.getGames());
	}

	@JsonIgnore
	public List<Game> getTieBreakGames()
	{
		List<Game> groupGames = plan.getGames();
		ArrayList<Game> games = null;
		boolean isTieBreakGamesCreated = false;
		boolean isGroupFinished = true;

		for (int k = 0; k < groupGames.size(); k++) {
			if (groupGames.get(k).getState() != GameState.FINISHED && groupGames.get(k).isDecisionGame() == false) {
				isGroupFinished = false;
			}
		}

		if (isGroupFinished == true) {
			games = new ArrayList<Game>();
			for (int j = 0; j < groupGames.size(); j++) {
				if (groupGames.get(j).isDecisionGame() == true) {
					isTieBreakGamesCreated = true;
					games.add(groupGames.get(j));
				}
			}

			if (isTieBreakGamesCreated == false) {
				games = (ArrayList<Game>) resultFactory.create(plan.getGames()).getTieBreakGames();
				if (games != null) {
					for (int i = 0; i < games.size(); i++) {
						plan.addGame(games.get(i));
					}
				}
			}
		}

		return games;
	}

	private void checkName(String name)
	{
		checkNotNull(name);
		checkArgument(!name.trim().isEmpty(), "A group must have an non-empty name.");
	}

	private void checkTeams(LinkedHashSet<ITeam> teams)
	{
		checkNotNull(teams);
		checkArgument(teams.size() > 1, "There must be at least two teams in a group.");
	}

	private void checkPlan(GroupPlan plan)
	{
		checkNotNull(plan);
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
		Group other = (Group) obj;
		return new EqualsBuilder().append(name, other.name).isEquals();
	}
}
