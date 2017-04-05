package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;

@JsonIgnoreProperties({"dateProvider", "changeHandlers"})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
@JsonPropertyOrder({"homeTeam", "guestTeam", "result"})
public class Game implements ChangeNotifier<Game>, ChangeHandler<GameResult>, Serializable
{
	private static final long serialVersionUID = 3400100364154057714L;

	private final Provider<Date> dateProvider;

	private final List<ChangeHandler<Game>> changeHandlers;

	private final ITeam homeTeam;

	private final ITeam guestTeam;

	private final GameResult result;

	private GameState state;

	private Date plannedStartTime;

	private Date realStartTime;

	private boolean decisionGame;

	@Inject
	@JsonCreator
	Game(@JacksonInject("dateProvider") Provider<Date> dateProvider, @JsonProperty("result") GameResult result,
			@Assisted("homeTeam") @JsonProperty("homeTeam") ITeam homeTeam,
			@Assisted("guestTeam") @JsonProperty("guestTeam") ITeam guestTeam)
	{
		checkNotNull(homeTeam);
		checkNotNull(guestTeam);
		this.dateProvider = dateProvider;
		this.result = result;
		this.homeTeam = homeTeam;
		this.guestTeam = guestTeam;
		changeHandlers = Lists.newLinkedList();
		state = GameState.PLANNED;
		decisionGame = false;
		result.addChangeHandler(this);
	}

	public ITeam getHomeTeam()
	{
		return homeTeam;
	}

	public ITeam getGuestTeam()
	{
		return guestTeam;
	}

	public GameResult getResult()
	{
		return result;
	}

	public GameState getState()
	{
		return state;
	}

	public void setState(GameState state)
	{
		checkNotNull(state);
		this.state = state;
		if (state == GameState.STARTED) {
			realStartTime = dateProvider.get();
		}
		fireChangeEvent();
	}

	public Date getPlannedStartTime()
	{
		return plannedStartTime;
	}

	public void setPlannedStartTime(Date startTime)
	{
		this.plannedStartTime = startTime;
		fireChangeEvent();
	}

	public Date getRealStartTime()
	{
		return realStartTime;
	}

	public void setDecisionGame(boolean decisionGame)
	{
		this.decisionGame = decisionGame;
	}

	public boolean isDecisionGame()
	{
		return this.decisionGame;
	}

	@Override
	public void addChangeHandler(ChangeHandler<Game> handler)
	{
		checkNotNull(handler);
		if (!changeHandlers.contains(handler)) {
			changeHandlers.add(handler);
		}
	}

	@Override
	public void removeChangeHandler(ChangeHandler<Game> handler)
	{
		checkNotNull(handler);
		changeHandlers.remove(handler);
	}

	@Override
	public void onChange(GameResult subject)
	{
		fireChangeEvent();
	}

	private void fireChangeEvent()
	{
		for (ChangeHandler<Game> handler : changeHandlers) {
			handler.onChange(this);
		}
	}

	@Override
	public String toString()
	{
		return String.format(
				"{Home Team: %s, Guest Team: %s, Result: %s, State: %s, Planned Start Time: %s, Real Start Time: %s}",
				homeTeam, guestTeam, result, state, plannedStartTime, realStartTime);
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(homeTeam).append(guestTeam).toHashCode();
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
		Game other = (Game) obj;
		return new EqualsBuilder().append(homeTeam, other.homeTeam).append(guestTeam, other.guestTeam).isEquals();
	}
}
