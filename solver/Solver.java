package solver;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import solver.persist.ScoreMap;

public class Solver {

	private ScoreMap scoreMap;
	private static final double decay = 0.99;
	private volatile boolean interrupted;

	public void initilize(String gameName, State initState) {
		scoreMap = new ScoreMap(gameName);
		long startTime = System.currentTimeMillis();
		System.out.println("Solving " + gameName);
		System.out.println(estimatedSize(initState));
		if (!scoreMap.containsKey(initState)) {
			solveFromBottom(initState);
		}
		System.out.println("Solved " + scoreMap.size() + " states " + (System.currentTimeMillis() - startTime) + "ms");
		if (interrupted)
			close();
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

	private void solveFromBottom(State state) {
		List<State> nextStates = state.getAllNextStates();
		if (nextStates.isEmpty())
			solveFromBottom(nextStates.get(0));
		solve(state);
	}

	private int estimatedSize(State root) {
		double count = 0;
		int numOfTrials = 10000;
		Random rng = new Random();
		for (int t = 0; t < numOfTrials; t++) {
			double prod = 1;
			double trialCount = 1;
			State state = root;
			List<State> nextStates = state.getAllNextStates();
			while (!nextStates.isEmpty()) {
				prod *= nextStates.size();
				trialCount += prod;
				state = nextStates.get(rng.nextInt(nextStates.size()));
				nextStates = state.getAllNextStates();
			}
			count += trialCount;
		}

		return (int) (count / ((double) numOfTrials));
	}

	private void solve(State state) {
		if (interrupted)
			return;
		if (scoreMap.containsKey(state))
			return;

		List<State> nextStates = state.getAllNextStates();

		if (nextStates.isEmpty()) {
			save(state, evaluate(state));
			return;
		}

		nextStates.forEach(s -> solve(s));

		List<Double> scores = nextStates.stream().map(s -> scoreMap.get(s)).collect(Collectors.toList());

		if (interrupted)
			return;
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

	public void interrupt() {
		interrupted = true;
	}

	public void close() {
		scoreMap.close();
	}
}
