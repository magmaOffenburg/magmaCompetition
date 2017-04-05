package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

@JsonIgnoreProperties({"changeHandlers"})
public class GameResult implements ChangeNotifier<GameResult>, Serializable
{
	private static final long serialVersionUID = -2026172769669521418L;

	private final List<ChangeHandler<GameResult>> changeHandlers;

	private int homeTeamPoints;

	private int guestTeamPoints;

	@Inject
	GameResult()
	{
		homeTeamPoints = 0;
		guestTeamPoints = 0;
		changeHandlers = Lists.newLinkedList();
	}

	@JsonCreator
	GameResult(@JsonProperty("homeTeamPoints") int homeTeamPoints, @JsonProperty("guestTeamPoints") int guestTeamPoints)
	{
		this.homeTeamPoints = homeTeamPoints;
		this.guestTeamPoints = guestTeamPoints;
		changeHandlers = Lists.newLinkedList();
	}

	public int getHomeTeamPoints()
	{
		return homeTeamPoints;
	}

	public void setHomeTeamPoints(int homeTeamPoints)
	{
		this.homeTeamPoints = homeTeamPoints;
		fireChangeEvent();
	}

	public int getGuestTeamPoints()
	{
		return guestTeamPoints;
	}

	public void setGuestTeamPoints(int guestTeamPoints)
	{
		this.guestTeamPoints = guestTeamPoints;
		fireChangeEvent();
	}

	@Override
	public void addChangeHandler(ChangeHandler<GameResult> handler)
	{
		checkNotNull(handler);
		if (!changeHandlers.contains(handler)) {
			changeHandlers.add(handler);
		}
	}

	@Override
	public void removeChangeHandler(ChangeHandler<GameResult> handler)
	{
		checkNotNull(handler);
		changeHandlers.remove(handler);
	}

	private void fireChangeEvent()
	{
		for (ChangeHandler<GameResult> handler : changeHandlers) {
			handler.onChange(this);
		}
	}

	@Override
	public String toString()
	{
		return String.format("%s : %s", homeTeamPoints, guestTeamPoints);
	}
}
