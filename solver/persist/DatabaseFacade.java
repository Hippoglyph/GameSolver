package solver.persist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;

public class DatabaseFacade {

	private static final int bufferLimit = (int) Math.pow(2, 20);

	private List<Pair<Long, Double>> unstoredData = new ArrayList<>();

	private Set<UUID> pendingSaves = Collections.newSetFromMap(new ConcurrentHashMap<UUID, Boolean>());

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
			commit();
		}
	}

	public Double get(long encodedState) {
		waitUntilSynced();
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

	public int size() {
		try {
			return Database.size(gameName);
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	private void commit() {
		if (unstoredData.isEmpty())
			return;

		UUID key = UUID.randomUUID();
		pendingSaves.add(key);
		List<Pair<Long, Double>> shallowCopy = new ArrayList<>(unstoredData);

		Executors.newSingleThreadExecutor().execute(() -> {
			long startTime = System.currentTimeMillis();
			try {
				Database.store(gameName, shallowCopy);
			} catch (MonetDBEmbeddedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(
					"Stored " + shallowCopy.size() + " items, cost " + (System.currentTimeMillis() - startTime) + "ms");
			pendingSaves.remove(key);
		});
		unstoredData.clear();
	}

	private boolean isSynced() {
		return pendingSaves.isEmpty();
	}

	private void waitUntilSynced() {
		while (!isSynced()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void close() {
		commit();
		long startTime = System.currentTimeMillis();
		System.out.println("Closing database...");
		waitUntilSynced();
		Database.close();
		System.out.println("Database closed " + (System.currentTimeMillis() - startTime) + "ms");
	}
}
