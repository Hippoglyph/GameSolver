package design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import solver.State;

public class Board extends JPanel {
	private static final long serialVersionUID = 1L;

	@FunctionalInterface
	public interface MouseClickedListener {
		void clicked(int row, int col);
	}

	public enum CellState {
		EMPTY, PLAYER1, PLAYER2
	}

	private final int windowSizeX;
	private final int rowSize;
	private final int windowSizeY;
	private final int colSize;
	private final int lineStepRow;
	private final int lineStepCol;

	private static final Color background = Color.BLACK;
	private static final int lineWidth = 5;
	private static final Color lineColor = Color.GRAY;
	private static final double playerMargin = 0.75;

	private List<MouseClickedListener> mouseClickedListener = new ArrayList<>();
	private State currentState;
	private int rowPlayerOffset;
	private int colPlayerOffset;

	public Board(int windowSizeX, int windowSizeY, int rowSize, int colSize) {

		this.windowSizeX = windowSizeX;
		this.rowSize = rowSize;
		this.lineStepRow = windowSizeY / rowSize;
		this.windowSizeY = windowSizeY;
		this.colSize = colSize;
		this.lineStepCol = windowSizeX / colSize;

		rowPlayerOffset = (int) (lineStepRow * playerMargin / 2);
		colPlayerOffset = (int) (lineStepCol * playerMargin / 2);

		setPreferredSize(new Dimension(windowSizeX, windowSizeY));
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					int row = (int) (((double) me.getY() / windowSizeY) * rowSize);
					int col = (int) (((double) me.getX() / windowSizeX) * colSize);
					mouseClickedListener.forEach(l -> l.clicked(row, col));
				}

			}
		});
	}

	public void registerGridLocationListener(MouseClickedListener listener) {
		if (listener != null)
			mouseClickedListener.add(listener);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		drawBackground(g2d);
		g2d.setStroke(new BasicStroke((float) (lineWidth * playerMargin)));
		if (currentState != null) {
			for (int r = 0; r < rowSize; r++) {
				for (int c = 0; c < colSize; c++) {
					draw(g2d, currentState.getCellState(r, c), c * lineStepCol + lineStepCol / 2,
							r * lineStepRow + lineStepRow / 2);
				}
			}
		}
	}

	private void draw(Graphics g, CellState cellState, int xCenter, int yCenter) {
		if (cellState == null)
			cellState = CellState.EMPTY;

		switch (cellState) {
		case PLAYER2:
			g.drawOval(xCenter - colPlayerOffset, yCenter - rowPlayerOffset, colPlayerOffset * 2, rowPlayerOffset * 2);
			break;
		case PLAYER1:
			g.drawLine(xCenter + colPlayerOffset, yCenter - rowPlayerOffset, xCenter - colPlayerOffset,
					yCenter + rowPlayerOffset);
			g.drawLine(xCenter - colPlayerOffset, yCenter - rowPlayerOffset, xCenter + colPlayerOffset,
					yCenter + rowPlayerOffset);
			break;
		case EMPTY:
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + cellState);
		}
	}

	private void drawBackground(Graphics g) {
		setBackground(background);
		g.setColor(lineColor);

		for (int i = 1; i < rowSize; i++)
			g.fillRect(0, i * lineStepRow, windowSizeY, lineWidth);

		for (int i = 1; i < colSize; i++)
			g.fillRect(i * lineStepCol, 0, lineWidth, windowSizeX);
	}

	public void draw(State state) {
		if (state != null) {
			this.currentState = state;
			repaint();
		}
	}
}
