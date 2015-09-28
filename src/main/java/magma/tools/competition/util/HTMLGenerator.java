package magma.tools.competition.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.Group;
import magma.tools.competition.domain.GroupPhase;
import magma.tools.competition.domain.GroupResult;
import magma.tools.competition.domain.ITeam;
import magma.tools.competition.domain.KoPhase;
import magma.tools.competition.domain.Phase;
import magma.tools.competition.domain.Tournament;

public class HTMLGenerator
{

	public static void generateHTML(Tournament tournament) throws IOException
	{

		String html = "<html><head><title>" + tournament.getName()
				+ "</title></head><body><h1>" + tournament.getName() + "</h1>";

		for (int iPhases = 0; iPhases < tournament.getPhases().size(); iPhases++) {

			Phase phase = tournament.getPhases().get(iPhases);

			html += "<h2>" + phase.getName() + "</h2>";

			if (phase instanceof GroupPhase) {
				GroupPhase groupPhase = (GroupPhase) phase;

				for (int iGroups = 0; iGroups < groupPhase.getGroups().size(); iGroups++) {

					Group group = groupPhase.getGroups().get(iGroups);
					GroupResult result = group.getResult();
					int teamCount = group.getTeams().size();

					html += "<h3>Group "
							+ group.getName()
							+ "</h3><h4>Ranking</h4><table border = 1><th>Rank</th><th>Team Name</th><th>Games</th><th>Goal Difference</th><th>Points</th>";

					boolean hasRank = (result.getTeamsOnRank(1).size() > 0);

					for (int iRank = 1; iRank <= teamCount; iRank++) {

						if (hasRank) {
							List<ITeam> teamsOnRank = result.getTeamsOnRank(iRank);

							for (int i = 0; i < teamsOnRank.size(); i++) {
								ITeam team = teamsOnRank.get(i);
								String teamName = team.getName();
								int gamesCount = result.getGames(team);
								int goalDifference = result.getGoals(team)
										- result.getGoalsAgainst(team);
								int points = result.getPoints(team);
								html += "<tr><td>" + iRank + "</td><td>" + teamName
										+ "</td><td>" + gamesCount + "</td><td>"
										+ goalDifference + "</td><td>" + points
										+ "</td></tr>";
							}
						} else {
							ITeam team = group.getTeams().get(iRank - 1);
							String teamName = team.getName();
							int gamesCount = 0;
							int goalDifference = 0;
							int points = 0;
							html += "<tr><td>" + iRank + "</td><td>" + teamName
									+ "</td><td>" + gamesCount + "</td><td>"
									+ goalDifference + "</td><td>" + points
									+ "</td></tr>";
						}
					}

					html += "</table><h4>Games</h4><table border = 1><th>Teams</th><th>Result</th><th>State</th>";

					for (int iGames = 0; iGames < group.getPlan().getGames().size(); iGames++) {
						Game game = group.getPlan().getGames().get(iGames);

						html += "<tr><td>" + game.getHomeTeam().getName() + " : "
								+ game.getGuestTeam().getName() + "</td><td>"
								+ game.getResult().getHomeTeamPoints() + " : "
								+ game.getResult().getGuestTeamPoints() + "</td><td>"
								+ game.getState() + "</td></tr>";
					}

					html += "</table>";
				}

			} else {
				KoPhase koPhase = (KoPhase) phase;

				html += "<h4>Games</h4><table border = 1><th>Teams</th><th>Result</th><th>State</th>";

				for (int iGames = 0; iGames < koPhase.getGames().size(); iGames++) {
					Game game = koPhase.getGames().get(iGames);

					html += "<tr><td>" + game.getHomeTeam().getName() + " : "
							+ game.getGuestTeam().getName() + "</td><td>"
							+ game.getResult().getHomeTeamPoints() + " : "
							+ game.getResult().getGuestTeamPoints() + "</td><td>"
							+ game.getState() + "</td></tr>";
				}

				html += "</table>";
			}

		}
		html += "</body></html>";

		FileWriter writer = new FileWriter(new File(ClusterConfiguration.get()
				.getFileStartPath() + "/tournament.html"));
		writer.write(html);
		writer.close();

	}
}
