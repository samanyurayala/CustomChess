import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    private final Map<Class<? extends BoardPiece>, Integer> SPRITES = Map.of(
            King.class, 0,
            Queen.class, 1,
            Bishop.class, 2,
            Knight.class, 3,
            Rook.class, 4,
            Pawn.class, 5
    );
    private final Color LIGHT_COLOR = new Color(0xEAE9D2);
    private final Color DARK_COLOR = new Color(0x4B7399);
    private final int SIZE;
    private ArrayList<BoardPiece> pieces;
    private Image[] chess_pieces;
    private Game game;

    public BoardPanel(int size, ArrayList<BoardPiece> pieces, Image[] chess_pieces, Game chessGame) throws IOException {
        this.SIZE = size;
        this.pieces = pieces;
        this.chess_pieces = chess_pieces;
        this.game = chessGame;
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? LIGHT_COLOR : DARK_COLOR);
                g.fillRect(col * SIZE, row * SIZE, SIZE, SIZE);
            }
        }
        for (BoardPiece piece: pieces) {
            int index = SPRITES.get(piece.getClass());
            if (!piece.isWhite()) index += 6;
            g.drawImage(chess_pieces[index], piece.getX(), piece.getY(), this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        game.selectPiece(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        game.dropPiece(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        game.movePiece(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
