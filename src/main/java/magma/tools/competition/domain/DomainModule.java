package magma.tools.competition.domain;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class DomainModule extends AbstractModule
{

	@Override
	protected void configure()
	{
		install(new FactoryModuleBuilder()
				.implement(ITeam.class, Names.named("team"), Team.class)
				.implement(ITeam.class, Names.named("qualifiedTeamProxy"),
						QualifiedTeamProxy.class)
				.implement(ITeam.class, Names.named("gameWinnerProxy"),
						GameWinnerProxy.class)
				.implement(ITeam.class, Names.named("gameLooserProxy"),
						GameLooserProxy.class).build(TeamFactory.class));
		bind(TeamBuilder.class);
		install(new FactoryModuleBuilder().implement(Tournament.class,
				Tournament.class).build(TournamentFactory.class));
		install(new FactoryModuleBuilder().implement(Game.class, Game.class)
				.build(GameFactory.class));
		bind(GameResult.class);
		install(new FactoryModuleBuilder().implement(Group.class, Group.class)
				.build(GroupFactory.class));
		install(new FactoryModuleBuilder().implement(GroupPlan.class,
				GroupPlan.class).build(GroupPlanFactory.class));
		install(new FactoryModuleBuilder()
				.implement(Phase.class, Names.named("groupPhase"), GroupPhase.class)
				.implement(Phase.class, Names.named("koPhase"), KoPhase.class)
				.build(PhaseFactory.class));
		bind(GroupResultCalculationStrategy.class);
		install(new FactoryModuleBuilder().implement(GroupResult.class,
				GroupResult.class).build(GroupResultFactory.class));
		install(new FactoryModuleBuilder().implement(DrawingBowl.class,
				DrawingBowl.class).build(DrawingBowlFactory.class));
		bind(GroupDrawStrategy.class);
		bind(PhaseBuilder.class);
		bind(TournamentBuilder.class);
	}
}
