package connect4;

import java.util.ArrayList;
import java.util.List;

import game.Board.CellState;
import solver.State;

public class Connect4State extends State {

	public static final int rowSize = 3;
	public static final int colSize = 7;

	private static final int victoryCount = 4;

	public Connect4State(long encodedState) {
		super(encodedState);
	}

	@Override
	public String getGameName() {
		return "Connect 4";
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

		for (int r = 0; r < rowSize(); r++) {
			int player2Count = 0;
			int player1Count = 0;
			for (int c = 0; c < colSize(); c++) {
				switch (getCellState(r, c)) {
				case EMPTY:
					player2Count = 0;
					player1Count = 0;
					emptyCount++;
					break;
				case PLAYER2:
					player2Count++;
					break;
				case PLAYER1:
					player1Count++;
					break;
				}
			}
			if (player2Count >= victoryCount)
				return VictoryState.PLAYER2;
			if (player1Count >= victoryCount)
				return VictoryState.PLAYER1;
		}
		// Column
		for (int c = 0; c < colSize(); c++) {
			int player2Count = 0;
			int player1Count = 0;
			for (int r = 0; r < rowSize(); r++) {
				switch (getCellState(r, c)) {
				case EMPTY:
					player2Count = 0;
					player1Count = 0;
					break;
				case PLAYER2:
					player2Count++;
					break;
				case PLAYER1:
					player1Count++;
					break;
				}
			}
			if (player2Count >= victoryCount)
				return VictoryState.PLAYER2;
			if (player1Count >= victoryCount)
				return VictoryState.PLAYER1;
		}

		// Diag Main
		for (int k = 0; k <= colSize() + rowSize() - 2; k++) {
			int player2Count = 0;
			int player1Count = 0;
			for (int j = 0; j <= k; j++) {
				int i = k - j;
				if (i < rowSize() && j < colSize()) {
					switch (getCellState(i, j)) {
					case EMPTY:
						player2Count = 0;
						player1Count = 0;
						break;
					case PLAYER2:
						player2Count++;
						break;
					case PLAYER1:
						player1Count++;
						break;
					}
					if (player2Count >= victoryCount)
						return VictoryState.PLAYER2;
					if (player1Count >= victoryCount)
						return VictoryState.PLAYER1;
				}
			}
		}

		// Diag Secondary
		for (int k = 0; k <= colSize() + rowSize() - 2; k++) {
			int player2Count = 0;
			int player1Count = 0;
			for (int j = 0; j <= k; j++) {
				int i = k - j;
				if (i < rowSize() && j < colSize()) {
					switch (getCellState(i, colSize() - 1 - j)) {
					case EMPTY:
						player2Count = 0;
						player1Count = 0;
						break;
					case PLAYER2:
						player2Count++;
						break;
					case PLAYER1:
						player1Count++;
						break;
					}
					if (player2Count >= victoryCount)
						return VictoryState.PLAYER2;
					if (player1Count >= victoryCount)
						return VictoryState.PLAYER1;
				}
			}
		}
		if (emptyCount <= 0)
			return VictoryState.DRAW;

		return VictoryState.UNDECIDED;
	}

	@Override
	public List<State> getAllNextStates() {
		List<State> states = new ArrayList<>();

		if (getVictoryState() != VictoryState.UNDECIDED)
			return states;

		int piece = nextTurn() == PlayerTurn.PLAYER1 ? PLAYER1 : PLAYER2;

		for (int c = 0; c < colSize(); c++) {
			for (int r = 0; r < rowSize(); r++) {
				if (getCellState(r, c) == CellState.EMPTY
						&& ((r == rowSize() - 1) || getCellState(r + 1, c) != CellState.EMPTY)) {
					int[][] newState = getCopyOfState();
					newState[r][c] = piece;
					states.add(new Connect4State(getEncodedState(newState)));
				}
			}
		}

		return states;
	}

	@Override
	public State getNextState(int row, int col) {
		for (int r = 0; r < rowSize(); r++) {
			if (getCellState(r, col) == CellState.EMPTY
					&& ((r == rowSize() - 1) || getCellState(r + 1, col) != CellState.EMPTY)) {
				List<State> nextStates = getAllNextStates();
				for (State state : nextStates) {
					if (state.getCellState(r, col) != CellState.EMPTY)
						return state;
				}
			}
		}
		return null;
	}

}
