package magma.tools.competition.processStateChecker;

/**
 * Interface for the process state checker. Can be used to determine if a
 * process is running or not.
 *
 * @author Simon Gutjahr
 *
 */
public interface IProcessStateChecker {
	/**
	 * Checks if a process is terminated on a remote computer via a ssh
	 * connection.
	 *
	 * @param processName The name of the process
	 * @return True if the process is not running, false if the process is
	 *         running
	 */
	public boolean isProcessTerminated(String processName);

	/**
	 *
	 * @param processName The name of the process
	 * @return True if the process is not running, false if the process is
	 *         running
	 */
	public boolean terminateProcess(String processName);
}
