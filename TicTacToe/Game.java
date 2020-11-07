package TicTacToe;

import java.util.List;
import java.util.Random;

import Design.Board;
import Design.Design;
import Solver.State;

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
		List<State> newStates = state.getAllNextStates();
		state = newStates.isEmpty() ? initState : newStates.get(rng.nextInt(newStates.size()));
		board.draw(state);
	}
}
