package magma.tools.competition.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import magma.tools.competition.domain.ITeam;

public class CSVWriter
{
	public static void write(String pathToCSVFile, String separator,
			LinkedList<ITeam> teams)
	{
		BufferedWriter writer;

		try {
			writer = new BufferedWriter(new FileWriter(new File(pathToCSVFile)));

			// write header line
			writer.write("TeamName;Username;StartScript;Path;SetTeam");
			writer.newLine();

			for (ITeam team : teams) {
				writer.write(new String(team.getName() + ";" + team.getUsername()
						+ ";" + team.getStartScriptFileName() + ";"
						+ team.getPathToScriptFile() + ";" + team.isSetTeam()));
				writer.newLine();
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
