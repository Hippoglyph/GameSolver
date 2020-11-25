package solver.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;

public class DatabaseFacade {

	private static final int bufferLimit = (int) Math.pow(2, 10);

	private List<Pair<Long, Double>> unstoredData = new ArrayList<>();

	private String gameName;

	public DatabaseFacade(String gameName) {
		this.gameName = gameName.trim().replaceAll("\s*", "_");
		try {
			Database.init(this.gameName);
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save(long encodedState, double score) {
		unstoredData.add(new Pair<Long, Double>(encodedState, score));
		if (unstoredData.size() >= bufferLimit) {
			clear();
		}
	}

	public Double get(long encodedState) {
		try {
			Pair<Long, Double> pair = Database.fetch(gameName, encodedState);
			if (pair == null)
				return null;
			return pair.second();
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void fill(Map<Long, Double> scoreMap) {
		try {
			List<Pair<Long, Double>> pairs = Database.fetch(gameName);
			pairs.forEach(p -> scoreMap.put(p.first(), p.second()));
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clear() {
		if (unstoredData.isEmpty())
			return;
		// System.out.println("Storing to database...");
		long startTime = System.currentTimeMillis();
		try {
			Database.store(gameName, unstoredData);
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(
//				"Stored " + unstoredData.size() + " items, cost " + (System.currentTimeMillis() - startTime) + "ms");
		unstoredData.clear();
	}

	public void close() {
		clear();
		Database.close();
	}
}
