package magma.tools.competition.monitor;

public class MonitorException extends Exception
{
	private static final long serialVersionUID = -8916896151556232191L;

	public MonitorException()
	{
		super();
	}

	public MonitorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MonitorException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MonitorException(String message)
	{
		super(message);
	}

	public MonitorException(Throwable cause)
	{
		super(cause);
	}
}
