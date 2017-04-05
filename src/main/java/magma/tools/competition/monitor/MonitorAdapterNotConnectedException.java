package magma.tools.competition.monitor;

public class MonitorAdapterNotConnectedException extends Exception
{
	private static final long serialVersionUID = -3945141970240832125L;

	public MonitorAdapterNotConnectedException()
	{
		super();
	}

	public MonitorAdapterNotConnectedException(
			String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MonitorAdapterNotConnectedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MonitorAdapterNotConnectedException(String message)
	{
		super(message);
	}

	public MonitorAdapterNotConnectedException(Throwable cause)
	{
		super(cause);
	}
}
