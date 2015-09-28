package magma.tools.competition.domain;

public class ProxyNotResolvableException extends Exception
{

	private static final long serialVersionUID = -4538423874055102215L;

	public ProxyNotResolvableException()
	{
		super();
	}

	public ProxyNotResolvableException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProxyNotResolvableException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ProxyNotResolvableException(String message)
	{
		super(message);
	}

	public ProxyNotResolvableException(Throwable cause)
	{
		super(cause);
	}

}
