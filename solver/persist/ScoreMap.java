package solver.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import solver.State;

public class ScoreMap {

	private static final int cacheSize = (int) Math.pow(2, 32);
	private static final int blockSize = (int) Math.pow(2, 10);

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
		Double value = cache.getIfPresent(key);
		if (value != null)
			return true;
		Boolean present = presentCache.getIfPresent(key);
		if (present != null)
			return present;
		getBlock(state);
		return cache.getIfPresent(key) != null;
	}

	public Double get(State state) {
		Long key = state.getEncodedState();
		Double value = cache.getIfPresent(key);
		if (value != null)
			return value;
		Boolean present = presentCache.getIfPresent(key);
		if (present != null && !present)
			return null;
		getBlock(state);
		return cache.getIfPresent(key);
	}

	private void getBlock(State state) {
		List<State> states = new ArrayList<State>();
		appendChildrenRecursivly(state, states);
		List<Pair<Long, Double>> pairs = facade.get(states.stream().map(s -> {
			long encodedState = s.getEncodedState();
			presentCache.put(encodedState, false);
			return encodedState;
		}).collect(Collectors.toList()));
		pairs.forEach(p -> {
			presentCache.put(p.first(), true);
			cache.put(p.first(), p.second());
		});
	}

	private void appendChildrenRecursivly(State state, List<State> states) {
		if (states.size() > blockSize)
			return;
		states.add(state);
		List<State> nextStates = state.getAllNextStates();
		if (state.getAllNextStates().isEmpty())
			return;
		nextStates.stream().filter(s -> {
			long encodedState = s.getEncodedState();
			Boolean present = presentCache.getIfPresent(encodedState);
			return cache.getIfPresent(encodedState) == null && (present == null || (present != null && present));
		}).forEach(s -> appendChildrenRecursivly(s, states));
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
