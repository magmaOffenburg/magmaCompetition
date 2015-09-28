package magma.tools.competition.monitorproxy;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Implementation of the monitor client. Contains the socket for the connection
 * to the monitor.
 * 
 * @author Simon Gutjahr
 * 
 */
public class MonitorConnection
{
	/** Network socket */
	private Socket _socket;

	/** Outgoing data stream */
	private DataOutputStream _outputStream;

	/** Incoming data stream */
	private InputStream _inputStream;

	/** Indicator if a connection is present */
	private boolean _isConnected;

	/** the host to connect to */
	private String _hostIP;

	/** the port to connect to */
	private int _hostPort;

	/**
	 * Constructor
	 * @param host host address to connect to
	 * @param port port to connect to
	 */
	public MonitorConnection(String host, int port)
	{
		_hostIP = host;
		_hostPort = port;
		_socket = null;
	}

	/**
	 * Connect the MonitorProxy to the monitor tcp server
	 * @return true if the connection was successful, false if not
	 * @throws SocketException
	 * @throws IOException
	 */
	public boolean connect() throws SocketException, IOException
	{
		boolean bReturn = false;
		_socket = new Socket(_hostIP, _hostPort);

		if (_socket != null) {
			if (_socket.isConnected()) {
				System.out.println("Connected to monitor");
				_socket.setTcpNoDelay(true);

				_inputStream = new BufferedInputStream(_socket.getInputStream());
				_outputStream = new DataOutputStream(_socket.getOutputStream());

				_isConnected = true;
				bReturn = true;
			}
		}

		return bReturn;
	}

	/**
	 * @return true if this connection is connected
	 */
	public boolean isConnected()
	{
		return _isConnected;
	}

	/**
	 * Disconnect from connection.
	 */
	public void disconnect()
	{
		if (_isConnected == true) {
			_isConnected = false;

			try {
				_inputStream.close();
				_outputStream.close();
				_socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send a message using the given stream
	 * 
	 * @param msg Message in ASCII form
	 * @throws IOException
	 * @throws SocketException
	 */
	public void sendMessage(byte[] msg)
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
	 * Receive a message from the given stream. Blocking call.
	 * 
	 * @return the next message, or null if the connection was closed
	 */
	public byte[] receiveMessage()
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
}
