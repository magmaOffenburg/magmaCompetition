package magma.tools.competition.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

class Team implements ITeam
{
	private static final long serialVersionUID = 5823165442040356841L;

	private final String name;

	private boolean setTeam;

	private String username;

	private String startScriptFileName;

	private String pathToScriptFile;

	@Inject
	@JsonCreator
	Team(@Assisted("name") @JsonProperty("name") String name,
			@Assisted("setTeam") @JsonProperty("setTeam") boolean setTeam,
			@Assisted("username") @JsonProperty("username") String username,
			@Assisted("startScriptFileName") @JsonProperty("startScriptFileName") String startScriptFileName,
			@Assisted("pathToScriptFile") @JsonProperty("pathToScriptFile") String pathToScriptFile)
	{
		super();
		checkName(name);
		checkUsername(username);
		checkStartScriptFileName(startScriptFileName);
		checkPathToScriptFile(pathToScriptFile);
		this.name = name;
		this.setTeam = setTeam;
		this.username = username;
		this.startScriptFileName = startScriptFileName;
		this.pathToScriptFile = pathToScriptFile;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isSetTeam()
	{
		return setTeam;
	}

	@Override
	public void setTeam(boolean isSetTeam) throws ProxyNotResolvableException
	{
		this.setTeam = isSetTeam;
	}

	@Override
	public String getUsername()
	{
		return username;
	}

	@Override
	public void setUsername(String username) throws ProxyNotResolvableException
	{
		checkUsername(username);
		this.username = username;
	}

	@Override
	public String getStartScriptFileName()
	{
		return startScriptFileName;
	}

	@Override
	public void setStartScriptFileName(String startScriptFileName) throws ProxyNotResolvableException
	{
		checkStartScriptFileName(startScriptFileName);
		this.startScriptFileName = startScriptFileName;
	}

	@Override
	public String getPathToScriptFile()
	{
		return pathToScriptFile;
	}

	@Override
	public void setPathToScriptFile(String pathToScriptFile) throws ProxyNotResolvableException
	{
		checkPathToScriptFile(pathToScriptFile);
		this.pathToScriptFile = pathToScriptFile;
	}

	private void checkName(String name)
	{
		check(name, "A team's name must not be empty.");
	}

	private void checkUsername(String username)
	{
		check(username, "The username must not be empty.");
	}

	private void checkStartScriptFileName(String startScriptFileName)
	{
		check(startScriptFileName, "The startscript filename must not be empty.");
	}

	private void checkPathToScriptFile(String pathToScriptFile)
	{
		check(pathToScriptFile, "The path to the script file was empty.");
	}

	private void check(String name, String message)
	{
		checkNotNull(name);
		checkArgument(name.trim().length() > 0, message);
	}

	@Override
	public String toString()
	{
		return String.format("{Teamname: %s, Username: %s, Startscript Filename: %s, Path to Script: %s, Set Team: %s}",
				name, username, startScriptFileName, pathToScriptFile, setTeam);
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder().append(name).toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Team other = (Team) obj;
		return new EqualsBuilder().append(name, other.name).isEquals();
	}
}
