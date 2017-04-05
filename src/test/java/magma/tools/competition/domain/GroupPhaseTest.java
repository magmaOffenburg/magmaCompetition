package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.TeamFactory;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class GroupPhaseTest
{
	@Mock
	private TeamFactory factory;

	@Mock
	private List<ITeam> teams;

	private List<Group> groupList;

	private LinkedHashSet<Group> groupSet;

	@Before
	public void setUp() throws Exception
	{
		when(teams.size()).thenReturn(4);
		groupList = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			Group group = mock(Group.class);
			when(group.getTeams()).thenReturn(teams);
			groupList.add(group);
		}
		groupSet = Sets.newLinkedHashSet(groupList);
	}

	@Test
	public void testConstructor() throws Exception
	{
		GroupPhase phase = new GroupPhase(factory, "name", groupSet, 2);
		assertEquals("name", phase.getName());
		assertSame(groupList.get(0), phase.getGroups().get(0));
		assertEquals(2, phase.getNumberOfQualifyingTeams());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNameNull() throws Exception
	{
		new GroupPhase(factory, null, groupSet, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNameInvalid() throws Exception
	{
		new GroupPhase(factory, " ", groupSet, 2);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorGroupsNull() throws Exception
	{
		new GroupPhase(factory, "name", null, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorGroupsInvalid() throws Exception
	{
		groupSet.clear();
		new GroupPhase(factory, "name", groupSet, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNumberOfQualifyingTeamsInvalid() throws Exception
	{
		new GroupPhase(factory, "name", groupSet, 0);
	}

	@Test
	public void testGetQualifyingTeams() throws Exception
	{
		GroupPhase phase = new GroupPhase(factory, "name", groupSet, 1);
		List<ITeam> proxies = Lists.newArrayList();
		for (int i = 0; i < 4; i++) {
			proxies.add(mock(ITeam.class));
		}
		when(factory.createProxy(groupList.get(0), 1)).thenReturn(proxies.get(0));
		when(factory.createProxy(groupList.get(1), 1)).thenReturn(proxies.get(1));
		when(factory.createProxy(groupList.get(2), 1)).thenReturn(proxies.get(2));
		when(factory.createProxy(groupList.get(3), 1)).thenReturn(proxies.get(3));
		List<ITeam> qualifyingTeams = phase.getQualifyingTeams();
		for (int i = 0; i < 4; i++) {
			assertSame(proxies.get(i), qualifyingTeams.get(i));
		}
	}

	@Test
	public void testGetRetiringTeams() throws Exception
	{
		GroupPhase phase = new GroupPhase(factory, "name", groupSet, 2);
		List<ITeam> proxies = Lists.newArrayList();
		for (int i = 0; i < 8; i++) {
			proxies.add(mock(ITeam.class));
		}
		when(factory.createProxy(groupList.get(0), 2)).thenReturn(proxies.get(0));
		when(factory.createProxy(groupList.get(0), 3)).thenReturn(proxies.get(1));
		when(factory.createProxy(groupList.get(1), 2)).thenReturn(proxies.get(2));
		when(factory.createProxy(groupList.get(1), 3)).thenReturn(proxies.get(3));
		when(factory.createProxy(groupList.get(2), 2)).thenReturn(proxies.get(4));
		when(factory.createProxy(groupList.get(2), 3)).thenReturn(proxies.get(5));
		when(factory.createProxy(groupList.get(3), 2)).thenReturn(proxies.get(6));
		when(factory.createProxy(groupList.get(3), 3)).thenReturn(proxies.get(7));
		List<ITeam> retiringTeams = phase.getRetiringTeams();
		for (int i = 0; i < 8; i++) {
			assertSame(proxies.get(i), retiringTeams.get(i));
		}
	}

	@Test
	public void testGetNumberOfRetiringTeams() throws Exception
	{
		GroupPhase phase = new GroupPhase(factory, "name", groupSet, 2);
		assertEquals(8, phase.getNumberOfRetiringTeams());
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(GroupPhase.class).usingGetClass().verify();
	}
}
