package magma.tools.competition.domain;

import java.util.LinkedHashSet;

import com.google.inject.assistedinject.Assisted;

public interface DrawingBowlFactory
{

	DrawingBowl create(@Assisted("items") LinkedHashSet<ITeam> teams);

}
