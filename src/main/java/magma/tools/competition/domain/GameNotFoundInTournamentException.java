package magma.tools.competition.domain;

public class GameNotFoundInTournamentException extends Exception
{
	private static final long serialVersionUID = 1L;

	public GameNotFoundInTournamentException(Game searchedGame)
	{
		super("The following game couldn't be found in the tournament: " + searchedGame.getHomeTeam().getName() +
				" : " + searchedGame.getGuestTeam().getName() + "!");
	}
}
