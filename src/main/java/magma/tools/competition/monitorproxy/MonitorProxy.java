package magma.tools.competition.monitorproxy;

import java.io.IOException;
import java.util.Iterator;

/**
 * Implementation of a generic proxy for the sp monitor.
 * 
 * Extends {@link Thread}
 * 
 * @author Simon Gutjahr
 * 
 */
public class MonitorProxy extends Thread
{
	/** TCP server for the observer */
	private MonitorProxyServer _monitorProxyServer;

	/** TCP client to connect to the monitor */
	private MonitorConnection _monitorConnection;

	/**
	 * Constructor for the MonitorProxy
	 * 
	 * @param monitorProxyServer TCP Server for clients to connect
	 * @param monitorClient TCP Client to connect to the monitor
	 * @throws MonitorProxyException
	 */
	public MonitorProxy(MonitorProxyServer monitorProxyServer,
			MonitorConnection monitorClient) throws MonitorProxyException
	{
		if (monitorProxyServer != null && monitorClient != null) {
			_monitorProxyServer = monitorProxyServer;
			_monitorConnection = monitorClient;
		} else {
			/* No valid server and client, throw a exception */
			throw new MonitorProxyException("Invalid input parameter");
		}
	}

	/**
	 * Run method of the Monitor proxy. Handles the forwarding of incoming
	 * messages.
	 */
	@Override
	public void run()
	{
		byte recv[] = null;

		try {
			/* Connect to the monitor tcp server */
			_monitorConnection.connect();

			/* Check is connection was successful */
			if (_monitorConnection.isConnected()) {
				/*
				 * Connection to monitor server was successful, start the monitor
				 * proxy server
				 */
				_monitorProxyServer.start();
				_monitorProxyServer.setMonitorConnection(_monitorConnection);

				while ((this.isInterrupted() != true)
						&& (_monitorProxyServer.isAlive() == true)
						&& (_monitorConnection.isConnected() == true)) {

					/* Wait for a new message */
					recv = _monitorConnection.receiveMessage();

					/* Send message to all connected clients */
					if (recv != null) {
						if (recv.length > 0) {
							for (Iterator<MonitorObserver> iterator = _monitorProxyServer
									.getMonitorObservers().iterator(); iterator
									.hasNext();) {
								MonitorObserver observer = iterator.next();
								observer.sendClientMsg(recv);
							}
						}
						recv = null;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Exit Monitor Proxy");

		/* Disconnect from monitor server */
		_monitorConnection.disconnect();

		/* Shut down all connected clients */
		try {
			_monitorProxyServer.shutdown();
		} catch (IOException e) {
		}
	}
}
