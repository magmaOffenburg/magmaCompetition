package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class GroupPlan implements ChangeNotifier<GroupPlan>, Serializable
{

	private static final long serialVersionUID = 8616937031552404724L;

	private final List<ChangeHandler<GroupPlan>> changeHandlers;

	private List<Game> games;

	@Inject
	@JsonCreator
	GroupPlan(@Assisted("games") @JsonProperty("games") List<Game> games)
	{
		checkNotNull(games);
		checkArgument(games.size() >= 1,
				"There must be at least a single game in a group plan.");
		this.games = games;
		this.changeHandlers = Lists.newLinkedList();
	}

	public List<Game> getGames()
	{
		return Collections.unmodifiableList(games);
	}

	public void addGame(Game game)
	{
		checkNotNull(game);
		games.add(game);
		fireChangeEvent();
	}

	@Override
	public void addChangeHandler(ChangeHandler<GroupPlan> handler)
	{
		checkNotNull(handler);
		if (!changeHandlers.contains(handler)) {
			changeHandlers.add(handler);
		}
	}

	@Override
	public void removeChangeHandler(ChangeHandler<GroupPlan> handler)
	{
		checkNotNull(handler);
		changeHandlers.remove(handler);
	}

	private void fireChangeEvent()
	{
		for (ChangeHandler<GroupPlan> handler : changeHandlers) {
			handler.onChange(this);
		}
	}

	@Override
	public String toString()
	{
		return games.toString();
	}

}
