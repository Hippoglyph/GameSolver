package game;

import solver.Solver;
import solver.State;
import solver.State.VictoryState;

public class Game {
	private final State initState;
	private final Board board;
	private State state;
	private Solver solver;

	public Game(String name, boolean start, State initState, int boardWidth, int boardHeight) {
		this.initState = initState;
		solver = new Solver(initState);
		state = start ? initState : solver.getBestState(initState);
		board = new Design(name, boardWidth, boardHeight, initState.colSize(), initState.rowSize()).getBoard();
		board.draw(state);
		board.registerGridLocationListener(this::playerClicked);
	}

	private void playerClicked(int row, int col) {
		if (state.getVictoryState() != VictoryState.UNDECIDED) {
			state = initState;
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
