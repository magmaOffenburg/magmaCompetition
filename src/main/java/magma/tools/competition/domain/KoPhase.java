package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class KoPhase extends Phase
{
	private static final long serialVersionUID = 5730353021174883685L;

	private final TeamFactory teamFactory;

	private List<Game> games;

	@Inject
	KoPhase(TeamFactory teamFactory, GameUtil gameUtil, @Assisted("name") String name,
			@Assisted("teams") LinkedHashSet<ITeam> teams)
	{
		super(name);
		checkTeams(teams);
		this.teamFactory = teamFactory;
		games = gameUtil.createKoPhaseGames(teams);
	}

	@JsonCreator
	KoPhase(@JacksonInject("teamFactory") TeamFactory teamFactory, @JsonProperty("name") String name,
			@JsonProperty("games") List<Game> games)
	{
		super(name);
		checkGames(games);
		this.teamFactory = teamFactory;
		this.games = games;
	}

	public List<Game> getGames()
	{
		return this.games;
	}

	@Override
	public List<ITeam> getQualifyingTeams()
	{
		List<ITeam> teams = Lists.newArrayList();
		for (Game game : games) {
			ITeam proxy = teamFactory.createWinnerProxy(game);
			teams.add(proxy);
		}
		return Collections.unmodifiableList(teams);
	}

	@Override
	public List<ITeam> getRetiringTeams()
	{
		List<ITeam> teams = Lists.newArrayList();
		for (Game game : games) {
			ITeam proxy = teamFactory.createLooserProxy(game);
			teams.add(proxy);
		}
		return Collections.unmodifiableList(teams);
	}

	private void checkTeams(LinkedHashSet<ITeam> teams)
	{
		checkNotNull(teams);
	}

	private void checkGames(List<Game> games)
	{
		checkNotNull(games);
		checkArgument(games.size() >= 1, "There must be at least one game in a Ko-Phase.");
	}
}
