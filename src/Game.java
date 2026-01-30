import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Game {
    private BoardPiece selectedPiece = null;
    private ArrayList<BoardPiece> pieces;
    private boolean isWhiteTurn;
    private Image[] chessPieceImgs;
    private final int SIZE;
    public static final int BOARD_SIZE = 8;
    public static final int LEFT_FILE = 0, TOP_RANK = 0;
    public static final int RIGHT_FILE = 7, BOTTOM_RANK = 7;


    public Game(int size) throws IOException {
        chessPieceImgs = readChessPieces(size);
        pieces = initChessPieces(size);
        isWhiteTurn = true;
        this.SIZE = size;
    }

    public void movePiece(int x, int y) {
        if (selectedPiece != null) {
            selectedPiece.setX(x - SIZE / 2);
            selectedPiece.setY(y - SIZE / 2);
        }
    }

    public void selectPiece(int x, int y) {
        selectedPiece = getPieceXY(x, y);
    }

    public void dropPiece(int x, int y) {
        if (selectedPiece == null) return;
        move(x / SIZE, y / SIZE, selectedPiece);
    }

    public void move(int xPos, int yPos, BoardPiece piece) {
        int oldXPos = piece.getXPos();
        BoardPiece piece2 = getPieceXPosYPos(xPos, yPos);
        ArrayList<Vector2d> legalSquares = piece.getLegalMoves(this);
        Vector2d testVector = new Vector2d(xPos, yPos);
        if (!legalSquares.contains(testVector) || !(isWhiteTurn == piece.isWhite())) {
            piece.setX(piece.getXPos() * SIZE);
            piece.setY(piece.getYPos() * SIZE);
            System.out.println(isWhiteTurn);
            return;
        }
        if (piece2 != null && piece2.isWhite() != piece.isWhite()) {
            kill(piece2);
        }
        piece.setXPos(xPos);
        piece.setYPos(yPos);
        piece.setX(xPos * SIZE);
        piece.setY(yPos * SIZE);
        if (!piece.hasMoved()) piece.setHasMoved(true);
        if (piece instanceof King) {
            if (xPos - oldXPos == 2) { /* kingside castling distance */
                BoardPiece rook = getPieceXPosYPos(7, piece.getYPos()); /* kingside rook */
                rook.setXPos(5);
                rook.setX(5 * SIZE);
            } else if (xPos - oldXPos == -2 ) { /* queenside castling distance */
                BoardPiece rook = getPieceXPosYPos(0, piece.getYPos()); /* queenside rook */
                rook.setXPos(3);
                rook.setX(3 * SIZE);
            }
        }
        isWhiteTurn = !isWhiteTurn;
        System.out.println(isWhiteTurn);
    }

    public ArrayList<BoardPiece> getPiece(Class<?> test, boolean isWhite) {
        ArrayList<BoardPiece> newPieces = new ArrayList<>();
        for (BoardPiece piece: pieces) {
            if (test.isInstance(piece) && piece.isWhite() == isWhite) newPieces.add(piece);
        }
        return newPieces;
    }

    public BoardPiece getPieceXY(int x, int y) {
        int xPos = x / SIZE;
        int yPos = y / SIZE;
        for (BoardPiece piece: pieces) {
            if (piece.getXPos() == xPos && piece.getYPos() == yPos) {
                return piece;
            }
        }
        return null;
    }

    public BoardPiece getPieceXPosYPos(int xPos, int yPos) {
        for (BoardPiece piece: pieces) {
            if (piece.getXPos() == xPos && piece.getYPos() == yPos) {
                return piece;
            }
        }
        return null;
    }

    public BoardPiece getPieceVec2D(Vector2d vector2d) {;
        for (BoardPiece piece: pieces) {
            if (piece.getXPos() == vector2d.x && piece.getYPos() == vector2d.y) {
                return piece;
            }
        }
        return null;
    }

    public void kill(BoardPiece piece) {
        pieces.remove(piece);
    }

    public ArrayList<BoardPiece> initChessPieces(int size) {
        ArrayList<BoardPiece> pieces = new ArrayList<>();
        pieces.add(new Rook(0, 0,false, size));
        pieces.add(new Knight(1, 0,false, size));
        pieces.add(new Bishop(2, 0,false, size));
        pieces.add(new Queen(3, 0,false, size));
        pieces.add(new King(4, 0,false, size));
        pieces.add(new Bishop(5, 0,false, size));
        pieces.add(new Knight(6, 0,false, size));
        pieces.add(new Rook(7, 0,false, size));
        pieces.add(new Pawn(0, 1,false, size));
        pieces.add(new Pawn(1, 1,false, size));
        pieces.add(new Pawn(2, 1,false, size));
        pieces.add(new Pawn(3, 1,false, size));
        pieces.add(new Pawn(4, 1,false, size));
        pieces.add(new Pawn(5, 1,false, size));
        pieces.add(new Pawn(6, 1,false, size));
        pieces.add(new Pawn(7, 1,false, size));
        pieces.add(new Rook(0, 7,true, size));
        pieces.add(new Knight(1, 7,true, size));
        pieces.add(new Bishop(2, 7,true, size));
        pieces.add(new Queen(3, 7,true, size));
        pieces.add(new King(4, 7,true, size));
        pieces.add(new Bishop(5, 7,true, size));
        pieces.add(new Knight(6, 7,true, size));
        pieces.add(new Rook(7, 7,true, size));
        pieces.add(new Pawn(0, 6,true, size));
        pieces.add(new Pawn(1, 6,true, size));
        pieces.add(new Pawn(2, 6,true, size));
        pieces.add(new Pawn(3, 6,true, size));
        pieces.add(new Pawn(4, 6,true, size));
        pieces.add(new Pawn(5, 6,true, size));
        pieces.add(new Pawn(6, 6,true, size));
        pieces.add(new Pawn(7, 6,true, size));
        return pieces;
    }

    public Image[] readChessPieces(int size) throws IOException {
        BufferedImage chessPieces = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/chess_pieces.png")));
        Image[] chess_pieces = new Image[12];
        int index = 0;
        for (int y = 0; y < 400; y += 200) {
            for (int x = 0; x < 1200; x += 200) {
                chess_pieces[index] = chessPieces.getSubimage(x, y, 200, 200).getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH);
                index++;
            }
        }
        return chess_pieces;
    }

    public ArrayList<BoardPiece> getPieces() {
        return pieces;
    }

    public Image[] getChessPieceImgs() {
        return chessPieceImgs;
    }
}
