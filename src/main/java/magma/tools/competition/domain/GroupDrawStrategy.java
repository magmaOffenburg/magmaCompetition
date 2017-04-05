package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class GroupDrawStrategy
{
	private DrawingBowlFactory factory;

	@Inject
	GroupDrawStrategy(DrawingBowlFactory factory)
	{
		this.factory = factory;
	}

	public List<LinkedHashSet<ITeam>> draw(int numberOfBuckets, LinkedHashSet<ITeam> teams)
	{
		checkTeams(teams);
		List<LinkedHashSet<ITeam>> teamBuckets = createBuckets(numberOfBuckets);
		List<DrawingBowl> bowls = createBowls(teams);
		Iterator<LinkedHashSet<ITeam>> iterator = Iterators.cycle(teamBuckets);
		for (DrawingBowl bowl : bowls) {
			while (!bowl.isEmpty()) {
				LinkedHashSet<ITeam> bucket = iterator.next();
				bucket.add(bowl.draw());
			}
		}
		return teamBuckets;
	}

	private List<LinkedHashSet<ITeam>> createBuckets(int numberOfBuckets)
	{
		List<LinkedHashSet<ITeam>> buckets = Lists.newArrayList();
		for (int i = 0; i < numberOfBuckets; i++) {
			buckets.add(Sets.newLinkedHashSet());
		}
		return buckets;
	}

	private List<DrawingBowl> createBowls(LinkedHashSet<ITeam> teams)
	{
		LinkedHashSet<ITeam> setTeams = Sets.newLinkedHashSet();
		LinkedHashSet<ITeam> otherTeams = Sets.newLinkedHashSet();
		List<DrawingBowl> bowls = Lists.newArrayList();
		for (ITeam team : teams) {
			if (team.isSetTeam()) {
				setTeams.add(team);
			} else {
				otherTeams.add(team);
			}
		}
		bowls.add(factory.create(setTeams));
		bowls.add(factory.create(otherTeams));
		return bowls;
	}

	private void checkTeams(LinkedHashSet<ITeam> teams)
	{
		checkNotNull(teams);
		checkArgument(teams.size() > 1, "There must be at least two teams to draw from.");
	}
}
