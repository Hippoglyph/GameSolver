package ticTacToe;

import design.Board;
import design.Design;
import solver.Solver;
import solver.State;

public class Game {
	private final State initState;
	private final Board board;
	private State state;
	private Solver solver;

	public Game(boolean start) {
		initState = new TicTacToeState(0);
		solver = new Solver(initState);
		if (start)
			state = initState;
		else
			state = solver.getBestState(initState);
		board = new Design("Tick Tac Toe", 450, 450, initState.colSize(), initState.rowSize()).getBoard();
		board.draw(state);
		board.registerGridLocationListener(this::playerClicked);
	}

	private void playerClicked(int row, int col) {
		State newState = state.getNextState(row, col);
		state = newState == null ? state : newState;
		newState = solver.getBestState(state);
		state = newState == null ? state : newState;
		board.draw(state);
	}
}
