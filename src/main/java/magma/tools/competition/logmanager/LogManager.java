package magma.tools.competition.logmanager;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import magma.tools.competition.domain.Game;
import magma.tools.competition.sshclient.SSHClient;
import magma.tools.competition.sshclient.SSHClientException;
import magma.tools.competition.sshclient.ISSHClient.AuthenticationMethods;
import magma.tools.competition.util.ClusterConfiguration;

public class LogManager implements Observer
{

	private static String LOG_FILE_PATH = "~/rcssserver-logs";

	private static String SPARKMONITOR_LOG = "sparkmonitor.log";

	private static String RCSSSERVER3D_LOG = "rcssserver3d.log";

	private SSHClient serverConnection;

	private int sizeFile1;

	private int sizeFile2;

	public LogManager(SSHClient serverConnection)
	{
		checkArgument(serverConnection.isConnected(),
				"The SSH Connection must already be established.");
		this.serverConnection = serverConnection;
	}

	public LogManager()
	{
		ClusterConfiguration configuration = null;
		try {
			configuration = ClusterConfiguration.get();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			this.serverConnection = new SSHClient(
					configuration.getServerUserName(),
					configuration.getServerAddress());
			this.serverConnection
					.setPreferredAuthentication(AuthenticationMethods.PASSWORD);
			this.serverConnection.setPassword(configuration
					.getServerUserPassword());
			this.serverConnection.setKnownHosts(configuration.getKnownHostPath());
			this.serverConnection.connect();

		} catch (SSHClientException e) {
			e.printStackTrace();
		}
	}

	public void storeLogFiles(String phaseName, Game game)
			throws InterruptedException
	{
		String path = getLogFilePath(phaseName, game);
		path = String.format("%s/%s", path, String.valueOf(new Date().getTime()));
		serverConnection.sendCmd(String.format("mkdir -p %s/", path));
		serverConnection.sendCmd(String.format("cp %s %s/", SPARKMONITOR_LOG,
				path));
		serverConnection.sendCmd(String.format("cp %s %s/", RCSSSERVER3D_LOG,
				path));

		serverConnection.addObserver(this);
		serverConnection.sendCmd("du " + SPARKMONITOR_LOG + " " + path + "/"
				+ SPARKMONITOR_LOG + " | cut -f -1");

		long start = System.currentTimeMillis();
		while (((sizeFile1 == 0) && (sizeFile2 == 0) || (sizeFile1 != sizeFile2))
				&& ((System.currentTimeMillis() - start) < 60000)) {
			Thread.sleep(1000);
			serverConnection.sendCmd("du " + SPARKMONITOR_LOG + " " + path + "/"
					+ SPARKMONITOR_LOG + " | cut -f -1");
		}

		serverConnection.deleteObserver(this);
	}

	public void restoreFromLogFiles(String phaseName, Game game)
			throws IOException
	{
		ClusterConfiguration configuration = ClusterConfiguration.get();
		String path = getLogFilePath(phaseName, game);
		String cmd = String
				.format(
						"find %s/*/%s | sort | tail -n 1 | xargs -i python restore.py {} %s %s",
						path, SPARKMONITOR_LOG, configuration.getServerAddress(),
						configuration.getServerPort());
		System.out.println(cmd);
		serverConnection.sendCmd(cmd);
	}

	private String getLogFilePath(String phaseName, Game game)
	{
		String homeTeamName = game.getHomeTeam().getName().replace(" ", "");
		String guestTeamName = game.getGuestTeam().getName().replace(" ", "");
		phaseName = phaseName.replace(" ", "");
		return String.format("%s/%s/%s_%s", LOG_FILE_PATH, phaseName,
				homeTeamName, guestTeamName);
	}

	@Override
	public void update(Observable o, Object arg)
	{
		String response = (String) arg;

		if (response.matches("\\d+")) {
			if (sizeFile1 == 0) {
				sizeFile1 = Integer.parseInt(response);
			} else {
				sizeFile2 = Integer.parseInt(response);
			}
		}
	}
}
