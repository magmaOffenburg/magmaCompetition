package magma.tools.competition.json;

import com.google.inject.AbstractModule;

public class JsonHandlerModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		requestStaticInjection(JsonHandler.class);
	}
}
