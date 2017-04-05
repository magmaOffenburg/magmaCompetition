package magma.tools.competition.sshclient;

/**
 * Interface for the SSH client.
 *
 * @author Simon Gutjahr
 *
 */
public interface ISSHClient {
	/**
	 * This enumeration holds the different authentication methods.
	 *
	 */
	public enum AuthenticationMethods { PUBLICKEY, PASSWORD }

	/**
	 * Sets the authentication methods which should be used.
	 *
	 * @param authenticationMethods Method for the authentication at the other
	 *        SSH client. Currently supported authentication methods can be found
	 *        at Enum {@link AuthenticationMethods}
	 */
	public void setPreferredAuthentication(ISSHClient.AuthenticationMethods authenticationMethods);

	/**
	 * Sets the path to the known hosts file.
	 *
	 * @param path path to the known hosts file
	 * @throws SSHClientException
	 */
	public void setKnownHosts(String path) throws SSHClientException;

	/**
	 * Sets the own identity. In this case this is the rsa public key.
	 *
	 * @param path to the own rsa public key
	 * @throws SSHClientException
	 */
	public void setIdentity(String path) throws SSHClientException;

	/**
	 * Sets the own identity. In this case this is the rsa public key.
	 *
	 * @param path to the own rsa public key
	 * @param password of the key
	 * @throws SSHClientException
	 */
	public void setIdentity(String path, String password) throws SSHClientException;

	/**
	 * Setter for the password.
	 *
	 * @param password
	 */
	public void setPassword(String password);

	/**
	 * Set the connection timeout.
	 *
	 * @param timeout Timeout for the connection
	 */
	public void setConnectionTimeout(int timeout);

	/**
	 * Send a command to the other SSH client.
	 *
	 * @param cmd The command to send to the other SSH client
	 * @return True if the command was sent, false if not
	 */
	public boolean sendCmd(String cmd);

	/**
	 * Creates the thread which handles the incoming data and starts it.
	 *
	 * @return Returns the thread
	 * @throws SSHClientException
	 */
	public void connect() throws SSHClientException;

	/**
	 * Returns the connection status.
	 *
	 * @return True if a connection is established, if not false
	 */
	public boolean isConnected();

	/**
	 * Interrupts the thread. After this method is called, the thread exits.
	 */
	public void disconnect();
}
