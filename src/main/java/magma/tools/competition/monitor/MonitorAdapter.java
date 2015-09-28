package magma.tools.competition.monitor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import magma.monitor.general.IMonitorRuntimeListener;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.general.impl.MonitorParameter;
import magma.monitor.general.impl.MonitorRuntime;
import magma.monitor.worldmodel.ISoccerAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorAdapter implements IMonitorRuntimeListener
{

	private static Logger logger = LoggerFactory.getLogger(MonitorAdapter.class);

	private MonitorAdapterConfiguration configuration;

	private MonitorRuntime runtime;

	private MonitorThread thread;

	private List<IMonitorListener> listeners;

	public MonitorAdapter(MonitorAdapterConfiguration configuration)
	{
		this.configuration = configuration;
		listeners = new LinkedList<>();
		MonitorComponentFactory factory = createComponentFactory(configuration);
		MonitorParameter parameters = createParameters(configuration, factory);
		runtime = new MonitorRuntime(parameters);
		runtime.addRuntimeListener(this);
	}

	public void startMonitor() throws MonitorException
	{
		try {
			checkState(thread == null, "Monitor is already running.");
			logger.info("Starting rcssserver3d-Monitor.");
			thread = new MonitorThread();
			thread.start();
			thread.join(configuration.getWaitForConnectionTime());
			if (!runtime.isConnected()) {
				Exception exception = thread.getException();
				throw new MonitorException(exception.getMessage());
			}
		} catch (InterruptedException e) {
		}
	}

	public void stopMonitor() throws MonitorException
	{
		try {
			checkState(thread != null, "Monitor is not running.");
			logger.info("Stopping rcssserver3d-Monitor.");
			runtime.stopMonitor();
			thread.join();
			thread = null;
		} catch (InterruptedException e) {
		}
	}

	public void kickOff() throws MonitorAdapterNotConnectedException
	{
		assertIsConnected();
		runtime.getServerCommander().kickOff();
	}

	public int getLeftTeamScore() throws MonitorAdapterNotConnectedException
	{
		assertIsConnected();
		return runtime.getWorldModel().getScoreLeft();
	}

	public int getRightTeamScore() throws MonitorAdapterNotConnectedException
	{
		assertIsConnected();
		return runtime.getWorldModel().getScoreRight();
	}

	public void setScore(int left, int right)
			throws MonitorAdapterNotConnectedException
	{
		assertIsConnected();
		runtime.getServerCommander().setScore(left, right);
	}

	public int getNumberOfPlayers(String teamName)
			throws MonitorAdapterNotConnectedException
	{
		assertIsConnected();
		int playersCounter = 0;
		ArrayList<? extends ISoccerAgent> allPlayers = runtime.getWorldModel()
				.getSoccerAgents();

		for (ISoccerAgent aPlayer : allPlayers) {
			if (aPlayer.getTeamName().equals(teamName)) {
				playersCounter++;
			}
		}
		return playersCounter;
	}

	public float getTime() throws MonitorAdapterNotConnectedException
	{
		assertIsConnected();
		return this.runtime.getWorldModel().getTime();
	}

	public boolean isConnected()
	{
		if (thread != null && runtime.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public void addMonitorListener(IMonitorListener listener)
	{
		checkNotNull(listener);
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeMonitorListener(IMonitorListener listener)
	{
		checkNotNull(listener);
		listeners.add(listener);
	}

	@Override
	public void monitorUpdated()
	{
		for (IMonitorListener listener : listeners) {
			listener.onUpdate(this);
		}
	}

	private void assertIsConnected() throws MonitorAdapterNotConnectedException
	{
		if (thread == null || !runtime.isConnected()) {
			throw new MonitorAdapterNotConnectedException();
		}
	}

	private MonitorParameter createParameters(
			MonitorAdapterConfiguration configuration,
			MonitorComponentFactory factory)
	{
		return new MonitorParameter(configuration.getHost(),
				configuration.getPort(), configuration.getLevel(),
				configuration.getRefereeID(), factory);
	}

	private MonitorComponentFactory createComponentFactory(
			MonitorAdapterConfiguration configuration)
	{
		return new MonitorComponentFactory(new FactoryParameter(
				configuration.getServerPid(), configuration.getHost(),
				configuration.getAgentPort(), configuration.getTeam1Name(),
				configuration.getTeam1Jar(), configuration.getTeam2Name(),
				configuration.getTeam2Jar(), configuration.getPlayersPerTeam(),
				configuration.getDropHeight()));
	}

	private class MonitorThread extends Thread
	{

		private Exception exception;

		@Override
		public void run()
		{
			try {
				runtime.startMonitor();
			} catch (Exception e) {
				this.exception = e;
			}
		}

		public Exception getException()
		{
			return exception;
		}

	}

}
