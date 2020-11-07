package ticTacToe;

import java.util.Random;

import solver.State;
import design.Board;
import design.Design;

public class Game {
	private final State initState;
	private final Board board;
	private State state;

	private Random rng = new Random();

	public Game() {
		initState = new TicTacToeState(0);
		state = initState;
		board = new Design("Tick Tac Toe", 450, 450, initState.colSize(), initState.rowSize()).getBoard();
		board.draw(initState);
		board.registerGridLocationListener(this::playerClicked);
	}

	private void playerClicked(int row, int col) {
		State newState = state.getNextState(row, col);
		state = newState == null ? state : newState;
		board.draw(state);
	}
}
