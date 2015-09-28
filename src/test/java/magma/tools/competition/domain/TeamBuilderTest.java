package magma.tools.competition.domain;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.TeamBuilder;
import magma.tools.competition.domain.TeamFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TeamBuilderTest
{

	@Mock
	private TeamFactory factory;

	@Mock
	private ITeam team;

	@InjectMocks
	private TeamBuilder builder;

	@Test
	public void testBuild() throws Exception
	{
		builder.name("name").setTeam(true).username("username")
				.startScriptFilename("script").pathToScriptFile("path");
		when(factory.create("name", true, "username", "script", "path"))
				.thenReturn(team);
		assertSame(team, builder.build());
	}

}
