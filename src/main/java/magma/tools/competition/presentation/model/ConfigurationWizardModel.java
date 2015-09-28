package magma.tools.competition.presentation.model;

public class ConfigurationWizardModel
{
	private int numOfCluster;

	private double gameDuration;

	private WizardPageOneTableModel pageOneTableModel;

	public ConfigurationWizardModel()
	{
		pageOneTableModel = new WizardPageOneTableModel();
		numOfCluster = 1;
		gameDuration = 10;
	}

	public WizardPageOneTableModel getPageOneTableModel()
	{
		return pageOneTableModel;
	}

	public void createPhases(int num)
	{
		pageOneTableModel.createPhases(num);
	}

	public void setNumOfCluster(int num)
	{
		if (num > 0) {
			numOfCluster = num;
		}
	}

	public void setGameDuration(double duration)
	{
		if (duration > 0) {
			gameDuration = duration;
		}
	}

	public int getNumOfCluster()
	{
		return numOfCluster;
	}

	public double getGameDuration()
	{
		return gameDuration;
	}
}
