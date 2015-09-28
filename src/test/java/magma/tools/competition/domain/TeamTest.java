package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import magma.tools.competition.domain.Team;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TeamTest
{

	private Team team;

	@Before
	public void setUp()
	{
		team = new Team("name", true, "username", "startScript", "path");
	}

	@Test
	public void testConstructor() throws Exception
	{
		assertEquals("name", team.getName());
		assertEquals(true, team.isSetTeam());
		assertEquals("username", team.getUsername());
		assertEquals("startScript", team.getStartScriptFileName());
		assertEquals("path", team.getPathToScriptFile());
	}

	@Test
	public void testSetTeam() throws Exception
	{
		team.setTeam(true);
		assertEquals(true, team.isSetTeam());
		team.setTeam(false);
		assertEquals(false, team.isSetTeam());
	}

	@Test
	public void testSetUsername() throws Exception
	{
		team.setUsername("new-username");
		assertEquals("new-username", team.getUsername());
	}

	@Test
	public void testSetStartScriptFileName() throws Exception
	{
		team.setStartScriptFileName("new-filename");
		assertEquals("new-filename", team.getStartScriptFileName());
	}

	@Test
	public void testSetPathToScriptFile() throws Exception
	{
		team.setPathToScriptFile("new-path");
		assertEquals("new-path", team.getPathToScriptFile());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNameNull() throws Exception
	{
		new Team(null, true, "username", "startScript", "path");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNameInvalid() throws Exception
	{
		new Team(" ", true, "username", "startScript", "path");
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorUsernameNull() throws Exception
	{
		new Team("name", true, null, "startScript", "path");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorUsernameInvalid() throws Exception
	{
		new Team("name", true, " ", "startScript", "path");
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorScriptNull() throws Exception
	{
		new Team("name", true, "username", null, "path");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorScriptInvalid() throws Exception
	{
		new Team("name", true, "username", " ", "path");
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorPathNull() throws Exception
	{
		new Team("name", true, "username", "startScript", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorPathInvalid() throws Exception
	{
		new Team("name", true, "username", "startScript", " ");
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(Team.class).usingGetClass().verify();
	}

}
