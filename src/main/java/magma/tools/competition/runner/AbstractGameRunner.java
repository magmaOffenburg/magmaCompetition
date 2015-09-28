package magma.tools.competition.runner;

public abstract class AbstractGameRunner extends Thread
{
	public abstract void startGame();

	public abstract SimulationEvent getSimulationEvent();

	public abstract void setFinished(boolean b);

}
