package magma.tools.competition.sshgame;

import java.util.Observable;
import java.util.Observer;

import magma.tools.competition.sshclient.ISSHClient.AuthenticationMethods;
import magma.tools.competition.sshclient.SSHClient;
import magma.tools.competition.sshclient.SSHClientException;

public class SSHGame
{
	public static void main(String[] args)
	{

		try {
			SSHDataReceiver dataReceiverServer = new SSHDataReceiver();
			SSHDataReceiver dataReceiverMagma = new SSHDataReceiver();
			SSHDataReceiver dataReceiverMagmaOpp = new SSHDataReceiver();

			String server = "127.0.0.1";
			String username = "robocup";
			String password = "yourPasswd";
			String knownHosts = ".ssh//known_hosts";

			SSHClient sshClientServer = new SSHClient(username, server);
			sshClientServer.addObserver(dataReceiverServer);
			sshClientServer
					.setPreferredAuthentication(AuthenticationMethods.PASSWORD);
			sshClientServer.setPassword(password);
			sshClientServer.setKnownHosts(knownHosts);
			sshClientServer.connect();

			String agent1 = "127.0.0.1";
			SSHClient sshClientMagma = new SSHClient(username, agent1);
			sshClientMagma.addObserver(dataReceiverMagma);
			sshClientMagma
					.setPreferredAuthentication(AuthenticationMethods.PASSWORD);
			sshClientMagma.setPassword(password);
			sshClientMagma.setKnownHosts(knownHosts);

			String agent2 = "127.0.0.1";
			SSHClient sshClientMagmaOpp = new SSHClient(username, agent2);
			sshClientMagmaOpp.addObserver(dataReceiverMagmaOpp);
			sshClientMagmaOpp
					.setPreferredAuthentication(AuthenticationMethods.PASSWORD);
			sshClientMagmaOpp.setPassword(password);
			sshClientMagmaOpp.setKnownHosts(knownHosts);

			if (sshClientServer.isConnected()) {
				System.out.println("Connection to server ok");
				sshClientServer.sendCmd("rcssserver3d");

				try {
					Thread.sleep(4000);
					sshClientMagma.connect();

					if (sshClientMagma.isConnected()) {
						sshClientMagma
								.sendCmd("echo -e '"
										+ password
										+ "\n' | sudo -k -S su magma -c 'cd /home/magma/ && ./start.sh "
										+ server + "'");
						Thread.sleep(8000);

						sshClientMagmaOpp.connect();

						if (sshClientMagmaOpp.isConnected()) {
							sshClientMagmaOpp
									.sendCmd("echo -e '"
											+ password
											+ "\n' | sudo -k -S su magmaopp -c 'cd /home/magmaopp/ && ./start.sh "
											+ server + "'");
							System.out.println("Server and clients started");
							Thread.sleep(8000);
						}

					}

					sshClientServer.disconnect();
					sshClientMagma.disconnect();
					sshClientMagmaOpp.disconnect();
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (SSHClientException e) {
			System.out.println(e.getMessage());
		}
	}
}

class SSHDataReceiver implements Observer
{
	private String resp;

	public SSHDataReceiver()
	{

	}

	@Override
	public void update(Observable obj, Object arg)
	{
		if (arg instanceof String) {
			resp = (String) arg;
			System.out.println(resp);
		}
	}
}
