package solver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import solver.persist.DatabaseFacade;

public class Solver {

	private Map<Long, Double> scoreMap;
	private static final double decay = 0.99;
	private final String gameName;
	private DatabaseFacade facade;

	public Solver(String gameName, State initState) {
		this.gameName = gameName;
		scoreMap = new HashMap<>();
		this.facade = new DatabaseFacade(gameName);
		loadFromDatabase();
		long startTime = System.currentTimeMillis();
		int loadedItemCount = scoreMap.size();
		solve(initState);
		facade.clear();
		facade.close();
		System.out.println("Solved " + (scoreMap.size() - loadedItemCount) + " items, cost "
				+ (System.currentTimeMillis() - startTime) + "ms");
	}

	private void loadFromDatabase() {
		System.out.println("Loading from database...");
		long startTime = System.currentTimeMillis();
		facade.fill(scoreMap);
		System.out.println(
				"Loaded " + scoreMap.size() + " items, cost " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public double score(State state) {
		return scoreMap.get(state.getEncodedState());
	}

	public State getBestState(State state) {
		List<State> nextStates = state.getAllNextStates();
		if (nextStates.isEmpty())
			return null;
		if (!nextStates.stream().allMatch(s -> scoreMap.containsKey(s.getEncodedState())))
			throw new IllegalStateException();

		Collections.shuffle(nextStates);

		switch (state.nextTurn()) {
		case PLAYER1:
			return nextStates.stream()
					.max((a, b) -> scoreMap.get(a.getEncodedState()).compareTo(scoreMap.get(b.getEncodedState())))
					.get();
		case PLAYER2:
			return nextStates.stream()
					.min((a, b) -> scoreMap.get(a.getEncodedState()).compareTo(scoreMap.get(b.getEncodedState())))
					.get();
		default:
			throw new IllegalArgumentException("Unexpected value: " + state.nextTurn());
		}
	}

	private void solve(State state) {
		if (scoreMap.containsKey(state.getEncodedState()))
			return;

		List<State> nextStates = state.getAllNextStates();

		if (nextStates.isEmpty()) {
			save(state, evaluate(state));
			return;
		}

		nextStates.forEach(s -> solve(s));

		List<Double> scores = nextStates.stream().map(s -> scoreMap.get(s.getEncodedState()))
				.collect(Collectors.toList());

		switch (state.nextTurn()) {
		case PLAYER1:
			save(state, decay * scores.stream().max(Double::compare).get());
			break;
		case PLAYER2:
			save(state, decay * scores.stream().min(Double::compare).get());
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + state.nextTurn());
		}
	}

	private void save(State state, Double score) {
		scoreMap.put(state.getEncodedState(), score);
		facade.save(state.getEncodedState(), score);
	}

	private double evaluate(State state) {
		switch (state.getVictoryState()) {
		case DRAW:
			return 0;
		case PLAYER1:
			return 1;
		case PLAYER2:
			return -1;
		default:
			throw new IllegalArgumentException("Unexpected value: " + state.getVictoryState());
		}
	}
}
