import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

public class GameHandler {
	private final int SCREEN_WIDTH = 38;
	private final int LEFT_PADDING = 1;
	private final int SCREEN_HEIGHT = 25;
	private final int FIELD_WIDTH = 16, FIELD_HEIGHT = 24;
	private final int BLOCK_SIZE = 4;

	private final int INIT_SPEED = 20;
	private final int MAX_SPEED = 10;

	private final int BLOCK_REWARD = 25;
	private final int LINE_REWARD = 100;

	private JTextArea textArea;
	private char[][] buffer;
	private int field[];
	private boolean isGameOver;
	private String blocks[];
	private int currentBlock, nextBlock, currentRotation, currentX, currentY;

	private int score, topScore;

	private int speed;
	private int speedCounter;
	private boolean forceDown;
	private int blockCount;

	private int lines[];

	public GameHandler(JTextArea ta) {
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

		field = new int[FIELD_WIDTH * FIELD_HEIGHT];
		lines = new int[FIELD_HEIGHT];
		buffer = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
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

		speed = INIT_SPEED;
		speedCounter = 0;
		forceDown = false;
		blockCount = 0;

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
		drawToBuffer(7, 9, "╔═══════════════════════╗");
		drawToBuffer(7, 10,"║       GAME OVER       ║");
		drawToBuffer(7, 11,"║                       ║");
		drawToBuffer(7, 12,"║   PLAY AGAIN? (Y/N)   ║");
		drawToBuffer(7, 13,"╚═══════════════════════╝");
		render();
		render();
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter("topscore.txt"));
			out.write(String.valueOf(topScore));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public void gameLogic() {

		System.out.println(forceDown);

		if (forceDown) {
			if (doesBlockFit(currentBlock, currentRotation, currentX, currentY + 1))
				currentY++;
			else {
				// If it can't be moved down, Lock the current block in the field
				for (int px = 0; px < BLOCK_SIZE; px++)
					for (int py = 0; py < BLOCK_SIZE; py++)
						if (blocks[currentBlock].charAt(rotate(px, py, currentRotation)) != '.')
							field[(currentY + py) * FIELD_WIDTH + (currentX + px)] = currentBlock + 1; // field에서 0은 빈칸을
																										// // 의미하므로 1을
																										// 더해줌
				blockCount++;
				if (blockCount % 10 == 0)
					if (speed >= MAX_SPEED)
						speed--;

				// Check have we got any lines
				for (int py = 0; py < BLOCK_SIZE; py++) {
					if (currentY + py < FIELD_HEIGHT - 1) {
						boolean isLine = true;
						for (int px = 1; px < FIELD_WIDTH - 1; px++) // px=0은 경계이므로 확인하지 않음
							isLine &= (field[(currentY + py) * FIELD_WIDTH + px]) != 0;
						if (isLine) {
							// Remove Line, set to =
							for (int px = 1; px < FIELD_WIDTH - 1; px++)
								field[(currentY + py) * FIELD_WIDTH + px] = 8;
							addLines(currentY + py);
						}
					}
				}
				score += BLOCK_REWARD;
				if (lines[0] != -1) {
					for (int i = 1; i < FIELD_HEIGHT; i++) {
						if (lines[i] == -1) {
							score += (1 << i) * LINE_REWARD;
							break;
						}
					}
				}
				if (topScore < score)
					topScore = score;
				// Choose next block
				currentBlock = nextBlock;
				nextBlock = (int) (Math.random() * 7);
				currentRotation = 0;
				currentX = FIELD_WIDTH / 2 - 2;
				currentY = 0;

				isGameOver = !doesBlockFit(currentBlock, currentRotation, currentX, currentY);
			}

			speedCounter = 0;
		}
	}

	public void drawAll() {
		// draw field
		for (int x = 0; x < FIELD_WIDTH; x++) {
			for (int y = 0; y < FIELD_HEIGHT; y++) {
				// #:경계선, A-G:블록종류, =:블록으로 한줄 완성
				drawToBuffer(x, y, " ABCDEFG=#".charAt(field[y * FIELD_WIDTH + x]));
			}
		}
		// draw current block
		for (int px = 0; px < BLOCK_SIZE; px++) {
			for (int py = 0; py < BLOCK_SIZE; py++)
				if (blocks[currentBlock].charAt(rotate(px, py, currentRotation)) == 'X')
				{
					System.out.println(currentRotation);
					drawToBuffer(currentX + px, currentY + py, (char) (currentBlock + 'A')); // 블록을 A B C D E F G로 나타내기
				}																				// // 위해
		}
		drawScore();
		drawTopScore();
		drawNextBlock();
		// Animate Line Completion
		if (lines[0] != -1) {
			// Display Frame (to draw = lines)
			render();

			System.out.print("A");
			try {
				Thread.sleep(400);
			} catch (InterruptedException ex) {
				System.err.print(ex);
			}
			for (int line : lines)
				for (int px = 1; px < FIELD_WIDTH - 1; px++) {
					for (int py = line; py > 0; py--)
						field[py * FIELD_WIDTH + px] = field[(py - 1) * FIELD_WIDTH + px];
					field[px] = 0;

				}
			clearLines();
		}
		drawToBuffer(25, 23, " by D.Lee");
		render();
	}

	private void drawScore() {
		drawToBuffer(FIELD_WIDTH + 2, 1, "┌───────────────┐");
		drawToBuffer(FIELD_WIDTH + 2, 2, "│               │");
		drawToBuffer(FIELD_WIDTH + 2, 3, "└───────────────┘");
		drawToBuffer(FIELD_WIDTH + 4, 2, "SCORE: " + score);
	}

	private void drawTopScore() {
		drawToBuffer(FIELD_WIDTH + 2, 15, "┌────────────────┐");
		drawToBuffer(FIELD_WIDTH + 2, 16, "│                │");
		drawToBuffer(FIELD_WIDTH + 2, 17, "└────────────────┘");
		drawToBuffer(FIELD_WIDTH + 3, 16, "TOP SCORE: " + topScore);
	}

	private void drawNextBlock() {
		drawToBuffer(FIELD_WIDTH + 2, 5, " NEXT: ");
		drawToBuffer(FIELD_WIDTH + 2, 6, "┌────┐");
		drawToBuffer(FIELD_WIDTH + 2, 11,"└────┘");
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
			break;
			// 12 13 14 15
		case 1: // 90 degrees //12 8 4 0
			pi = 12 + py - (px * BLOCK_SIZE); // 13 9 5 1 break; // 14 10 6 2
			break;
			// 15 11 7 3
		case 2: // 180 degrees //15 14 13 12
			pi = 15 - (py * BLOCK_SIZE) - px; // 11 10 9 8 break; // 7 6 5 4
			break;
			// 3 2 1 0
		case 3: // 270 degrees // 3 7 11 15
			pi = 3 - py + (px * BLOCK_SIZE); // 2 6 10 14 break; // 1 5 9 13
			break;
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

	// posX, posY: 맨 위 왼쪽 점
	private boolean doesBlockFit(int blockIndex, int rotation, int posX, int posY) {
		// All Field cells >0 are occupied
		for (int px = 0; px < BLOCK_SIZE; px++)
			for (int py = 0; py < BLOCK_SIZE; py++) { // Get index into block
				int pi = rotate(px, py, rotation);
				// Get index into field
				int fi = (posY + py) * FIELD_WIDTH + (posX + px);
				// Check that test is in bounds. Note out of bounds does
				// not necessarily mean a fail, as the long vertical block // can have cells
				// that lie outside the boundary, so we'll // just ignore them
				if (posX + px >= 0 && posX + px < FIELD_WIDTH) {
					if (posY + py >= 0 && posY + py < FIELD_HEIGHT) {
						// In Bounds so do collision check
						if (blocks[blockIndex].charAt(pi) == 'X' && field[fi] != 0)
							return false; // fail on first hit
					}
				}
			}
		return true;
	}

	public void gameTiming() { // Game tick
		try {
			Thread.sleep(50);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		forceDown = (speedCounter++ == speed);
	}

	private void addLines(int index) {
		for (int i = 0; i < FIELD_HEIGHT; i++) {
			if (lines[i] == -1) {
				lines[i] = index;
				break;
			}
		}
	}


	private void clearLines() {
		for (int i = 0; i < FIELD_HEIGHT; i++) {
			lines[i] = -1;
		}
	}

	public void moveLeftBlock() {
		currentX -= doesBlockFit(currentBlock, currentRotation, currentX - 1, currentY) ? 1 : 0;
	}

	public void moveRightBlock() {
		currentX += doesBlockFit(currentBlock, currentRotation, currentX + 1, currentY) ? 1 : 0;
	}

	public void moveDownBlock() {
		currentY += doesBlockFit(currentBlock, currentRotation, currentX, currentY + 1) ? 1 : 0;
	}
	
	public void moveForwardDown() {
		
		boolean isForward = false;
		while(true)
		{
			isForward = doesBlockFit(currentBlock, currentRotation, currentX, currentY + 1) ? true : false;
			
			if(isForward)
			{
				++currentY;
			}
			else
			{
				forceDown = false;
				return;
				
			}
		}
		
	}

	public void rotateBlock() {
		currentRotation += doesBlockFit(currentBlock, currentRotation + 1, currentX, currentY) ? 1 : 0;
	}

}
