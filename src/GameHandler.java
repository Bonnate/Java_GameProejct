import java.awt.Font;

import javax.swing.JTextArea;

public class GameHandler {
	private final int SCREEN_WIDTH = 38;
	private final int LEFT_PADDING = 1;
	private final int SCREEN_HEIGHT = 25;
	private final int FIELD_WIDTH = 16, FIELD_HEIGHT = 24;
	private final int BLOCK_SIZE = 4;

	private JTextArea textArea;
	private char[][] buffer;
	private int field[];
	private boolean isGameOver;
	private String blocks[];
	private int currentBlock, nextBlock, currentRotation, currentX, currentY;

	public GameHandler(JTextArea ta) 
	{
		textArea = ta;
		field = new int[FIELD_WIDTH * FIELD_HEIGHT];
		buffer = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
		blocks = new String[7];
		blocks[0] = "..X." + "..X." + "..X." + "..X.";
		blocks[1] = "..X." + ".XX." + ".X.." + "....";
		blocks[2] = ".X.." + ".XX." + "..X." + "....";
		blocks[3] = "...." + ".XX." + ".XX." + "....";
		blocks[4] = "..X." + ".XX." + "..X." + "....";
		blocks[5] = "...." + ".XX." + "..X." + "..X.";
		blocks[6] = "...." + ".XX." + ".X.." + ".X..";
		initData();
	}

	public void initData() {
		for (int x = 0; x < FIELD_WIDTH; x++) // Board Boundary
			for (int y = 0; y < FIELD_HEIGHT; y++)
				field[y * FIELD_WIDTH + x] = (x == 0 || x == FIELD_WIDTH - 1 || y == FIELD_HEIGHT - 1) ? 9 : 0;
		isGameOver = false;
		clearBuffer();

		currentBlock = (int) (Math.random() * 7);
		nextBlock = (int) (Math.random() * 7);
		currentRotation = 0;
		currentX = FIELD_WIDTH / 2 - 2;
		currentY = 0;

	}

	public void gameTiming() { // Game tick
		try {
			Thread.sleep(50);

		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	private void clearBuffer() {
		for (int y = 0; y < SCREEN_HEIGHT; y++) {
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				buffer[x][y] = '.';
			}
		}
	}

	private void drawToBuffer(int px, int py, String c) {
		for (int x = 0; x < c.length(); x++) {
			buffer[px + x + LEFT_PADDING][py] = c.charAt(x);
		}
	}

	private void drawToBuffer(int px, int py, char c) {
		buffer[px + LEFT_PADDING][py] = c;
	}

	public void drawGameOver() {
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void gameLogic() {
	}

	public void drawAll() {
		// draw field
		for (int x = 0; x < FIELD_WIDTH; x++) {
			for (int y = 0; y < FIELD_HEIGHT; y++) {
				// #:경계선, A-G:블록종류, =:블록으로 한줄 완성
				drawToBuffer(x, y, " ABCDEFG=#".charAt(field[y * FIELD_WIDTH + x]));
			}

			for (int px = 0; px < BLOCK_SIZE; px++) {
				for (int py = 0; py < BLOCK_SIZE; py++)

					if (blocks[currentBlock].charAt(rotate(px, py, currentRotation)) == 'X')
						drawToBuffer(currentX + px, currentY + py, (char) (currentBlock + 'A'));
			}

			drawNextBlock();

		}
		drawToBuffer(25, 23, "TETRIS GAME!");
		render();
	}

	private void drawNextBlock() {
		drawToBuffer(FIELD_WIDTH + 2, 5, " NEXT: ");
		drawToBuffer(FIELD_WIDTH + 2, 6, "┌────┐");
		drawToBuffer(FIELD_WIDTH + 2, 11, "└────┘");
		for (int py = 0; py < BLOCK_SIZE; py++) {
			drawToBuffer(FIELD_WIDTH + 2, py + 7, "│    │");
			for (int px = 0; px < BLOCK_SIZE; px++) {
				if (blocks[nextBlock].charAt(rotate(px, py, 0)) == 'X')
					drawToBuffer(px + FIELD_WIDTH + 3, py + 7, (char) (nextBlock + 'A')); // 블록을 A B C D E F G로 나타내기 위해
			}
		}
	}
	
	// 0' i=y*BLOCK_SIZE+x
	// 90' i=12+y-(x*BLOCK_SIZE)
	// 180' i=15-(y*BLOCK_SIZE)-x
	// 270' i=3-y+(x*BLOCK_SIZE)
	private int rotate(int px, int py, int r) {

	    int pi = 0;
	    switch (r % BLOCK_SIZE) {
	        case 0: // 0 degrees // 0 1 2 3
	            pi = py * BLOCK_SIZE + px; // 4 5 6 7 break; // 8 9 10 11
	            // 12 13 14 15
	        case 1: // 90 degrees //12 8 4 0
	            pi = 12 + py - (px * BLOCK_SIZE); // 13 9 5 1 break; // 14 10 6 2
	            // 15 11 7 3
	        case 2: // 180 degrees //15 14 13 12
	            pi = 15 - (py * BLOCK_SIZE) - px; // 11 10 9 8 break; // 7 6 5 4
	            // 3 2 1 0
	        case 3: // 270 degrees // 3 7 11 15
	            pi = 3 - py + (px * BLOCK_SIZE); // 2 6 10 14 break; // 1 5 9 13
	    } // 0 4 8 12
	    return pi;

	}
	

	private void render() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < SCREEN_HEIGHT; y++) {
			for (int x = 0; x < SCREEN_WIDTH; x++) {
				sb.append(buffer[x][y]);
			}
			sb.append("\n");
		}
		textArea.setText(sb.toString());
	}
}
