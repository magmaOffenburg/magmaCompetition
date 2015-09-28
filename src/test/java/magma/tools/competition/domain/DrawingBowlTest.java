package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Random;

import magma.tools.competition.domain.DrawingBowl;
import magma.tools.competition.domain.ITeam;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class DrawingBowlTest
{

	@Mock
	private Random random;

	private List<ITeam> teams;

	private DrawingBowl bowl;

	@Before
	public void setUp() throws Exception
	{
		teams = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			teams.add(mock(ITeam.class));
		}
		bowl = new DrawingBowl(random, Sets.newLinkedHashSet(teams));
	}

	@Test
	public void testIsEmpty() throws Exception
	{
		assertEquals(false, bowl.isEmpty());
		for (int i = 0; i < 10; i++) {
			bowl.draw();
		}
		assertEquals(true, bowl.isEmpty());
	}

	@Test
	public void testDraw() throws Exception
	{
		when(random.nextInt()).thenReturn(0);
		assertSame(teams.get(0), bowl.draw());
		when(random.nextInt()).thenReturn(0);
		assertSame(teams.get(1), bowl.draw());
	}

	@Test(expected = IllegalStateException.class)
	public void testDrawBowlEmpty() throws Exception
	{
		for (int i = 0; i < 10; i++) {
			bowl.draw();
		}
		bowl.draw();
	}

}
