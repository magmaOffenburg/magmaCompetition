package magma.tools.competition.presentation.view;

import java.util.List;

import magma.tools.competition.presentation.model.ConfigurationWizardModel;

import org.ciscavate.cjwizard.PageFactory;
import org.ciscavate.cjwizard.WizardPage;
import org.ciscavate.cjwizard.WizardSettings;

public class WizardFactory implements PageFactory
{
	private WizardPage[] pages;

	public WizardFactory(ConfigurationWizardModel model)
	{
		pages = new WizardPage[2];
		pages[0] = new WizardPageOne("One", "Configuration Wizard", model);
		pages[1] = new WizardPageThree("Two", "Configuration Wizard", model);
	}

	@Override
	public WizardPage createPage(List<WizardPage> path, WizardSettings settings)
	{
		WizardPage page = pages[path.size()];
		return page;
	}
}
