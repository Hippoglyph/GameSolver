package ticTacToe;

import java.util.ArrayList;
import java.util.List;

import game.Board.CellState;
import solver.State;

public class TicTacToeState extends State {

	public static final int SIZE = 3;

	public TicTacToeState(long encodedState) {
		super(encodedState);
	}

	@Override
	public String getGameName() {
		return "Tic Tac Toe " + SIZE;
	}

	@Override
	public int rowSize() {
		return SIZE;
	}

	@Override
	public int colSize() {
		return SIZE;
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
			if (circleCount >= SIZE)
				return VictoryState.PLAYER2;
			if (crossCount >= SIZE)
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
			if (circleCount >= SIZE)
				return VictoryState.PLAYER2;
			if (crossCount >= SIZE)
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

		if (circleCount >= SIZE || circleCountMain >= SIZE)
			return VictoryState.PLAYER2;
		if (crossCount >= SIZE || crossCountMain >= SIZE)
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

		int piece = nextTurn() == PlayerTurn.PLAYER1 ? PLAYER1 : PLAYER2;

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
