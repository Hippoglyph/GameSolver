package solver.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import nl.cwi.monetdb.embedded.env.MonetDBEmbeddedException;

public class DatabaseFacade {

	private static final int bufferLimit = (int) Math.pow(2, 20);

	private List<Pair<Long, Double>> unstoredData = new ArrayList<>();

	final CountUpDownLatch latch = new CountUpDownLatch();

	private String gameName;

	public DatabaseFacade(String gameName) {
		this.gameName = gameName.trim().replaceAll("\s+", "_");
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

	public List<Pair<Long, Double>> get(Long encodedState) {
		waitUntilSynced();
		try {
			return Database.fetch(gameName, encodedState);
		} catch (MonetDBEmbeddedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int size() {
		commit();
		waitUntilSynced();
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

		latch.countUp();
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
			latch.countDown();
		});
		unstoredData.clear();
	}

	private void waitUntilSynced() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
