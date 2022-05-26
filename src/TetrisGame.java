import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
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
		textArea.setEditable(false);
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
			// game loop
			while (!handler.isGameOver()) {
				handler.gameTiming();
				handler.gameLogic();
				handler.drawAll();
			}

			handler.drawGameOver();
		}
	}
	
	
	public void restart() {
	     handler.initData();
	     new Thread(new GameThread()).start();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		case KeyEvent.VK_D: // Right key pressed
			handler.moveRightBlock();
			break;
		case KeyEvent.VK_A: // Left key pressed
			handler.moveLeftBlock();
			break;
		case KeyEvent.VK_S: // Down key pressed
			handler.moveDownBlock();
			break;
		case KeyEvent.VK_W: // Up key pressed
			handler.rotateBlock();
			break;
		case KeyEvent.VK_Y: // Y key pressed if (handler.isGameOver())
			restart();
			break;
		case KeyEvent.VK_N: // N key pressed if (handler.isGameOver())
			System.exit(0);
			break;
		case KeyEvent.VK_SPACE: // Right key pressed
			handler.moveForwardDown();
			break;

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
