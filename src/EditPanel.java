import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class EditPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    private final Map<Class<? extends BoardPiece>, Integer> SPRITES = Map.of(
            King.class, 0,
            Queen.class, 1,
            Bishop.class, 2,
            Knight.class, 3,
            Rook.class, 4,
            Pawn.class, 5
    );
    private final Map<Class<? extends BoardPiece>, Character> CHAR_MAP = Map.of(
            Queen.class, 'q',
            Rook.class, 'r',
            Bishop.class, 'b',
            Knight.class, 'n',
            Pawn.class, 'p',
            King.class, 'k'
    );
    private final Color LIGHT_COLOR = new Color(0xEAE9D2);
    private final Color DARK_COLOR = new Color(0x4B7399);
    private final Color MEDIUM_COLOR = new Color(0x8EB2C2);
    private final int SIZE;
    private ArrayList<BoardPiece> pieces;
    private Image[] chess_pieces;
    private BoardPiece selectedPiece;
    private boolean isWhiteTurn;
    private boolean[] whiteCanCastle;
    private boolean[] blackCanCastle;

    public EditPanel(int size, ArrayList<BoardPiece> pieces, Image[] chess_pieces, Game chessGame) {
        setFocusable(true);
        this.SIZE = size;
        this.pieces = pieces;
        this.chess_pieces = Arrays.copyOfRange(chess_pieces, 12, 24);
        isWhiteTurn = true;
        addMouseListener(this);
        addMouseMotionListener(this);
        whiteCanCastle = new boolean[]{false, false};
        blackCanCastle = new boolean[]{false, false};
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
        g.setColor(MEDIUM_COLOR);
        g.fillRect(SIZE * 8, 0, getWidth() - SIZE * 8, getHeight());
        for (BoardPiece piece: pieces) {
            int index = SPRITES.get(piece.getClass());
            if (!piece.isWhite()) index += 6;
            g.drawImage(chess_pieces[index], piece.getX(), piece.getY(), this);
        }
        for (int i = 0; i < 6; i++) {
            g.drawImage(chess_pieces[i], SIZE * 8, SIZE * i, this);
        }
        for (int i = 6; i < 12; i++) {
            g.drawImage(chess_pieces[i], SIZE * 9, SIZE * (i - 6), this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        boolean isWhite;
        if (e.getX() >= SIZE * 8 && e.getX() < SIZE * 9) isWhite = true;
        else if (e.getX() >= SIZE * 9 && e.getX() < SIZE * 10) isWhite = false;
        else if (e.getX() / SIZE >= Game.LEFT_FILE && e.getX() / SIZE <= Game.RIGHT_FILE && e.getY() / SIZE >= Game.TOP_RANK && e.getY() / SIZE <= Game.BOTTOM_RANK) {
            selectedPiece = getPiece(e.getX() / SIZE, e.getY() / SIZE);
            return;
        } else {
            selectedPiece = null;
            return;
        }
        if (e.getY() >= 0 && e.getY() < SIZE) selectedPiece = new King(-1, -1, isWhite, SIZE);
        else if (e.getY() >= SIZE && e.getY() < SIZE * 2) selectedPiece = new Queen(-1, -1, isWhite, SIZE);
        else if (e.getY() >= SIZE * 2 && e.getY() < SIZE * 3) selectedPiece = new Bishop(-1, -1, isWhite, SIZE);
        else if (e.getY() >= SIZE * 3 && e.getY() < SIZE * 4) selectedPiece = new Knight(-1, -1, isWhite, SIZE);
        else if (e.getY() >= SIZE * 4 && e.getY() < SIZE * 5) selectedPiece = new Rook(-1, -1, isWhite, SIZE);
        else if (e.getY() >= SIZE * 5 && e.getY() < SIZE * 6) selectedPiece = new Pawn(-1, -1, isWhite, SIZE);

        pieces.add(selectedPiece);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selectedPiece == null) return;
        if (e.getX() > SIZE * Game.BOARD_SIZE || e.getX() < 0 || e.getY() > SIZE * Game.BOARD_SIZE || e.getY() < 0) {
            pieces.remove(selectedPiece);
            selectedPiece = null;
            repaint();
            return;
        }
        selectedPiece.setXPos(e.getX() / SIZE);
        selectedPiece.setX(selectedPiece.getXPos() * SIZE);
        selectedPiece.setYPos(e.getY() / SIZE);
        selectedPiece.setY(selectedPiece.getYPos() * SIZE);
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
        if (selectedPiece != null) {
            selectedPiece.setX(e.getX() - SIZE / 2);
            selectedPiece.setY(e.getY() - SIZE / 2);
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public String readFenFromPosition(ArrayList<BoardPiece> pieces) {
        if (!isValidPosition()) return "Not valid position";
        StringBuilder fen = new StringBuilder();
        for (int currentRank = 0; currentRank < Game.BOARD_SIZE; currentRank++) {
            int counter = 0;
            for (int currentFile = 0; currentFile < Game.BOARD_SIZE; currentFile++) {
                BoardPiece piece = getPiece(currentFile, currentRank);
                if (piece != null) {
                    if (counter != 0) fen.append(counter);
                    char pieceClass = CHAR_MAP.get(piece.getClass());
                    if (piece.isWhite()) pieceClass = Character.toUpperCase(pieceClass);
                    fen.append(pieceClass);
                    counter = 0;
                } else {
                    counter++;
                }
            }
            if (counter != 0) fen.append(counter);
            fen.append("/");
        }
        fen.deleteCharAt(fen.length() - 1);
        char isWhiteMove = isWhiteTurn ? 'w' : 'b';
        fen.append(" ").append(isWhiteMove).append(" ");
        if (whiteCanCastle[1]) fen.append("K");
        if (whiteCanCastle[0]) fen.append("Q");
        if (blackCanCastle[1]) fen.append("k");
        if (blackCanCastle[0]) fen.append("q");
        if (!whiteCanCastle[1] && !whiteCanCastle[0] && !blackCanCastle[1] && !blackCanCastle[0]) fen.append("-");
        fen.append(" - 0 1");
        return fen.toString();
    }

    public BoardPiece getPiece(int xPos, int yPos) {
        for (BoardPiece piece: pieces) {
            if (piece.getXPos() == xPos && piece.getYPos() == yPos) return piece;
        }
        return null;
    }

    public String onTurnButtonPressed() {
        isWhiteTurn = !isWhiteTurn;
        String whosMove = isWhiteTurn ? "White" : "Black";
        return whosMove + " to move";
    }

    public String getFen() {
        return readFenFromPosition(pieces);
    }

    public boolean isValidPosition() {
        int whiteKingCounter = 0;
        int blackKingCounter = 0;
        for (BoardPiece piece: pieces) {
            if (piece instanceof King) {
                if (piece.isWhite()) whiteKingCounter++;
                else blackKingCounter++;
            }
        }
        return whiteKingCounter == 1 && blackKingCounter == 1;
    }

    public boolean[] getWhiteCanCastle() {
        return whiteCanCastle;
    }

    public boolean[] getBlackCanCastle() {
        return blackCanCastle;
    }
}
