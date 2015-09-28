package magma.tools.competition.domain;

import com.google.inject.Inject;

public class TeamBuilder
{

	private TeamFactory factory;

	private String name;

	private boolean setTeam;

	private String username;

	private String startScriptFileName;

	private String pathToScriptFile;

	@Inject
	public TeamBuilder(TeamFactory factory)
	{
		this.factory = factory;
	}

	public TeamBuilder name(String name)
	{
		this.name = name;
		return this;
	}

	public TeamBuilder setTeam(boolean setTeam)
	{
		this.setTeam = setTeam;
		return this;
	}

	public TeamBuilder username(String username)
	{
		this.username = username;
		return this;
	}

	public TeamBuilder startScriptFilename(String startScriptFileName)
	{
		this.startScriptFileName = startScriptFileName;
		return this;
	}

	public TeamBuilder pathToScriptFile(String pathToScriptFile)
	{
		this.pathToScriptFile = pathToScriptFile;
		return this;
	}

	public ITeam build()
	{
		return factory.create(name, setTeam, username, startScriptFileName,
				pathToScriptFile);
	}

}
