package connect4;

import java.util.List;

import game.Board.CellState;
import solver.State;

public class Connect4State extends State {

	public static final int rowSize = 6;
	public static final int colSize = 7;

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CellState getCellState(int row, int col) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VictoryState getVictoryState() {
		// TODO Auto-generated method stub
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
