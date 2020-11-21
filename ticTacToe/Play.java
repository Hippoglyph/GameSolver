package ticTacToe;

import game.Game;

public class Play {
	public static void main(String[] args) {
		new Game(Boolean.valueOf(args[0]), new TicTacToeState(0), 450, 450);
	}
}
