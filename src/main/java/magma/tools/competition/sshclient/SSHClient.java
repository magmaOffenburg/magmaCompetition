package magma.tools.competition.sshclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Observable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * SSH client for the JSha SSH implementation.
 * 
 * Implements {@link ISSHClient} Implements {@link Runnable} Extends
 * {@link Observable}
 * 
 * @author Simon Gutjahr
 * 
 */
public class SSHClient extends Observable implements Runnable, ISSHClient
{
	private JSch _jsch;

	private Session _session;

	private Channel _channel;

	private int _timeout;

	private PrintStream _printStream;

	private BufferedReader _bufferedReader;

	private boolean _exitRequest;

	private Thread _thread;

	private SSHClientConState _conState;

	/**
	 * Enumeration for the connection states
	 * 
	 */
	private enum SSHClientConState {
		SSHCLIENT_CON_STATE_INIT, SSHCLIENT_CON_STATE_CONNECTING, SSHCLIENT_CON_STATE_CONNECTED, SSHCLIENT_CON_STATE_CONNECTION_ERROR, SSHCLIENT_CON_STATE_IO_ERROR
	}

	/**
	 * Constructor for the SSHClient.
	 * 
	 * @param username Name for the login at the other ssh client
	 * @param server DNS name or IP address of the other ssh client
	 * @throws SSHClientException
	 */
	public SSHClient(String username, String server) throws SSHClientException
	{
		_jsch = new JSch();
		try {
			_session = _jsch.getSession(username, server);
		} catch (JSchException e) {
			throw new SSHClientException("JSchException " + e.getMessage());
		}

		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		_session.setConfig(config);

		_channel = null;
		_printStream = null;
		_bufferedReader = null;
		_exitRequest = false;
		_thread = null;
		_timeout = 5000;
		_conState = SSHClientConState.SSHCLIENT_CON_STATE_INIT;
	}

	/**
	 * Sets the authentication methods which should be used.
	 * 
	 * @param authenticationMethods Method for the authentication at the other
	 *        SSH client. Currently supported authentication methods can be found
	 *        at Enum {@link AuthenticationMethods}
	 */
	@Override
	public void setPreferredAuthentication(
			ISSHClient.AuthenticationMethods authenticationMethods)
	{
		if (_session != null) {
			switch (authenticationMethods) {
			case PUBLICKEY: {
				_session.setConfig("PreferredAuthentications", "publickey");
				break;
			}

			case PASSWORD: {
				_session.setConfig("PreferredAuthentications", "password");
				break;
			}

			default: {
				break;
			}
			}
		}
	}

	/**
	 * Sets the path to the known hosts file.
	 * 
	 * @param path path to the known hosts file
	 * @throws SSHClientException
	 */
	@Override
	public void setKnownHosts(String path) throws SSHClientException
	{
		if (_jsch != null) {
			try {
				_jsch.setKnownHosts(path);
			} catch (JSchException e) {
				throw new SSHClientException("JSchException " + e.getMessage());
			}
		}
	}

	/**
	 * Sets the own identity. In this case this is the rsa public key.
	 * 
	 * @param path to the own rsa public key
	 * @throws SSHClientException
	 */
	@Override
	public void setIdentity(String path) throws SSHClientException
	{
		if (_jsch != null) {
			try {
				_jsch.addIdentity(path);
			} catch (JSchException e) {
				throw new SSHClientException("JSchException " + e.getMessage());
			}
		}
	}

	/**
	 * Sets the own identity. In this case this is the rsa public key.
	 * 
	 * @param path to the own rsa public key
	 * @param password of the key
	 * @throws SSHClientException
	 */
	@Override
	public void setIdentity(String path, String password)
			throws SSHClientException
	{
		if (_jsch != null) {
			try {
				_jsch.addIdentity(path, password);
			} catch (JSchException e) {
				throw new SSHClientException("JSchException " + e.getMessage());
			}
		}
	}

	@Override
	public void setPassword(String password)
	{
		if (password != null) {
			_session.setPassword(password);
		}
	}

	/**
	 * Set the connection timeout.
	 * 
	 * @param timeout Timeout for the connection
	 */
	@Override
	public void setConnectionTimeout(int timeout)
	{
		_timeout = timeout;
	}

	/**
	 * Send a command to the other SSH client.
	 * 
	 * @param cmd The command to send to the other SSH client
	 * @return True if the command was sent, false if not
	 */
	@Override
	public boolean sendCmd(String cmd)
	{
		boolean bReturn = false;

		/* Check if connection is established */
		if ((_channel != null) && (_printStream != null)) {
			if (_channel.isConnected() == true) {

				/* Send a command to the other ssh client */
				_printStream.println(cmd);
				_printStream.flush();

				bReturn = true;
			}
		}

		return bReturn;
	}

	/**
	 * Creates the thread which handles the incoming data and starts it.
	 * 
	 * @return Returns the thread
	 * @throws SSHClientException
	 */
	@Override
	public void connect() throws SSHClientException
	{
		/* Create and start the receiver thread */
		_thread = new Thread(this);
		_thread.start();

		int timeout = (_timeout > 5000) ? _timeout : 5000;

		long start = System.currentTimeMillis();
		while (_conState == SSHClientConState.SSHCLIENT_CON_STATE_INIT
				&& ((System.currentTimeMillis() - timeout) < start)) {
		}

		switch (_conState) {
		case SSHCLIENT_CON_STATE_CONNECTION_ERROR: {
			throw new SSHClientException("Connection error");
		}
		case SSHCLIENT_CON_STATE_IO_ERROR: {
			throw new SSHClientException("IO error");
		}
		default:
			break;
		}
	}

	/**
	 * Returns the connection status.
	 * 
	 * @return True if a connection is established, if not false
	 */
	@Override
	public boolean isConnected()
	{
		boolean bReturn = false;

		if (_channel != null) {
			bReturn = _channel.isConnected();
		}

		return bReturn;
	}

	/**
	 * Interrupts the thread. After this method is called, the thread exits.
	 */
	@Override
	public void disconnect()
	{
		_exitRequest = true;

		if (_thread != null) {
			_thread.interrupt();
		}
	}

	/**
	 * Initialization of the SSH connection.
	 * 
	 */
	private void init()
	{
		/* Initialize the session */
		try {
			_session.connect(_timeout);

			/* Initialize the channel as a shell */
			_channel = _session.openChannel("shell");
			_channel.connect(_timeout);
		} catch (JSchException e1) {
			_conState = SSHClientConState.SSHCLIENT_CON_STATE_CONNECTION_ERROR;
			e1.printStackTrace();
			return;
		}

		if (_channel.isConnected() == false) {
			try {
				Thread.sleep(_timeout);
			} catch (InterruptedException e) {
			}
		}

		if (_channel.isConnected() == true) {
			/* Set input and output stream */
			try {
				_printStream = new PrintStream(_channel.getOutputStream(), true);
				_bufferedReader = new BufferedReader(new InputStreamReader(
						_channel.getInputStream()));
				_conState = SSHClientConState.SSHCLIENT_CON_STATE_CONNECTED;
			} catch (IOException e) {
				_conState = SSHClientConState.SSHCLIENT_CON_STATE_IO_ERROR;
			}
		} else {
			_conState = SSHClientConState.SSHCLIENT_CON_STATE_CONNECTION_ERROR;
		}
	}

	/**
	 * Run method for the data receiver thread.
	 */
	@Override
	public void run()
	{
		try {

			/* Initialization */
			init();

			/* Loop until ssh close event */
			while (!_exitRequest && !_thread.isInterrupted()) {
				/* Read data */
				if (_bufferedReader.ready() == true) {
					String response = _bufferedReader.readLine();

					/* Forward data to the observers */
					this.setChanged();
					this.notifyObservers(response);
				}
			}
		} catch (IOException e) {
		}

		/* Close the ssh connection */
		if (this.isConnected() == true) {
			_channel.disconnect();
			_session.disconnect();
		}
	}
}