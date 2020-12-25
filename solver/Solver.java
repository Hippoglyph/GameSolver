package solver;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import solver.persist.ScoreMap;

public class Solver {

	private ScoreMap scoreMap;
	private static final double decay = 0.99;

	public Solver(String gameName, State initState) {
		scoreMap = new ScoreMap(gameName);
		long startTime = System.currentTimeMillis();
		System.out.println("Solving " + gameName);
		solve(initState);
		System.out.println("Solved " + scoreMap.size() + " states " + (System.currentTimeMillis() - startTime) + "ms");
	}

	public State getBestState(State state) {
		List<State> nextStates = state.getAllNextStates();
		if (nextStates.isEmpty())
			return null;
		if (!nextStates.stream().allMatch(s -> scoreMap.containsKey(s)))
			throw new IllegalStateException();

		Collections.shuffle(nextStates);

		switch (state.nextTurn()) {
		case PLAYER1:
			return nextStates.stream().max((a, b) -> scoreMap.get(a).compareTo(scoreMap.get(b))).get();
		case PLAYER2:
			return nextStates.stream().min((a, b) -> scoreMap.get(a).compareTo(scoreMap.get(b))).get();
		default:
			throw new IllegalArgumentException("Unexpected value: " + state.nextTurn());
		}
	}

	private void solve(State state) {
		if (scoreMap.containsKey(state))
			return;

		List<State> nextStates = state.getAllNextStates();

		if (nextStates.isEmpty()) {
			save(state, evaluate(state));
			return;
		}

		nextStates.forEach(s -> solve(s));

		List<Double> scores = nextStates.stream().map(s -> scoreMap.get(s)).collect(Collectors.toList());

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
		scoreMap.put(state, score);
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

	public void close() {
		scoreMap.close();
	}
}
