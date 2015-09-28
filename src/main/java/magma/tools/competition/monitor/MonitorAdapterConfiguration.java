package magma.tools.competition.monitor;

import java.util.logging.Level;

import magma.util.connection.IServerConnection;

public class MonitorAdapterConfiguration
{

	private String host = "127.0.0.1";

	private int port = IServerConnection.MONITOR_PORT;

	private Level level = Level.OFF;

	private int refereeID = 0;

	private String serverPid = null;

	private int agentPort = IServerConnection.SERVER_PORT;

	private String team1Name = "";

	private String team1Jar = "";

	private String team2Name = "";

	private String team2Jar = "";

	private int playersPerTeam = 11;

	private float dropHeight = 0.65f;

	private int waitForConnectionTime = 1500;

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public Level getLevel()
	{
		return level;
	}

	public void setLevel(Level level)
	{
		this.level = level;
	}

	public int getRefereeID()
	{
		return refereeID;
	}

	public void setRefereeID(int refereeID)
	{
		this.refereeID = refereeID;
	}

	public String getServerPid()
	{
		return serverPid;
	}

	public void setServerPid(String serverPid)
	{
		this.serverPid = serverPid;
	}

	public int getAgentPort()
	{
		return agentPort;
	}

	public void setAgentPort(int agentPort)
	{
		this.agentPort = agentPort;
	}

	public String getTeam1Name()
	{
		return team1Name;
	}

	public void setTeam1Name(String team1Name)
	{
		this.team1Name = team1Name;
	}

	public String getTeam1Jar()
	{
		return team1Jar;
	}

	public void setTeam1Jar(String team1Jar)
	{
		this.team1Jar = team1Jar;
	}

	public String getTeam2Name()
	{
		return team2Name;
	}

	public void setTeam2Name(String team2Name)
	{
		this.team2Name = team2Name;
	}

	public String getTeam2Jar()
	{
		return team2Jar;
	}

	public void setTeam2Jar(String team2Jar)
	{
		this.team2Jar = team2Jar;
	}

	public int getPlayersPerTeam()
	{
		return playersPerTeam;
	}

	public void setPlayersPerTeam(int playersPerTeam)
	{
		this.playersPerTeam = playersPerTeam;
	}

	public float getDropHeight()
	{
		return dropHeight;
	}

	public void setDropHeight(float dropHeight)
	{
		this.dropHeight = dropHeight;
	}

	public int getWaitForConnectionTime()
	{
		return waitForConnectionTime;
	}

	public void setWaitForConnectionTime(int waitForConnectionTime)
	{
		this.waitForConnectionTime = waitForConnectionTime;
	}

}
