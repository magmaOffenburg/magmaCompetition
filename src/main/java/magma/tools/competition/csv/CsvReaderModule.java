package magma.tools.competition.csv;

import com.google.inject.AbstractModule;

public class CsvReaderModule extends AbstractModule
{
	@Override
	protected void configure()
	{
		requestStaticInjection(CSVReader.class);
	}
}
