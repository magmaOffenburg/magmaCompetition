package magma.tools.competition.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({ "username", "setTeam", "pathToScriptFile",
		"startScriptFileName" })
public abstract class TeamProxy implements ITeam
{

	private static final long serialVersionUID = -3293520587522505461L;

	TeamProxy()
	{
	}

	@Override
	@JsonProperty
	public String getName()
	{
		ITeam team = resolve();
		if (team != null) {
			return team.getName();
		} else {
			return getProxiedName();
		}
	}

	@JsonIgnore
	protected void setName(String name)
	{
		/*
		 * TODO remove this, when
		 * https://github.com/FasterXML/jackson-databind/issues/95 is solved. also
		 * see setName(String name) in TeamProxy!
		 */
	}

	@Override
	public boolean isSetTeam()
	{
		ITeam team = resolve();
		if (team != null) {
			return team.isSetTeam();
		} else {
			return false;
		}
	}

	@Override
	public void setTeam(boolean isSetTeam) throws ProxyNotResolvableException
	{
		ITeam team = resolveOrFail();
		team.setTeam(isSetTeam);
	}

	@Override
	public String getUsername()
	{
		ITeam team = resolve();
		if (team != null) {
			return team.getUsername();
		} else {
			return "-";
		}
	}

	@Override
	public void setUsername(String username) throws ProxyNotResolvableException
	{
		ITeam team = resolveOrFail();
		team.setUsername(username);
	}

	@Override
	public String getStartScriptFileName()
	{
		ITeam team = resolve();
		if (team != null) {
			return team.getStartScriptFileName();
		} else {
			return "-";
		}
	}

	@Override
	public void setStartScriptFileName(String startScriptFileName)
			throws ProxyNotResolvableException
	{
		ITeam team = resolveOrFail();
		team.setStartScriptFileName(startScriptFileName);
	}

	@Override
	public String getPathToScriptFile()
	{
		ITeam team = resolve();
		if (team != null) {
			return team.getPathToScriptFile();
		} else {
			return "-";
		}
	}

	@Override
	public void setPathToScriptFile(String pathToScriptFile)
			throws ProxyNotResolvableException
	{
		ITeam team = resolveOrFail();
		team.setPathToScriptFile(pathToScriptFile);
	}

	private ITeam resolveOrFail() throws ProxyNotResolvableException
	{
		ITeam team = resolve();
		if (team == null) {
			throw new ProxyNotResolvableException();
		}
		return team;
	}

	protected abstract ITeam resolve();

	protected abstract String getProxiedName();

}
