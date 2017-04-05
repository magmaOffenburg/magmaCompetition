package magma.tools.competition.domain;

import java.util.LinkedHashSet;

import com.google.inject.assistedinject.Assisted;

public interface TournamentFactory {
	public Tournament create(@Assisted("name") String name, @Assisted("numberOfClusters") int numberOfClusters,
			@Assisted("gameDuration") double gameDuration, @Assisted("teams") LinkedHashSet<ITeam> teams,
			@Assisted("phases") LinkedHashSet<Phase> phases);
}
