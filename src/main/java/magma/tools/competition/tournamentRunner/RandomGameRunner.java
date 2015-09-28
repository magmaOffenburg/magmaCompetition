package magma.tools.competition.tournamentRunner;

import java.util.Random;

import magma.tools.competition.domain.Game;
import magma.tools.competition.domain.GameState;

public class RandomGameRunner extends Thread
{
	Game game;

	public RandomGameRunner(Game game)
	{
		this.game = game;
	}

	@Override
	public void run()
	{
		Random random = new Random();
		do {
			game.setState(GameState.STARTED);

			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

			game.getResult().setGuestTeamPoints(random.nextInt(6));
			game.getResult().setHomeTeamPoints(random.nextInt(6));
		} while (game.getResult().getGuestTeamPoints() == game.getResult()
				.getHomeTeamPoints());

		game.setState(GameState.FINISHED);
	}
}
