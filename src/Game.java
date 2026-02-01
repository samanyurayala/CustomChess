import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Game {
    private BoardPiece selectedPiece = null;
    private ArrayList<BoardPiece> pieces;
    private boolean isWhiteTurn;
    private final Image[] CHESS_PIECE_IMGS;
    private final int SIZE;
    private final Sound SOUND;
    private final Icon[] PROMOTION;
    private final Map<Integer, Class<? extends BoardPiece>> PROMOTION_MAP = Map.of(
            3, Queen.class,
            2, Rook.class,
            1, Bishop.class,
            0, Knight.class
    );
    public static final int BOARD_SIZE = 8;
    public static final int LEFT_FILE = 0, TOP_RANK = 0;
    public static final int RIGHT_FILE = 7, BOTTOM_RANK = 7;

    public Game(int size) throws IOException {
        CHESS_PIECE_IMGS = readChessPieces(size);
        pieces = initChessPieces(size);
        isWhiteTurn = true;
        this.SIZE = size;
        SOUND = new Sound();
        PROMOTION = new Icon[]{new ImageIcon(CHESS_PIECE_IMGS[3]), new ImageIcon(CHESS_PIECE_IMGS[2]), new ImageIcon(CHESS_PIECE_IMGS[4]), new ImageIcon(CHESS_PIECE_IMGS[1]), new ImageIcon(CHESS_PIECE_IMGS[9]), new ImageIcon(CHESS_PIECE_IMGS[8]), new ImageIcon(CHESS_PIECE_IMGS[10]), new ImageIcon(CHESS_PIECE_IMGS[7])};
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
        int oldYPos = piece.getYPos();
        boolean isPieceOnSquare = false;
        boolean playMove = false;
        boolean playCapture = false;
        boolean playCastle = false;
        boolean playPromote = false;
        boolean capturesPiece = false;
        BoardPiece piece2 = getPieceXPosYPos(xPos, yPos);
        ArrayList<Vector2d> legalSquares = piece.getLegalMoves(this);
        Vector2d testVector = new Vector2d(xPos, yPos);
        if (!legalSquares.contains(testVector) || !(isWhiteTurn == piece.isWhite())) {
            piece.setX(piece.getXPos() * SIZE);
            piece.setY(piece.getYPos() * SIZE);
            return;
        }
        if (piece2 != null && piece2.isWhite() != piece.isWhite()) {
            capturesPiece = true;
            isPieceOnSquare = true;
            playCapture = true;
        } else {
            playMove = true;
        }
        piece.setXPos(xPos);
        piece.setYPos(yPos);
        piece.setX(xPos * SIZE);
        piece.setY(yPos * SIZE);
        ArrayList<BoardPiece> pawns = getPiece(Pawn.class, !piece.isWhite());
        for (BoardPiece pawn : pawns) {
            if (pawn.isEnpassant()) pawn.setEnpassant(false);
        }
        if (piece instanceof Pawn) {
            if (!piece.hasMoved()) {
                if (((piece.isWhite() && piece.getYPos() == 4) || (!piece.isWhite() && piece.getYPos() == 3)) && !piece.isEnpassant()) piece.setEnpassant(true);
            } else if (piece.hasMoved() && piece.isEnpassant()) piece.setEnpassant(false);
        }
        if (!piece.hasMoved()) piece.setHasMoved(true);
        if (piece instanceof King) {
            if (xPos - oldXPos == 2) { /* kingside castling distance */
                BoardPiece rook = getPieceXPosYPos(7, piece.getYPos()); /* kingside rook */
                rook.setXPos(5);
                rook.setX(5 * SIZE);
                playCastle = true;
                playMove = false;
            } else if (xPos - oldXPos == -2 ) { /* queenside castling distance */
                BoardPiece rook = getPieceXPosYPos(0, piece.getYPos()); /* queenside rook */
                rook.setXPos(3);
                rook.setX(3 * SIZE);
                playCastle = true;
                playMove = false;
            }
        }
        if (piece instanceof Pawn) {
            int newY = piece.isWhite() ? 1 : -1;
            if (Math.abs(xPos - oldXPos) == 1 && !isPieceOnSquare) {
                kill(getPieceXPosYPos(piece.getXPos(), piece.getYPos() + newY));
                playCapture = true;
            }
        }
        if (piece instanceof Pawn) {
            if ((piece.isWhite() && piece.getYPos() == 0) || (!piece.isWhite() && piece.getYPos() == 7)) {
                Icon[] PROMOTIONS = piece.isWhite() ? Arrays.copyOfRange(PROMOTION, 0, 4) : Arrays.copyOfRange(PROMOTION, 4, PROMOTION.length);
                int choice = JOptionPane.showOptionDialog(null, null, "Promote", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), PROMOTIONS, null);
                if (choice == -1) {
                    piece.setXPos(oldXPos);
                    piece.setYPos(oldYPos);
                    piece.setX(oldXPos * SIZE);
                    piece.setY(oldYPos * SIZE);
                    return;
                }
                promote(choice, piece);
                playPromote = true;
                playCapture = false;
            }
        }
        if (capturesPiece) kill(piece2);
        isWhiteTurn = !isWhiteTurn;
        boolean kingInCheck = getPiece(King.class, isWhiteTurn).getFirst().isInCheck(this);
        if (kingInCheck) {
            SOUND.playCheck();
            playMove = false;
            playCapture = false;
            playCastle = false;
            playPromote = false;
        }
        if (getAllLegalMoves(isWhiteTurn).isEmpty()) {
            if (kingInCheck) {
                SOUND.playEnd();
            }
            else SOUND.playEnd();
        }
        if (playCapture) SOUND.playCapture();
        if (playMove) SOUND.playMove();
        if (playCastle) SOUND.playCastle();
        if (playPromote) SOUND.playPromote();
    }

    public ArrayList<Vector2d> getAllLegalMoves(boolean isWhite) {
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (BoardPiece piece : pieces) {
            if (piece.isWhite() == isWhite) moves.addAll(piece.getLegalMoves(this));
        }
        return moves;
    }

    public void promote(int option, BoardPiece piece) {
        Class<? extends BoardPiece> newPieceType = PROMOTION_MAP.get(option);
        try {
            BoardPiece newPiece = newPieceType.getConstructor(int.class, int.class, boolean.class, int.class).newInstance(piece.getXPos(), piece.getYPos(), piece.isWhite(), SIZE);
            kill(piece);
            newPiece.setHasMoved(true);
            pieces.add(newPiece);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<BoardPiece> getPiece(Class<? extends BoardPiece> test, boolean isWhite) {
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
        BufferedImage chessPieces = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/images/chess_pieces.png")));
        Image[] chess_pieces = new Image[12];
        int index = 0;
        for (int y = 0; y < 600; y += 300) {
            for (int x = 0; x < 1800; x += 300) {
                chess_pieces[index] = chessPieces.getSubimage(x, y, 300, 300).getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH);
                index++;
            }
        }
        return chess_pieces;
    }

    public ArrayList<BoardPiece> getPieces() {
        return pieces;
    }

    public Image[] getChessPieceImgs() {
        return CHESS_PIECE_IMGS;
    }
}
