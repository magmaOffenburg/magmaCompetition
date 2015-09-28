package magma.tools.competition.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = Team.class, name = "team"),
		@Type(value = QualifiedTeamProxy.class, name = "qualifiedTeam"),
		@Type(value = GameWinnerProxy.class, name = "winner"),
		@Type(value = GameLooserProxy.class, name = "looser") })
public interface ITeam extends Serializable
{

	String getName();

	boolean isSetTeam();

	void setTeam(boolean isSetTeam) throws ProxyNotResolvableException;

	String getUsername();

	void setUsername(String username) throws ProxyNotResolvableException;

	String getStartScriptFileName();

	void setStartScriptFileName(String startScriptFileName)
			throws ProxyNotResolvableException;

	String getPathToScriptFile();

	void setPathToScriptFile(String pathToScriptFile)
			throws ProxyNotResolvableException;

}
