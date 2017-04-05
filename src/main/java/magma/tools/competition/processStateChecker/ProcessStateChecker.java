package magma.tools.competition.processStateChecker;

import java.util.Observable;
import java.util.Observer;

import magma.tools.competition.sshclient.ISSHClient;
import magma.tools.competition.sshclient.SSHClient;

/**
 * Implementation of the Interface IProcessStateChecker. Can be used to
 * determine if a process is running or not.
 *
 * @author Simon Gutjahr
 *
 */
public class ProcessStateChecker implements Observer, IProcessStateChecker
{
	private enum ProcessStateCheckerAction { ACTION_CHECK, ACTION_TERMINATE }

	private ISSHClient _sshClient;

	private boolean _isTerminated;

	private String _processName;

	private int _responseTimeout;

	private ProcessStateCheckerAction action;

	/**
	 * Constructor
	 *
	 * @param sshClient The ssh connection to work with.
	 */
	public ProcessStateChecker(ISSHClient sshClient)
	{
		_sshClient = sshClient;
		_isTerminated = false;
		_responseTimeout = 3000;
		_processName = "none";
		((SSHClient) _sshClient).addObserver(this);
	}

	/**
	 * Checks if a process is terminated on a remote computer via a ssh
	 * connection.
	 *
	 * @param processName The name of the process
	 * @return True if the process is not running, false if the process is
	 *         running
	 */
	@Override
	public boolean isProcessTerminated(String processName)
	{
		_isTerminated = false;
		_processName = processName;
		String cmd = "pgrep -l ".concat(processName);
		action = ProcessStateCheckerAction.ACTION_CHECK;

		_sshClient.sendCmd(cmd);

		long start = System.currentTimeMillis();
		while (_isTerminated == false && ((System.currentTimeMillis() - _responseTimeout) < start)) {
		}

		_processName = "none";

		return _isTerminated;
	}

	public boolean terminateProcess(String processName)
	{
		_isTerminated = false;
		_processName = processName;
		String cmd = "pgrep -l ".concat(processName);
		action = ProcessStateCheckerAction.ACTION_TERMINATE;

		_sshClient.sendCmd(cmd);

		long start = System.currentTimeMillis();
		while (_isTerminated == false && ((System.currentTimeMillis() - _responseTimeout) < start)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		_isTerminated = true;
		_processName = "none";

		return _isTerminated;
	}

	/**
	 * The observer update method to get the response from the ssh connection.
	 */
	@Override
	public void update(Observable o, Object arg)
	{
		String resp;
		if (!_processName.equals("none")) {
			if (arg instanceof String) {
				resp = (String) arg;

				if (resp.contains(_processName) == true) {
					switch (action) {
					case ACTION_CHECK: {
						_isTerminated = false;
						break;
					}
					case ACTION_TERMINATE: {
						String id = resp.split(" ")[0];
						_sshClient.sendCmd("kill -9 " + id);
						break;
					}
					}

					System.out.println(resp);
				}
			}
		}
	}
}
