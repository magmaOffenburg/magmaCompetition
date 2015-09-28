package magma.tools.competition.monitorproxy;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Implementation of a monitor observer. Contains the socket connection.
 * 
 * @author Simon Gutjahr
 * 
 */
public class MonitorObserver
{
	/** Socket to the observer */
	private Socket _socket;

	private MonitorConnection _monitorConnection;

	/** Outgoing data stream */
	private DataOutputStream _outputStream;

	/** Incoming data stream */
	private InputStream _inputStream;

	/** Indicator if a connection is present */
	private boolean _isConnected;

	private ClientMessageForwarder _clientMessageForwarder;

	/**
	 * Constructor
	 * @param socket The socket for the specific observer
	 * @throws IOException
	 */
	public MonitorObserver(Socket clientSocket) throws IOException
	{
		this(clientSocket, null);
	}

	/**
	 * Constructor
	 * @param socket The socket for the specific observer
	 * @throws IOException
	 */
	public MonitorObserver(Socket clientSocket,
			MonitorConnection monitorConnection) throws IOException
	{
		if (clientSocket != null) {
			_socket = clientSocket;
			_inputStream = new BufferedInputStream(_socket.getInputStream());
			_outputStream = new DataOutputStream(_socket.getOutputStream());
			_isConnected = true;
			_clientMessageForwarder = null;
			_monitorConnection = monitorConnection;
		}

		if (_monitorConnection != null) {
			_clientMessageForwarder = new ClientMessageForwarder();
			_clientMessageForwarder.start();
		}
	}

	/**
	 * Stop monitor observer.
	 */
	public void close()
	{
		boolean success = false;

		if (_socket != null) {
			try {
				_socket.close();
				success = true;
				_isConnected = false;
				if (_clientMessageForwarder != null) {
					_clientMessageForwarder.interrupt();
				}
			} catch (IOException e) {
			}
		}

		if (success == true) {
			System.out.println("Closed monitor observer " + _socket);
		}
	}

	/**
	 * Send a message to the monitor observer.
	 * 
	 * @param msg - the message to send
	 * @throws IOException
	 */
	public void sendClientMsg(byte[] msg)
	{
		if (_isConnected == true) {
			int len = msg.length;

			int byte0 = (len >> 24) & 0xFF;
			int byte1 = (len >> 16) & 0xFF;
			int byte2 = (len >> 8) & 0xFF;
			int byte3 = len & 0xFF;

			try {
				_outputStream.writeByte((byte) byte0);
				_outputStream.writeByte((byte) byte1);
				_outputStream.writeByte((byte) byte2);
				_outputStream.writeByte((byte) byte3);
				_outputStream.write(msg);
				_outputStream.flush();
			} catch (IOException e) {
				_isConnected = false;
			}
		}
	}

	/**
	 * Return if the monitor observer is connected
	 * 
	 * @return connection state
	 */
	public boolean isConnected()
	{
		return _isConnected;
	}

	/**
	 * Receive a message from the client agent. Blocking call.
	 * 
	 * @return the next, complete message received from the client agent
	 */
	private byte[] receiveClientMsg()
	{
		byte[] result = null;
		int length = 0;

		if (_isConnected == true) {
			try {
				int byte0 = _inputStream.read();
				int byte1 = _inputStream.read();
				int byte2 = _inputStream.read();
				int byte3 = _inputStream.read();

				length = byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
				int total = 0;

				if (length < 0) {
					length = -1;
				} else {
					result = new byte[length];
					while (total < length) {
						total += _inputStream.read(result, total, length - total);
					}
				}
			} catch (IOException e) {
				length = -1;
			} catch (IndexOutOfBoundsException e) {

			}
		}

		/* Check for receive error */
		if (length < 0) {
			_isConnected = false;
			result = null;
		}

		return result;
	}

	/**
	 * 
	 * 
	 */
	private class ClientMessageForwarder extends Thread
	{
		@Override
		public void run()
		{
			while (this.isInterrupted() == false) {
				/* receive next client action message */
				byte[] msg = receiveClientMsg();

				if (msg == null) {
					/* shutdown when receiving null-message */
					_isConnected = false;
				} else {
					_monitorConnection.sendMessage(msg);
				}
			}
		}
	}
}
