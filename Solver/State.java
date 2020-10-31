package Solver;

import Design.Board.CellState;

public abstract class State {
	public final long encodedState;

	public State(long encodedState) {
		this.encodedState = encodedState;
	}

	abstract public int rowSize();

	abstract public int colSize();

	abstract public CellState getCellState(int row, int col);
}
