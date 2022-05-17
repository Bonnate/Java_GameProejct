import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class TetrisGame extends JFrame implements KeyListener {
	private GameHandler handler;
	private JTextArea textArea = new JTextArea();

	public TetrisGame() {
		setTitle("Let's play Tetris");
		setSize(720, 920);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // this will center your app textArea.setFont(new Font("Courier New",
										// Font.PLAIN, 30)); add(textArea);
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", Font.PLAIN, 26));
		textArea.addKeyListener(this);
		add(textArea);
		setVisible(true);

		handler = new GameHandler(textArea);
		new Thread(new GameThread()).start();
	}

	public static void main(String[] args) {
		new TetrisGame();
	}

	class GameThread implements Runnable {
		@Override
		public void run() {
			while (!handler.isGameOver()) {
				// 1. Game timing ================================ handler.gameTiming();
				// 3. Game logic ==================================
				handler.gameLogic();
				// 4. Render output ==============================
				handler.drawAll();
			}
			// game over
			handler.drawGameOver();
		}
	}
	// 2. Get Input ======================================

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT: // Right key pressed handler.moveRightBlock();
			break;
		case KeyEvent.VK_LEFT: // Left key pressed handler.moveLeftBlock();
			break;
		case KeyEvent.VK_DOWN: // Down key pressed handler.moveDownBlock();
			break;
		case KeyEvent.VK_UP: // Up key pressed handler.rotateBlock();
			break;
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
