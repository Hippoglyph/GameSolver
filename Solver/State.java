package Solver;

import java.util.List;

import Design.Board.CellState;

public abstract class State {

	public enum VictoryState {
		PLAYER1, PLAYER2, DRAW, UNDECIDED
	}

	public final long encodedState;

	public State(long encodedState) {
		this.encodedState = encodedState;
	}

	public long getEncodedState() {
		return encodedState;
	}

	abstract public int rowSize();

	abstract public int colSize();

	abstract public CellState getCellState(int row, int col);

	abstract public VictoryState getVictoryState();

	abstract public List<State> getAllNextStates();
}
