import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
    private final Map<Character, Class<? extends BoardPiece>> CHAR_MAP = Map.of(
            'q', Queen.class,
            'r', Rook.class,
            'b', Bishop.class,
            'n', Knight.class,
            'p', Pawn.class,
            'k', King.class
    );
    private final Map<Character, Integer> SQUARE_MAP = Map.of(
            'a', 0,
            'b', 1,
            'c', 2,
            'd', 3,
            'e', 4,
            'f', 5,
            'g', 6,
            'h', 7
    );
    public final boolean[] whiteCastle = {true, true};
    public final boolean[] blackCastle = {true, true};
    public static final int BOARD_SIZE = 8;
    public static final int LEFT_FILE = 0, TOP_RANK = 0;
    public static final int RIGHT_FILE = 7, BOTTOM_RANK = 7;

    public Game(int size, String fen) {
        this.SIZE = size;
        CHESS_PIECE_IMGS = readChessPieces(size);
        pieces = readFEN(fen);
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

    public BoardPiece getPieceArrayList(int xPos, int yPos, ArrayList<BoardPiece> pieces) {
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

    public ArrayList<BoardPiece> readFEN(String board) {
        ArrayList<BoardPiece> pieces = new ArrayList<>();
        String[] read = board.split("\\s+");
        int currentRank = 0;
        int currentFile = 0;
        for (int i = 0; i < read[0].length(); i++) {
            char c = read[0].charAt(i);
            if (Character.isDigit(c)) currentFile += Integer.parseInt(String.valueOf(c));
            else if (c == '/') {
                currentRank++;
                currentFile = 0;
            } else if (Character.isLetter(c)) {
                boolean isWhite = Character.isUpperCase(c);
                Class<? extends BoardPiece> newPieceType = CHAR_MAP.get(Character.toLowerCase(c));
                try {
                    pieces.add(newPieceType.getConstructor(int.class, int.class, boolean.class, int.class).newInstance(currentFile, currentRank, isWhite, SIZE));
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                currentFile++;
            }
        }
        isWhiteTurn = read[1].charAt(0) == 'w';
        String[] canCastle = read[2].split("");
        if (!Arrays.asList(canCastle).contains("K")) whiteCastle[1] = false;
        if (!Arrays.asList(canCastle).contains("Q")) whiteCastle[0] = false;
        if (!Arrays.asList(canCastle).contains("k")) blackCastle[1] = false;
        if (!Arrays.asList(canCastle).contains("q")) blackCastle[0] = false;
        if (!read[3].equals("-")) {
            String[] enPassant = read[3].split("");
            int xPos = SQUARE_MAP.get(enPassant[0].charAt(0));
            int yPos = 8 - Integer.parseInt(enPassant[1]);
            int adderY = (yPos == 2) ? 1 : -1;
            BoardPiece piece1 = getPieceArrayList(xPos + 1, yPos + adderY, pieces);
            BoardPiece piece2 = getPieceArrayList(xPos - 1, yPos + adderY, pieces);
            if (piece1 instanceof Pawn) {
                ArrayList<Vector2d> enPassantSquares1 = new ArrayList<>();
                enPassantSquares1.add(new Vector2d(xPos, yPos));
                ((Pawn) piece1).setEnPassantSquares(enPassantSquares1);
            }
            if (piece2 instanceof Pawn) {
                ArrayList<Vector2d> enPassantSquares2 = new ArrayList<>();
                enPassantSquares2.add(new Vector2d(xPos, yPos));
                ((Pawn) piece2).setEnPassantSquares(enPassantSquares2);
            }
        }
        return pieces;
    }

    public Image[] readChessPieces(int size) {
        BufferedImage chessPieces;
        try {
            chessPieces = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/images/chess_pieces.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (chessPieces == null) return new Image[24];
        Image[] chess_pieces = new Image[24];
        int index = 0;
        for (int y = 0; y < 600; y += 300) {
            for (int x = 0; x < 1800; x += 300) {
                chess_pieces[index] = chessPieces.getSubimage(x, y, 300, 300).getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH);
                chess_pieces[index + 12] = chessPieces.getSubimage(x, y, 300, 300).getScaledInstance(size / 2, size / 2, BufferedImage.SCALE_SMOOTH);
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
