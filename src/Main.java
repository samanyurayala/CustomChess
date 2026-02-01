import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        drawGame(90);
    }

    public static void drawGame(int size) throws IOException {
        Game chessGame = new Game(size);
        JFrame frame = new JFrame("Custom Chess");
        JPanel panel = new BoardPanel(size, chessGame.getPieces(), chessGame.getChessPieceImgs(), chessGame);
        panel.setPreferredSize(new Dimension(size * 8, size * 8));
        frame.add(panel);
        frame.pack();
        frame.setLocation(10, 10);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
