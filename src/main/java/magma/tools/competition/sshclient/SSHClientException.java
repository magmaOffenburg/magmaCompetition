package magma.tools.competition.sshclient;

/**
 * SSH client Exception class.
 * 
 * 
 * @author Simon Gutjahr
 * 
 */
public class SSHClientException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SSHClientException(String error)
	{
		super(error);
	}
}
