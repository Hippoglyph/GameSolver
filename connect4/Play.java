package connect4;

import game.Game;

public class Play {
	public static void main(String[] args) {
		new Game(Boolean.valueOf(args[0]), new Connect4State(0L), 450, 450);
	}
}
