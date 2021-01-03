package solver.persist;

import java.util.List;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import solver.State;

public class ScoreMap {

	private static final int cacheSize = (int) Math.pow(2, 32);

	private DatabaseFacade facade;
	private Cache<Long, Double> cache;
	private Cache<Long, Boolean> presentCache;

	public ScoreMap(String gameName) {
		this.facade = new DatabaseFacade(gameName);
		this.cache = Caffeine.newBuilder().maximumSize(cacheSize).build();
		this.presentCache = Caffeine.newBuilder().maximumSize(cacheSize).build();
	}

	public boolean containsKey(State state) {
		Long key = state.getEncodedState();
		Boolean present = presentCache.getIfPresent(key);
		if (present != null) {
			return present;
		}
		return get(state) != null;
	}

	public Double get(State state) {
		Long key = state.getEncodedState();
		Double value = cache.getIfPresent(key);
		if (value != null)
			return value;
		Boolean present = presentCache.getIfPresent(key);
		if (present != null && !present) {
			return null;
		}
		List<Pair<Long, Double>> pairs = facade.get(state.getEncodedState());
		pairs.forEach(p -> {
			presentCache.put(p.first(), true);
			cache.put(p.first(), p.second());
		});
		if (cache.getIfPresent(key) == null)
			presentCache.put(key, false);
		return cache.getIfPresent(key);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public void put(State state, Double value) {
		Long key = state.getEncodedState();
		facade.save(key, value);
		cache.put(key, value);
		presentCache.put(key, true);
	}

	public int size() {
		return facade.size();
	}

	public void close() {
		facade.close();
	}
}
