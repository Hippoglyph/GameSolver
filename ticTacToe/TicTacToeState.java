package ticTacToe;

import java.util.ArrayList;
import java.util.List;

import game.Board.CellState;
import solver.State;

public class TicTacToeState extends State {

	public static final int rowSize = 3;
	public static final int colSize = 3;

	public TicTacToeState(long encodedState) {
		super(encodedState);
	}

	@Override
	protected int[][] getState(long encodedState) {
		int[][] gridState = new int[rowSize][colSize];
		for (int r = 0; r < rowSize; r++) {
			for (int c = 0; c < colSize; c++) {
				long row = (encodedState >> ((rowSize - 1 - r) * rowSize * 2));
				gridState[r][c] = (int) ((row >> ((colSize - 1 - c) * 2)) & 3);
			}
		}
		return gridState;
	}

	@Override
	protected long getEncodedState(int[][] gridState) {
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
	public int rowSize() {
		return rowSize;
	}

	@Override
	public int colSize() {
		return colSize;
	}

	@Override
	public VictoryState getVictoryState() {
		int emptyCount = 0;

		// Row
		for (int r = 0; r < rowSize(); r++) {
			int circleCount = 0;
			int crossCount = 0;
			for (int c = 0; c < colSize(); c++) {
				switch (getCellState(r, c)) {
				case EMPTY:
					emptyCount++;
					break;
				case PLAYER2:
					circleCount++;
					break;
				case PLAYER1:
					crossCount++;
					break;
				}
			}
			if (circleCount >= rowSize())
				return VictoryState.PLAYER2;
			if (crossCount >= rowSize())
				return VictoryState.PLAYER1;
		}

		// Col
		for (int c = 0; c < colSize(); c++) {
			int circleCount = 0;
			int crossCount = 0;
			for (int r = 0; r < rowSize(); r++) {
				switch (getCellState(r, c)) {
				case PLAYER2:
					circleCount++;
					break;
				case PLAYER1:
					crossCount++;
					break;
				default:
					break;
				}
			}
			if (circleCount >= colSize())
				return VictoryState.PLAYER2;
			if (crossCount >= colSize())
				return VictoryState.PLAYER1;
		}

		// Diag
		int circleCountMain = 0;
		int crossCountMain = 0;
		int circleCount = 0;
		int crossCount = 0;
		for (int i = 0; i < rowSize(); i++) {
			switch (getCellState(i, i)) {
			case PLAYER2:
				circleCountMain++;
				break;
			case PLAYER1:
				crossCountMain++;
				break;
			default:
				break;
			}
			switch (getCellState(rowSize() - 1 - i, i)) {
			case PLAYER2:
				circleCount++;
				break;
			case PLAYER1:
				crossCount++;
				break;
			default:
				break;
			}
		}

		if (circleCount >= rowSize() || circleCountMain >= rowSize())
			return VictoryState.PLAYER2;
		if (crossCount >= rowSize() || crossCountMain >= rowSize())
			return VictoryState.PLAYER1;

		if (emptyCount <= 0)
			return VictoryState.DRAW;

		return VictoryState.UNDECIDED;
	}

	@Override
	public List<State> getAllNextStates() {
		List<State> states = new ArrayList<>();

		if (getVictoryState() != VictoryState.UNDECIDED)
			return states;

		int piece = nextTurn() == PlayerTurn.PLAYER1 ? CROSS : CIRCLE;

		for (int r = 0; r < rowSize(); r++) {
			for (int c = 0; c < colSize(); c++) {
				if (getCellState(r, c) == CellState.EMPTY) {
					int[][] newState = getCopyOfState();
					newState[r][c] = piece;
					states.add(new TicTacToeState(getEncodedState(newState)));
				}
			}
		}

		return states;
	}

	@Override
	public State getNextState(int r, int c) {
		if (getCellState(r, c) != CellState.EMPTY)
			return null;
		List<State> nextStates = getAllNextStates();
		for (State state : nextStates) {
			if (state.getCellState(r, c) != CellState.EMPTY)
				return state;
		}
		return null;
	}
}
