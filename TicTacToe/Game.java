package TicTacToe;

import Design.Board;
import Design.Design;
import Solver.State;

public class Game {

	public Game() {
		State state = new TicTacToeState(183729);
		Board board = new Design("Tick Tac Toe", 450, 450, state.colSize(), state.rowSize()).getBoard();
		board.draw(state);
		board.registerGridLocationListener(this::playerClicked);
	}

	private void playerClicked(int row, int col) {
		System.out.println(String.format("Clicked %d, %d", row, col));
	}
}
