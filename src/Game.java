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
        selectedPiece = getPiece(x, y);
    }

    public void dropPiece(int x, int y) {
        if (selectedPiece == null) return;
        move(x / SIZE, y / SIZE, selectedPiece);
    }

    public void move(int xPos, int yPos, BoardPiece piece) {
        BoardPiece piece2 = getPiece(xPos * SIZE, yPos * SIZE);
        ArrayList<Vector2d> legalSquares = piece.getLegalMoves(this);
        Vector2d testVector = new Vector2d(xPos, yPos);
        if (!legalSquares.contains(testVector) || !(isWhiteTurn == piece.isWhite())) {
            piece.setX(piece.getXPos() * SIZE);
            piece.setY(piece.getYPos() * SIZE);
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
        isWhiteTurn = !isWhiteTurn;
    }

    public BoardPiece getPiece(int x, int y) {
        int xPos = x / SIZE;
        int yPos = y / SIZE;
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
