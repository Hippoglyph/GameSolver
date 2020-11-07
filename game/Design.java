package game;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Design {
	private Board board;

	public Design(String name, int windowSizeX, int windowSizeY, int gridSizeX, int gridSizeY) {
		Frame frame = new Frame(name);
		board = new Board(windowSizeX, windowSizeY, gridSizeX, gridSizeY);
		frame.add(board);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
	}

	public Board getBoard() {
		return board;
	}
}
