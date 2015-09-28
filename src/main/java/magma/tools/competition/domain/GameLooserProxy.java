package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class GameLooserProxy extends TeamProxy
{

	private static final long serialVersionUID = -9222006140621317916L;

	@JsonProperty
	private final Game game;

	@Inject
	@JsonCreator
	GameLooserProxy(@Assisted("game") @JsonProperty("game") Game game)
	{
		checkGame(game);
		this.game = game;
	}

	@Override
	protected ITeam resolve()
	{
		if (game.getState() == GameState.FINISHED) {
			GameResult result = game.getResult();
			if (result.getHomeTeamPoints() > result.getGuestTeamPoints()) {
				return game.getGuestTeam();
			} else if (result.getHomeTeamPoints() < result.getGuestTeamPoints()) {
				return game.getHomeTeam();
			}
		}
		return null;
	}

	@Override
	protected String getProxiedName()
	{
		return String.format("Looser '%s : %s'", game.getHomeTeam().getName(),
				game.getGuestTeam().getName());
	}

	private void checkGame(Game game)
	{
		checkNotNull(game);
	}

	@Override
	public String toString()
	{
		return getProxiedName();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(game).toHashCode();
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
		GameLooserProxy other = (GameLooserProxy) obj;
		return new EqualsBuilder().append(game, other.game).isEquals();
	}

}
