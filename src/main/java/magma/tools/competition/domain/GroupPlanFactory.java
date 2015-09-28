package magma.tools.competition.domain;

import java.util.List;

import com.google.inject.assistedinject.Assisted;

public interface GroupPlanFactory
{

	GroupPlan create(@Assisted("games") List<Game> games);

}
