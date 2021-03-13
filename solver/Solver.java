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
	private Random rng;
	private long lastProgressTimeStamp;
	private static long printFreq = 600;
	private int estimatedSize = 1;

	public void initilize(String gameName, State initState) {
		scoreMap = new ScoreMap(gameName);
		long startTime = System.currentTimeMillis();
		lastProgressTimeStamp = 0;
		System.out.println("Solving " + gameName);
		if (!scoreMap.containsKey(initState)) {
			estimatedSize = estimatedSize(initState);
			solveFromBottom(initState);
		}
		System.out.println("Solved " + scoreMap.size() + " states " + getTime(System.currentTimeMillis() - startTime));
		if (interrupted)
			close();
	}

	private String getTime(long ms) {
		String unit = "ms";
		long number = ms;
		if (ms > 36000000) {
			unit = "h";
			number = (long) ((double) ms / 3600000.0);
		} else if (ms > 600000) {
			unit = "min";
			number = (long) ((double) ms / 60000.0);
		} else if (ms > 10000) {
			unit = "s";
			number = (long) ((double) ms / 1000.0);
		}

		return String.format("%d%s", number, unit);
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
			solveFromBottom(nextStates.get(rng.nextInt(nextStates.size())));
		solve(state);
	}

	private int estimatedSize(State root) {
		double count = 0;
		int numOfTrials = 1000;
		rng = new Random();
		for (int t = 0; t < numOfTrials; t++) {
			count += estimateSize(root) / numOfTrials;
		}

		return (int) count;
	}

	private int estimateSize(State state) {
		List<State> nextStates = state.getAllNextStates();
		if (nextStates.isEmpty())
			return 1;
		State newState = nextStates.get(rng.nextInt(nextStates.size()));
		return 1 + estimateSize(newState) * nextStates.size();
	}

	private void solve(State state) {
		progressPrint();
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

	private void progressPrint() {
		if (System.currentTimeMillis() - lastProgressTimeStamp > printFreq * 1000) {
			int statesSolved = scoreMap.size();
			System.out.println(
					String.format("%d states solved (%.1f%%)", statesSolved, statesSolved * 100.0 / estimatedSize));
			lastProgressTimeStamp = System.currentTimeMillis();
		}
	}

	public void interrupt() {
		interrupted = true;
	}

	public void close() {
		scoreMap.close();
	}
}
