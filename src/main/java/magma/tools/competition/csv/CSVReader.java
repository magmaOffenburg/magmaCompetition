package magma.tools.competition.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.TeamBuilder;

import com.google.inject.Inject;

/**
 * This class is responsible for reading out csv files
 *
 * @author Daniel
 *
 */
public class CSVReader
{
	@Inject
	private static TeamBuilder teamBuilder;

	/**
	 *
	 * @param pathToCSVFile the path to the CSV file which should be read out
	 * @param fileHasHeaderRow whether the first row of the csv file contains the
	 *        column headers (true) or whether the data starts in the first row
	 *        (false)
	 * @param separator the char, with which the data of the columns are
	 *        separated
	 * @return a list (one entry per row (= team)) of read out teams from CSV
	 * @throws CSVReadingException exception if a problem occurs or the structure
	 *         of the csv was bad
	 */
	public static LinkedList<ITeam> readOutCSV(String pathToCSVFile, boolean fileHasHeaderRow, String separator)
			throws CSVReadingException
	{
		final int COLUMN_COUNT = 5;

		final int COLUMN_TEAMNAME = 0;
		final int COLUMN_USERNAME = 1;
		final int COLUMN_STARTSCRIPTFILENAME = 2;
		final int COLUMN_STARTSCRIPTPATH = 3;
		final int COLUMN_SETTEAM = 4;

		LinkedList<ITeam> listOfAllTeams = new LinkedList<>();
		String rowString;
		String[] rowArray;
		BufferedReader reader;
		boolean firstRow = true;

		try {
			reader = new BufferedReader(new FileReader(new File(pathToCSVFile)));

			while (reader.ready()) {
				rowString = reader.readLine();
				rowArray = rowString.split(separator);

				// The CSV file has as much columns as needed (defined by
				// COLUMN_COUNT)
				if (rowArray.length == COLUMN_COUNT) {
					if (!(fileHasHeaderRow && firstRow)) {
						teamBuilder.name(rowArray[COLUMN_TEAMNAME]);
						teamBuilder.username(rowArray[COLUMN_USERNAME]);
						teamBuilder.startScriptFilename(rowArray[COLUMN_STARTSCRIPTFILENAME]);
						teamBuilder.pathToScriptFile(rowArray[COLUMN_STARTSCRIPTPATH]);
						teamBuilder.setTeam(Boolean.parseBoolean(rowArray[COLUMN_SETTEAM]));
						listOfAllTeams.add(teamBuilder.build());
					}
				} else {
					reader.close();
					throw new CSVReadingException(
							"Failure while reading the CSV file " + pathToCSVFile +
							": The column count of the rows was not equal to CSVReader.COLUMN_COUNT!");
				}
				firstRow = false;
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new CSVReadingException(
					"IOException while reading the CSV file " + pathToCSVFile + ": \n" + e.getMessage());
		}

		return listOfAllTeams;
	}
}
