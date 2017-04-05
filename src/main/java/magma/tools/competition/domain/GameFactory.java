package magma.tools.competition.domain;

import com.google.inject.assistedinject.Assisted;

public interface GameFactory {
	Game create(@Assisted("homeTeam") ITeam homeTeam, @Assisted("guestTeam") ITeam guestTeam);
}
