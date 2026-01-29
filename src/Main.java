import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        drawGame(80);
    }

    public static void drawGame(int size) throws IOException {
        Game chessGame = new Game(size);
        JFrame frame = new JFrame("Custom Chess");
        frame.setBounds(10, 10, size * 8, size * 8 + 28);
        JPanel panel = new BoardPanel(size, chessGame.getPieces(), chessGame.getChessPieceImgs(), chessGame);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
