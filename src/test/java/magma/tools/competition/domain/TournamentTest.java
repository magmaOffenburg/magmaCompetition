package magma.tools.competition.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TournamentTest
{

	private List<ITeam> teams;

	private List<Phase> phases;

	private LinkedHashSet<ITeam> teamSet;

	private LinkedHashSet<Phase> phaseSet;

	@Before
	public void setUp()
	{
		teams = new ArrayList<>();
		teams.add(mock(ITeam.class));
		teams.add(mock(ITeam.class));
		phases = new ArrayList<>();
		phases.add(mock(GroupPhase.class));
		phases.add(mock(GroupPhase.class));
		teamSet = new LinkedHashSet<>(teams);
		phaseSet = new LinkedHashSet<>(phases);
	}

	@Test
	public void testConstructor() throws Exception
	{
		Tournament tournament = new Tournament("name", 2, 360, teamSet, phaseSet);
		assertEquals("name", tournament.getName());
		assertEquals(2, tournament.getNumberOfClusters());
		assertEquals(360, tournament.getGameDuration(), 0.001);
		assertNotNull(tournament.getTeams());
		assertNotNull(tournament.getPhases());
	}

	@Test
	public void testGetTeams() throws Exception
	{
		Tournament tournament = new Tournament("name", 2, 360, teamSet, phaseSet);
		assertSame(tournament.getTeams().get(0), teams.get(0));
		assertSame(tournament.getTeams().get(1), teams.get(1));
	}

	@Test
	public void testGetPhases() throws Exception
	{
		Tournament tournament = new Tournament("name", 2, 360, teamSet, phaseSet);
		assertSame(tournament.getPhases().get(0), phases.get(0));
		assertSame(tournament.getPhases().get(1), phases.get(1));
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNameNull() throws Exception
	{
		new Tournament(null, 2, 360, teamSet, phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNameInvalid() throws Exception
	{
		new Tournament(" ", 2, 360, teamSet, phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNumberOfClustersNegative() throws Exception
	{
		new Tournament("name", -2, 360, teamSet, phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorNumberOfClustersZero() throws Exception
	{
		new Tournament("name", 0, 360, teamSet, phaseSet);
	}

	public void testConstructorNumberOfClustersOne() throws Exception
	{
		Tournament tournament = new Tournament("name", 1, 360, teamSet, phaseSet);
		assertEquals(2, tournament.getNumberOfClusters());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorGameDurationNegative() throws Exception
	{
		new Tournament("name", 2, -5, teamSet, phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorGameDurationZero() throws Exception
	{
		new Tournament("name", 2, 0, teamSet, phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTeamsEmpty() throws Exception
	{
		teams.clear();
		new Tournament("name", 2, 360, new LinkedHashSet<>(teams), phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorTeamsOne() throws Exception
	{
		teams.remove(0);
		new Tournament("name", 2, 360, new LinkedHashSet<>(teams), phaseSet);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructorPhasesEmpty() throws Exception
	{
		phases.clear();
		new Tournament("name", 2, 360, teamSet, new LinkedHashSet<>(phases));
	}

	@Test
	public void testEqualsContract() throws Exception
	{
		EqualsVerifier.forClass(Tournament.class).usingGetClass().verify();
	}

}
