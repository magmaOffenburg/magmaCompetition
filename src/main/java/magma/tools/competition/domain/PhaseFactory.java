package magma.tools.competition.domain;

import java.util.LinkedHashSet;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public interface PhaseFactory {
	@Named("groupPhase")
	Phase createGroupPhase(@Assisted("name") String name, @Assisted("groups") LinkedHashSet<Group> groups,
			@Assisted("numberOfQualifyingTeams") int numberOfQualifyingTeams);

	@Named("koPhase")
	Phase createKoPhase(@Assisted("name") String name, @Assisted("teams") LinkedHashSet<ITeam> teams);
}
