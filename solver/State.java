package solver;

import java.util.List;

import game.Board.CellState;

public abstract class State {

	public static final int EMPTY = 0;
	public static final int PLAYER1 = 1;
	public static final int PLAYER2 = 2;

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

	abstract public String getGameName();

	public long getEncodedState() {
		return encodedState;
	}

	abstract public int rowSize();

	abstract public int colSize();

	public int getCellEncodingSize() {
		return 2;
	}

	private int getCellEncodingBitMerge() {
		return ~(-1 << getCellEncodingSize());
	}

	protected int[][] getState() {
		if (state == null) {
			state = getState(encodedState);
		}
		return state;
	}

	protected int[][] getState(long encodedState) {
		long mutatedState = encodedState;
		int[][] gridState = new int[rowSize()][colSize()];
		for (int r = 0; r < rowSize(); r++) {
			for (int c = 0; c < colSize(); c++) {
				gridState[r][c] = (int) (mutatedState & getCellEncodingBitMerge());
				mutatedState = mutatedState >> getCellEncodingSize();
			}
		}
		return gridState;
	}

	protected long getEncodedState(int[][] gridState) {
		long encodedState = 0L;
		for (int r = rowSize() - 1; r >= 0; r--) {
			for (int c = colSize() - 1; c >= 0; c--) {
				encodedState = encodedState | gridState[r][c];
				encodedState = encodedState << getCellEncodingSize();
			}
		}
		encodedState = encodedState >> getCellEncodingSize();
		return encodedState;
	}

	public CellState getCellState(int r, int c) {
		if (r >= rowSize() || r < 0 || c >= colSize() || c < 0)
			return null;
		int[][] gridState = getState();
		int encodedCellState = gridState[r][c];

		if (encodedCellState == EMPTY)
			return CellState.EMPTY;
		else if (encodedCellState == PLAYER2)
			return CellState.PLAYER2;
		else if (encodedCellState == PLAYER1)
			return CellState.PLAYER1;

		return null;
	}

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

	abstract public VictoryState getVictoryState();

	abstract public List<State> getAllNextStates();

	abstract public State getNextState(int row, int col);
}
