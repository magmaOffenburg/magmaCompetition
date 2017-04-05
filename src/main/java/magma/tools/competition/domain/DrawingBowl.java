package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkState;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class DrawingBowl
{
	private final Random random;

	private final List<ITeam> items;

	@Inject
	DrawingBowl(Random random, @Assisted("items") LinkedHashSet<ITeam> items)
	{
		this.random = random;
		this.items = Lists.newArrayList(items);
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	public ITeam draw()
	{
		checkState(items.size() > 0, "Bowl is empty.");
		int randomIndex = (int) Math.floor(random.nextDouble() * items.size());
		return items.remove(randomIndex);
	}
}
