package game;

import solver.Solver;
import solver.State;
import solver.State.VictoryState;

public class Game {
	private final State initState;
	private Board board;
	private State state;
	private Solver solver;
	private final boolean start;
	private volatile boolean earlyInterruption;

	public Game(boolean start, State initState, int boardWidth, int boardHeight) {
		this.start = start;
		this.initState = initState;
		solver = new Solver();
		new TerminalInterrupter(() -> {
			solver.interrupt();
			earlyInterruption = true;
		});
		solver.initilize(initState.getGameName(), initState);
		if (earlyInterruption)
			System.exit(0);
		state = start ? initState : solver.getBestState(initState);
		board = new Design(initState.getGameName(), boardWidth, boardHeight, initState.colSize(), initState.rowSize(),
				solver::close).getBoard();
		board.draw(state);
		board.registerGridLocationListener(this::playerClicked);
	}

	private void playerClicked(int row, int col) {
		if (state.getVictoryState() != VictoryState.UNDECIDED) {
			state = start ? initState : solver.getBestState(initState);
			board.draw(state);
			return;
		}
		State newState = state.getNextState(row, col);
		state = newState == null ? state : newState;
		newState = solver.getBestState(state);
		state = newState == null ? state : newState;
		board.draw(state);
	}
}
