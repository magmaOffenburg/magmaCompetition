package magma.tools.competition.domain;

@FunctionalInterface
public interface ChangeHandler<T>
{

	void onChange(T subject);

}
