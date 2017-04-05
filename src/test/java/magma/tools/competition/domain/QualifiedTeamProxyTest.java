package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupResult;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.ProxyNotResolvableException;
import magma.tools.competition.domain.QualifiedTeamProxy;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class QualifiedTeamProxyTest
{
	@Mock
	private Group group;

	@Mock
	private List<ITeam> teams;

	@Mock
	private GroupResult result;

	@Mock
	private ITeam team;

	private QualifiedTeamProxy proxy;

	@Before
	public void setUp() throws Exception
	{
		when(group.getResult()).thenReturn(result);
		when(group.getTeams()).thenReturn(teams);
		when(group.getName()).thenReturn("groupname");
		when(team.getName()).thenReturn("teamname");
		when(team.isSetTeam()).thenReturn(true);
		when(team.getUsername()).thenReturn("username");
		when(team.getPathToScriptFile()).thenReturn("path");
		when(team.getStartScriptFileName()).thenReturn("startscript");
		when(teams.size()).thenReturn(4);
		when(result.isFinal()).thenReturn(true);
		when(result.getTeamsOnRank(1)).thenReturn(Lists.asList(team, new ITeam[] {}));
		proxy = new QualifiedTeamProxy(group, 1);
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertEquals("teamname", proxy.getName());
		verify(result).getTeamsOnRank(1);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorGroupNull() throws Exception
	{
		new QualifiedTeamProxy(null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRankNegative() throws Exception
	{
		new QualifiedTeamProxy(group, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRankZero() throws Exception
	{
		new QualifiedTeamProxy(group, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRankImpossible() throws Exception
	{
		new QualifiedTeamProxy(group, 5);
	}

	@Test
	public void testGetName() throws Exception
	{
		assertEquals("teamname", proxy.getName());
	}

	@Test
	public void testIsSetTeam() throws Exception
	{
		assertEquals(true, proxy.isSetTeam());
	}

	@Test
	public void testIsSetTeamProxied() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		assertEquals(false, proxy.isSetTeam());
	}

	@Test
	public void testGetNameProxiedFirst() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		assertEquals("1st Group groupname", proxy.getName());
	}

	@Test
	public void testGetNameProxiedSecond() throws Exception
	{
		proxy = new QualifiedTeamProxy(group, 2);
		when(result.isFinal()).thenReturn(false);
		assertEquals("2nd Group groupname", proxy.getName());
	}

	@Test
	public void testGetNameProxiedThird() throws Exception
	{
		proxy = new QualifiedTeamProxy(group, 3);
		when(result.isFinal()).thenReturn(false);
		assertEquals("3rd Group groupname", proxy.getName());
	}

	@Test
	public void testGetNameProxiedFourth() throws Exception
	{
		proxy = new QualifiedTeamProxy(group, 4);
		when(result.isFinal()).thenReturn(false);
		assertEquals("4th Group groupname", proxy.getName());
	}

	@Test
	public void testGetUsername() throws Exception
	{
		assertEquals("username", proxy.getUsername());
	}

	@Test
	public void testGetUsernameProxied() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		assertEquals("-", proxy.getUsername());
	}

	@Test
	public void testGetStartScriptFileName() throws Exception
	{
		assertEquals("startscript", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetStartScriptFileNameProxied() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		assertEquals("-", proxy.getStartScriptFileName());
	}

	@Test
	public void testGetPathToScriptFileName() throws Exception
	{
		assertEquals("path", proxy.getPathToScriptFile());
	}

	@Test
	public void testGetPathToScriptFileNameProxied() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		assertEquals("-", proxy.getPathToScriptFile());
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(QualifiedTeamProxy.class).usingGetClass().verify();
	}

	@Test
	public void testSetUsername() throws Exception
	{
		proxy.setUsername("new-name");
		verify(team).setUsername("new-name");
	}

	@Test(expected = ProxyNotResolvableException.class)
	public void testSetUsernameNotResolvable() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		proxy.setUsername("new-name");
	}

	@Test
	public void testSetTeam() throws Exception
	{
		proxy.setTeam(true);
		verify(team).setTeam(true);
	}

	@Test(expected = ProxyNotResolvableException.class)
	public void testSetTeamNotResolvable() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		proxy.setTeam(true);
	}

	@Test
	public void testSetStartScriptFileName() throws Exception
	{
		proxy.setStartScriptFileName("new-filename");
		verify(team).setStartScriptFileName("new-filename");
	}

	@Test(expected = ProxyNotResolvableException.class)
	public void testSetStartScriptFileNameNotResolvable() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		proxy.setStartScriptFileName("new-filename");
	}

	@Test
	public void testSetPathToScriptFile() throws Exception
	{
		proxy.setPathToScriptFile("new-path");
		verify(team).setPathToScriptFile("new-path");
	}

	@Test(expected = ProxyNotResolvableException.class)
	public void testSetPathToScriptFileNotResolvable() throws Exception
	{
		when(result.isFinal()).thenReturn(false);
		proxy.setPathToScriptFile("new-path");
	}
}
