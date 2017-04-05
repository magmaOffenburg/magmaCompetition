package magma.tools.competition.domain;

public interface ChangeNotifier<T> {
	void addChangeHandler(ChangeHandler<T> handler);

	void removeChangeHandler(ChangeHandler<T> handler);
}
