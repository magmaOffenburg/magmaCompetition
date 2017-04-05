package magma.tools.competition.tournamentRunner;

import java.util.List;

import magma.tools.competition.domain.Game;

public interface ITournamentRunner {
	public void setPhaseName(String phaseName);

	public void setGames(List<Game> games);

	public void setGameDuration(double d);

	public void startTournament();

	public void stopTournament();

	public boolean isRunning();
}
