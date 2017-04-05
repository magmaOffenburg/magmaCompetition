package magma.tools.competition.monitorproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of the MonitorProxyServer. Handles the incoming observer
 * connections.
 *
 * Extends {@link Thread}
 *
 * @author Simon Gutjahr
 *
 */
public class MonitorProxyServer extends Thread
{
	/** The server socket */
	private ServerSocket _monitorProxySocket;

	/** The server port */
	private int _monitorProxyPort;

	/** List of monitor observers */
	private CopyOnWriteArrayList<MonitorObserver> _monitorObservers;

	private MonitorConnection _monitorConnection;

	/**
	 * Custom Constructor
	 * @param port Port for the observer connections
	 */
	public MonitorProxyServer(int port)
	{
		_monitorProxySocket = null;
		_monitorProxyPort = port;
		_monitorObservers = new CopyOnWriteArrayList<MonitorObserver>();
		_monitorConnection = null;
	}

	/**
	 * Default constructor
	 */
	public MonitorProxyServer()
	{
		this(1337);
	}

	/**
	 * Run method of the MonitorProxyServer thread. Waits for incoming observer
	 * connections.
	 */
	@Override
	public void run()
	{
		try {
			_monitorProxySocket = new ServerSocket(_monitorProxyPort);

			System.out.println("Proxy server listening on port: " + _monitorProxyPort);

			while (this.isInterrupted() != true) {
				Socket clientSocket = _monitorProxySocket.accept();

				try {
					MonitorObserver monitorObserver;
					if (_monitorObservers.size() == 0) {
						monitorObserver = new MonitorObserver(clientSocket, _monitorConnection);
					} else {
						monitorObserver = new MonitorObserver(clientSocket);
					}

					_monitorObservers.add(monitorObserver);
				} catch (IOException e) {
				}
			}
		} catch (IOException e) {
			System.out.println("Proxy server socket closed!");
		}

		_monitorProxySocket = null;

		for (MonitorObserver observer : _monitorObservers) {
			observer.close();
		}

		_monitorObservers.clear();
	}

	/**
	 * Shutdown proxy server and all active agent-proxy instances.
	 * @throws IOException
	 */
	public void shutdown() throws IOException
	{
		if ((_monitorProxySocket != null) && (!_monitorProxySocket.isClosed())) {
			_monitorProxySocket.close();
		}
	}

	/**
	 * Retrieve the current list of monitor observers.
	 *
	 * @return current list of monitor observers.
	 */
	public List<MonitorObserver> getMonitorObservers()
	{
		for (MonitorObserver observer : _monitorObservers) {
			if (observer.isConnected() == false) {
				_monitorObservers.remove(observer);
			}
		}

		return (List<MonitorObserver>) Collections.unmodifiableList(_monitorObservers);
	}

	public void setMonitorConnection(MonitorConnection monitorConnection)
	{
		if (monitorConnection != null) {
			_monitorConnection = monitorConnection;
		}
	}
}
