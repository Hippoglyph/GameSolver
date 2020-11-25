package solver.persist;

public class Pair<T, Y> {

	private T t;
	private Y y;

	public Pair(T t, Y y) {
		this.t = t;
		this.y = y;
	}

	public T first() {
		return t;
	}

	public Y second() {
		return y;
	}
}
