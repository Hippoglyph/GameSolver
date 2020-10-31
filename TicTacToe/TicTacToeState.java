package TicTacToe;

import Design.Board.CellState;
import Solver.State;

public class TicTacToeState extends State {

	private static final int EMPTY = 0;
	private static final int CIRCLE = 1;
	private static final int CROSS = 2;

	public static final int rowSize = 3;
	public static final int colSize = 3;
	private final int[][] state;

	public TicTacToeState(long encodedState) {
		super(encodedState);
		state = getState(encodedState);
		// System.out.println(String.format("%d -> %d", encodedState,
		// getEncodedState(state)));
	}

	public int[][] getState(long encodedState) {
		int[][] gridState = new int[rowSize][colSize];
		for (int r = 0; r < rowSize; r++) {
			for (int c = 0; c < colSize; c++) {
				long row = (encodedState >> ((rowSize - 1 - r) * rowSize * 2));
				gridState[r][c] = (int) ((row >> ((colSize - 1 - c) * 2)) & 3);
			}
		}
		return gridState;
	}

	public long getEncodedState(int[][] gridState) {
		long encodedState = 0L;
		for (int r = 0; r < rowSize; r++) {
			for (int c = 0; c < colSize; c++) {
				long row = (gridState[r][c] << ((rowSize - 1 - r) * rowSize * 2));
				encodedState = encodedState | (row << ((colSize - 1 - c) * 2));
			}
		}
		return encodedState;
	}

	@Override
	public CellState getCellState(int x, int y) {
		if (x >= rowSize || x < 0 || y >= colSize || y < 0)
			return null;
		int encodedCellState = state[x][y];

		if (encodedCellState == EMPTY)
			return CellState.EMPTY;
		else if (encodedCellState == CIRCLE)
			return CellState.PLAYER1;
		else if (encodedCellState == CROSS)
			return CellState.PLAYER2;

		return null;
	}

	@Override
	public int rowSize() {
		return rowSize;
	}

	@Override
	public int colSize() {
		return colSize;
	}

}
