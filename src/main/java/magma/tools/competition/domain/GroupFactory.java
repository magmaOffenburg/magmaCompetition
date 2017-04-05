package magma.tools.competition.domain;

import java.util.LinkedHashSet;

import com.google.inject.assistedinject.Assisted;

public interface GroupFactory {
	Group create(@Assisted("name") String name, @Assisted("teams") LinkedHashSet<ITeam> teams);
}
