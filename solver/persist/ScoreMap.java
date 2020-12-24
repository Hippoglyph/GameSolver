package solver.persist;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class ScoreMap {

	private static final int cacheSize = (int) Math.pow(2, 32);

	private DatabaseFacade facade;
	private Cache<Long, Double> cache;

	public ScoreMap(String gameName) {
		this.facade = new DatabaseFacade(gameName);
		this.cache = Caffeine.newBuilder().maximumSize(cacheSize).build();
	}

	public boolean containsKey(Long key) {
		Double value = cache.getIfPresent(key);
		if (value != null)
			return true;
		return false;
	}

	public Double get(Long key) {
		Double value = cache.getIfPresent(key);
		if (value != null)
			return value;
		value = facade.get(key);
		if (value != null)
			cache.put(key, value);
		return value;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public void put(Long key, Double value) {
		facade.save(key, value);
		cache.put(key, value);
	}

	public int size() {
		return facade.size();
	}

	public void close() {
		facade.close();
	}
}
