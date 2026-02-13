import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Game {
    private boolean engineMoveMade = false;
    private BoardPiece selectedPiece = null;
    private ArrayList<BoardPiece> pieces;
    private boolean isWhiteTurn;
    private final Image[] CHESS_PIECE_IMGS;
    private final int SIZE;
    private final Sound SOUND;
    private final Icon[] PROMOTION;
    private int turn;
    private int turnTill50MoveRule;
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
    private final Map<Integer, Character> SQUARE_MAP2 = Map.of(
            0, 'a',
            1, 'b',
            2, 'c',
            3, 'd',
            4, 'e',
            5, 'f',
            6, 'g',
            7, 'h'
    );
    private final Map<Class<? extends BoardPiece>, Character> CHAR_MAP_2 = Map.of(
            Queen.class, 'q',
            Rook.class, 'r',
            Bishop.class, 'b',
            Knight.class, 'n',
            Pawn.class, 'p',
            King.class, 'k'
    );
    public final boolean[] whiteCastle = {true, true};
    public final boolean[] blackCastle = {true, true};
    public static final int BOARD_SIZE = 8;
    public static final int LEFT_FILE = 0, TOP_RANK = 0;
    public static final int RIGHT_FILE = 7, BOTTOM_RANK = 7;

    Engine engine = new Engine("minimalChess");

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

    public void makeMove(String move) {
        if (move.isBlank()) return;
        String[] moveSquare = move.split("");
        Vector2d oldSquare = new Vector2d(SQUARE_MAP.get(moveSquare[0].charAt(0)), 8 - Integer.parseInt(moveSquare[1]));
        BoardPiece piece = getPieceXPosYPos(oldSquare.x, oldSquare.y);
        Vector2d newSquare = new Vector2d(SQUARE_MAP.get(moveSquare[2].charAt(0)), 8 - Integer.parseInt(moveSquare[3]));
        move(newSquare.x, newSquare.y, piece);
    }

    public void move(int xPos, int yPos, BoardPiece piece) {
        int oldXPos = piece.getXPos();
        int oldYPos = piece.getYPos();
        boolean isPieceOnSquare = false;
        boolean playMove = false;
        boolean playCapture = false;
        boolean playCastle = false;
        boolean playPromote = false;
        boolean capturesPieceNotEnpassant = false;
        boolean capture = false;
        BoardPiece piece2 = getPieceXPosYPos(xPos, yPos);
        ArrayList<Vector2d> legalSquares = piece.getLegalMoves(this);
        Vector2d testVector = new Vector2d(xPos, yPos);
        if (!legalSquares.contains(testVector) || !(isWhiteTurn == piece.isWhite())) {
            piece.setX(piece.getXPos() * SIZE);
            piece.setY(piece.getYPos() * SIZE);
            return;
        }
        if (piece2 != null && piece2.isWhite() != piece.isWhite()) {
            capturesPieceNotEnpassant = true;
            isPieceOnSquare = true;
            playCapture = true;
            capture = true;
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
                capture = true;
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
        if (capturesPieceNotEnpassant) kill(piece2);
        ArrayList<BoardPiece> allPawns = getPiece(Pawn.class, piece.isWhite());
        for (BoardPiece pawn : allPawns) {
            ((Pawn) pawn).setEnPassantSquares(new ArrayList<>());
        }
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
        if (!piece.isWhite()) {
            turn++;
            turnTill50MoveRule++;
        }
        if (piece instanceof Pawn || capture) turnTill50MoveRule = 0;
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
        turnTill50MoveRule = Integer.parseInt(read[4]);
        turn = Integer.parseInt(read[5]);
        return pieces;
    }

    public String readFenFromPosition(ArrayList<BoardPiece> pieces) {
        StringBuilder fen = new StringBuilder();
        for (int currentRank = 0; currentRank < Game.BOARD_SIZE; currentRank++) {
            int counter = 0;
            for (int currentFile = 0; currentFile < Game.BOARD_SIZE; currentFile++) {
                BoardPiece piece = getPieceXPosYPos(currentFile, currentRank);
                if (piece != null) {
                    if (counter != 0) fen.append(counter);
                    char pieceClass = CHAR_MAP_2.get(piece.getClass());
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
        boolean[] whiteKingCanCastle = getPiece(King.class, true).getFirst().isCastlingPossible(this);
        boolean[] blackKingCanCastle = getPiece(King.class, false).getFirst().isCastlingPossible(this);
        if (whiteKingCanCastle[1]) fen.append("K");
        if (whiteKingCanCastle[0]) fen.append("Q");
        if (blackKingCanCastle[1]) fen.append("k");
        if (blackKingCanCastle[0]) fen.append("q");
        if (!whiteKingCanCastle[1] && !whiteKingCanCastle[0] && !blackKingCanCastle[1] && !blackKingCanCastle[0]) fen.append("-");
        ArrayList<BoardPiece> allPawns = getPiece(Pawn.class, isWhiteTurn);
        ArrayList<Vector2d> enPassantSquares = new ArrayList<>();
        for (BoardPiece piece : allPawns) {
            enPassantSquares.addAll(((Pawn) piece).enpassant(this));
        }
        String square;
        if (enPassantSquares.isEmpty()) square = "-";
        else {
            Vector2d squareVec2D = enPassantSquares.getFirst();
            square = SQUARE_MAP2.get(squareVec2D.x) + String.valueOf(squareVec2D.y + 1);
        }
        fen.append(" ").append(square).append(" ").append(turnTill50MoveRule).append(" ").append(turn);
        return fen.toString();
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

    public boolean isEngineMoveMade() {
        return engineMoveMade;
    }

    public void setEngineMoveMade(boolean engineMoveMade) {
        this.engineMoveMade = engineMoveMade;
    }

    public Image[] getChessPieceImgs() {
        return CHESS_PIECE_IMGS;
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }
}
