import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        drawGame(90);
    }

    public static void drawGame(int size) {
        Game chessGame = new Game(size, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); // Starting position FEN
        JFrame frame = new JFrame("Custom Chess");
        JPanel panel = new BoardPanel(size, chessGame.getPieces(), chessGame.getChessPieceImgs(), chessGame);
        panel.setPreferredSize(new Dimension(size * 8, size * 8));
        frame.add(panel);
        frame.pack();
        frame.setLocation(10, 10);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (chessGame.getEngine() != null) {
                    chessGame.getEngine().close();
                }
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
}
