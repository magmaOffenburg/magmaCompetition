package magma.tools.competition.domain;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public interface TeamFactory {
	@Named("team")
	ITeam create(@Assisted("name") String name, @Assisted("setTeam") boolean setTeam,
			@Assisted("username") String username, @Assisted("startScriptFileName") String startScriptFileName,
			@Assisted("pathToScriptFile") String pathToScriptFile);

	@Named("qualifiedTeamProxy")
	ITeam createProxy(@Assisted("group") Group group, @Assisted("rank") int rank);

	@Named("gameWinnerProxy")
	ITeam createWinnerProxy(@Assisted("game") Game game);

	@Named("gameLooserProxy")
	ITeam createLooserProxy(@Assisted("game") Game game);
}
