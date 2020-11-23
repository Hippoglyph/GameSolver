package connect4;

import java.util.List;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public State getNextState(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

}
