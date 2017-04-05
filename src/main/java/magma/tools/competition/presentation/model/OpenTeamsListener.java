package magma.tools.competition.presentation.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import magma.tools.competition.csv.CSVReader;
import magma.tools.competition.csv.CSVReadingException;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.util.ClusterConfiguration;

public class OpenTeamsListener implements ActionListener
{
	private JFileChooser chooser;

	private LinkedList<ITeam> teams;

	private String teamFilePath;

	public OpenTeamsListener()
	{
		this.chooser = new JFileChooser("Verzeichnis wählen");
	}

	public LinkedList<ITeam> getTeams()
	{
		return teams;
	}

	public String getTeamFilePath()
	{
		return teamFilePath;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try {
			// TODO: change ClusterConfiguration to not throw IOExceptions
			String path = ClusterConfiguration.get().getFileStartPath();
			final File file = new File(path);

			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setCurrentDirectory(file);
			chooser.setVisible(true);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		final int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File inputVerzFile = chooser.getSelectedFile();
			teamFilePath = inputVerzFile.getPath();
			try {
				teams = CSVReader.readOutCSV(teamFilePath, true, ";");
				JOptionPane.showMessageDialog(null, "Successfully loaded " + teams.size() + " teams!");
			} catch (CSVReadingException e1) {
				JOptionPane.showMessageDialog(null, "Error open team file - Unexpected format");
			}
		} else {
			chooser.setSelectedFile(null);
		}

		chooser.setVisible(false);
	}
}
