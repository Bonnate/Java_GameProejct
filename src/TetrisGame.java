import javax.swing.JFrame;
import javax.swing.JTextArea;

public class TetrisGame extends JFrame {
    private GameHandler handler;
    private JTextArea textArea = new JTextArea();


    public TetrisGame() {
        setTitle("Let's play Tetris");
        setSize(720, 920);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // this will center your app textArea.setFont(new Font("Courier New", Font.PLAIN, 30)); add(textArea);
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
            while (!handler.isGameOver()) {
                // 1. Game timing ================================ handler.gameTiming();
                // 3. Game logic ==================================
                handler.gameLogic();
                // 4. Render output ==============================
                handler.drawAll();
            }
            //game over
            handler.drawGameOver();
        }
    }
    // 2. Get Input ======================================
}
