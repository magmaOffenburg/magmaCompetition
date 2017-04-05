package magma.tools.competition.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClusterConfiguration
{
	private static final String CLUSTER_CONFIGURATION_PATH = "./simmanager.properties";

	private static final String KEY_SERVER_ADDRESS = "serverAddress";

	private static final String KEY_SERVER_PORT = "serverPort";

	private static final String KEY_SERVER_USER_NAME = "serverUserName";

	private static final String KEY_SERVER_USER_PASSWORD = "serverUserPassword";

	private static final String KEY_TEAM_LEFT_ADDRESS = "teamLeftAddress";

	private static final String KEY_TEAM_RIGHT_ADDRESS = "teamRightAddress";

	private static final String FILE_START_PATH = "fileStartPath";

	private static final String KNOWN_HOSTS_PATH = "knownHostPath";

	private static ClusterConfiguration instance;

	private Properties properties;

	private ClusterConfiguration() throws IOException
	{
		properties = new Properties();
		properties.load(new FileInputStream(new File(CLUSTER_CONFIGURATION_PATH)));
	}

	public static ClusterConfiguration get() throws IOException
	{
		if (instance == null) {
			instance = new ClusterConfiguration();
		}
		return instance;
	}

	public String getServerAddress()
	{
		return getProperty(KEY_SERVER_ADDRESS);
	}

	public Object getServerPort()
	{
		return getProperty(KEY_SERVER_PORT);
	}

	public String getServerUserName()
	{
		return getProperty(KEY_SERVER_USER_NAME);
	}

	public String getServerUserPassword()
	{
		return getProperty(KEY_SERVER_USER_PASSWORD);
	}

	public String getTeamLeftAddress()
	{
		return getProperty(KEY_TEAM_LEFT_ADDRESS);
	}

	public String getTeamRightAddress()
	{
		return getProperty(KEY_TEAM_RIGHT_ADDRESS);
	}

	public String getFileStartPath()
	{
		return getProperty(FILE_START_PATH);
	}

	public String getKnownHostPath()
	{
		return getProperty(KNOWN_HOSTS_PATH);
	}

	public String checkValid()
	{
		String missingElements = "";

		if (getServerAddress() == null) {
			missingElements += "- Property " + KEY_SERVER_ADDRESS + " is missing\n";
		}

		if (getServerPort() == null) {
			missingElements += "- Property " + KEY_SERVER_PORT + " is missing\n";
		}

		if (getServerUserName() == null) {
			missingElements += "- Property " + KEY_SERVER_USER_NAME + " is missing\n";
		}

		if (getServerUserPassword() == null) {
			missingElements += "- Property " + KEY_SERVER_USER_PASSWORD + " is missing\n";
		}

		if (getTeamLeftAddress() == null) {
			missingElements += "- Property " + KEY_TEAM_LEFT_ADDRESS + " is missing\n";
		}

		if (getTeamRightAddress() == null) {
			missingElements += "- Property " + KEY_TEAM_RIGHT_ADDRESS + " is missing\n";
		}

		if (getFileStartPath() == null) {
			missingElements += "- Property " + FILE_START_PATH + " is missing\n";
		}

		if (getKnownHostPath() == null) {
			missingElements += "- Property " + KNOWN_HOSTS_PATH + " is missing\n";
		}

		// check the startFile Path and the knownHosts Path
		if (missingElements.length() == 0) {
			if (!(new File(getFileStartPath()).isDirectory())) {
				missingElements += "- Property " + FILE_START_PATH + " is not a valid directory!\n";
			}

			if (!(new File(getKnownHostPath()).isFile())) {
				missingElements += "- Property " + KNOWN_HOSTS_PATH + " is not a valid file!\n";
			}
		}

		return missingElements;
	}

	protected String getProperty(String key)
	{
		return properties.getProperty(key);
	}
}
