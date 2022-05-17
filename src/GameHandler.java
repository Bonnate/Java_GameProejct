import javax.swing.JTextArea;

public class GameHandler {
    private final int SCREEN_WIDTH = 38;
    private final int LEFT_PADDING = 1;
    private final int SCREEN_HEIGHT = 25;
    private final int FIELD_WIDTH = 16, FIELD_HEIGHT = 24;
    private JTextArea textArea;
    private char[][] buffer;
    private int field[];
    private boolean isGameOver;
    public GameHandler(JTextArea ta) {
        textArea = ta;

        field = new int[FIELD_WIDTH * FIELD_HEIGHT];
        buffer = new char[SCREEN_WIDTH][SCREEN_HEIGHT];
        initData();
    }
    public void initData() {
        for (int x = 0; x < FIELD_WIDTH; x++) // Board Boundary
            for (int y = 0; y < FIELD_HEIGHT; y++)
                field[y * FIELD_WIDTH + x] = (x == 0 || x == FIELD_WIDTH - 1 || y == FIELD_HEIGHT - 1) ? 9 : 0;
        isGameOver = false;
        clearBuffer();
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
    public void drawGameOver() {}
    public boolean isGameOver() {
        return isGameOver;
    }
    public void gameLogic() {}
    public void drawAll() {
        // draw field
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                // #:경계선, A-G:블록종류, =:블록으로 한줄 완성
                drawToBuffer(x, y, " ABCDEFG=#".charAt(field[y * FIELD_WIDTH + x]));
            }
        }
        drawToBuffer(25, 23, " by D.Lee");
        render();
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
