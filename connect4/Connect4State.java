package connect4;

import java.util.List;

import solver.State;

public class Connect4State extends State {

	public static final int rowSize = 6;
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
		// Row
		for (int r = 0; r < rowSize(); r++) {
			int circleCount = 0;
			int crossCount = 0;
			for (int c = 0; c < colSize(); c++) {
				switch (getCellState(r, c)) {
				case EMPTY:
					circleCount = 0;
					crossCount = 0;
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
			if (circleCount >= victoryCount)
				return VictoryState.PLAYER2;
			if (crossCount >= victoryCount)
				return VictoryState.PLAYER1;
		}

		// TODO Col
		// TODO Diag
		// TODO Filled
		return null;
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
