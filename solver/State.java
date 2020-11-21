package solver;

import java.util.List;

import game.Board.CellState;

public abstract class State {

	public static final int EMPTY = 0;
	public static final int CIRCLE = 1;
	public static final int CROSS = 2;

	public enum VictoryState {
		PLAYER1, PLAYER2, DRAW, UNDECIDED
	}

	public enum PlayerTurn {
		PLAYER1, PLAYER2
	}

	protected final long encodedState;
	protected PlayerTurn nextTurn;
	protected int[][] state = null;

	public State(long encodedState) {
		this.encodedState = encodedState;
	}

	public long getEncodedState() {
		return encodedState;
	}

	abstract public int rowSize();

	abstract public int colSize();

	protected int[][] getState() {
		if (state == null) {
			state = getState(encodedState);
		}
		return state;
	}

	abstract protected int[][] getState(long encodedState);

	abstract protected long getEncodedState(int[][] gridState);

	public CellState getCellState(int r, int c) {
		if (r >= rowSize() || r < 0 || c >= colSize() || c < 0)
			return null;
		int[][] gridState = getState();
		int encodedCellState = gridState[r][c];

		if (encodedCellState == EMPTY)
			return CellState.EMPTY;
		else if (encodedCellState == CIRCLE)
			return CellState.PLAYER2;
		else if (encodedCellState == CROSS)
			return CellState.PLAYER1;

		return null;
	}

	abstract public VictoryState getVictoryState();

	abstract public List<State> getAllNextStates();

	abstract public State getNextState(int row, int col);

	public PlayerTurn nextTurn() {
		if (nextTurn == null) {
			int player2Pieces = 0;
			int player1Pieces = 0;
			for (int r = 0; r < rowSize(); r++) {
				for (int c = 0; c < colSize(); c++) {
					switch (getCellState(r, c)) {
					case PLAYER2:
						player2Pieces++;
						break;
					case PLAYER1:
						player1Pieces++;
						break;
					default:
						break;
					}
				}
			}
			nextTurn = player1Pieces > player2Pieces ? PlayerTurn.PLAYER2 : PlayerTurn.PLAYER1;
		}
		return nextTurn;
	}

	protected int[][] getCopyOfState() {
		int[][] gridState = getState();
		int[][] newGridState = new int[rowSize()][colSize()];
		for (int r = 0; r < rowSize(); r++) {
			for (int c = 0; c < colSize(); c++) {
				newGridState[r][c] = gridState[r][c];
			}
		}
		return newGridState;
	}
}
