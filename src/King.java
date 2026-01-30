import java.util.ArrayList;
import java.util.Arrays;

public class King extends BoardPiece {
    private final Vector2d[] BASE_MOVEMENT = {
            new Vector2d(1, 0), // Horizontal Right
            new Vector2d(-1, 0), // Horizontal Left
            new Vector2d(0, -1), // Vertical Up
            new Vector2d(0, 1), // Vertical Down
            new Vector2d(-1, -1), // Diagonal Left Up
            new Vector2d(1, -1), // Diagonal Right Up
            new Vector2d(-1, 1), // Diagonal Left Down
            new Vector2d(1, 1)  // Diagonal Right Down
    };

    public King(int xPos, int yPos, boolean isWhite, int scale) {
        super(xPos, yPos, isWhite, scale);
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        ArrayList<Vector2d> moves = getAttackSquares(board);
        ArrayList<Vector2d> enemyControlledSquares = getSquaresControlledByEnemy(board);
        moves.removeIf(enemyControlledSquares::contains);
        if (canCastle(board)[1]) moves.add(new Vector2d(getXPos() + 2, getYPos()));
        if (canCastle(board)[0]) moves.add(new Vector2d(getXPos() - 2, getYPos()));
        return moves;
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d : BASE_MOVEMENT) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) continue;
            BoardPiece piece = board.getPieceVec2D(testVector);
            if (piece != null) {
                if (piece.isWhite() != isWhite()) moves.add(testVector);
            } else moves.add(testVector);
        }
        return moves;
    }

    public boolean isInCheck(Game board) {
        Vector2d currentPos = new Vector2d(getXPos(), getYPos());
        for (BoardPiece piece: board.getPieces()) {
            if (piece.isWhite() != isWhite() && piece.getAttackSquares(board).contains(currentPos)) return true;
        }
        return false;
    }

    public ArrayList<BoardPiece> getCheckingPieces(Game board) {
        ArrayList<BoardPiece> pieces = new ArrayList<>();
        Vector2d currentPos = new Vector2d(getXPos(), getYPos());
        for (BoardPiece piece: board.getPieces()) {
            if (piece.isWhite() != isWhite() && piece.getAttackSquares(board).contains(currentPos)) pieces.add(piece);
        }
        return pieces;
    }

    public ArrayList<Vector2d> getSquaresBetweenCheckingPiece(Game board) {
        ArrayList<Vector2d> squares = new ArrayList<>();
        ArrayList<BoardPiece> checkingPieces = getCheckingPieces(board);
        if (checkingPieces.size() != 1) return new ArrayList<>();
        BoardPiece checkingPiece = checkingPieces.getFirst();
        Vector2d checkingPiecePosition = new Vector2d(checkingPiece.getXPos(), checkingPiece.getYPos());
        if (!(checkingPiece instanceof Knight) && !(checkingPiece instanceof Pawn)) {
            int adderX = Integer.compare(getXPos() - checkingPiecePosition.x, 0);
            int adderY = Integer.compare(getYPos() - checkingPiecePosition.y, 0);
            for (int i = 1; i < Math.max(Math.abs(getXPos() - checkingPiecePosition.x), Math.abs(getYPos() - checkingPiecePosition.y)); i++) {
                squares.add(new Vector2d(getXPos() - adderX * i, getYPos() - adderY * i));
            }
        }
        squares.add(checkingPiecePosition);
        return squares;
    }

    public ArrayList<Vector2d> getSquaresControlledByEnemy(Game board) {
        ArrayList<Vector2d> squares = new ArrayList<>();
        for (BoardPiece piece: board.getPieces()) {
            if (piece.isWhite() != isWhite()) {
                ArrayList<Vector2d> moves = piece.getAttackSquares(board);
                squares.addAll(moves);
            }
        }
        return squares;
    }

    public boolean[] canCastle(Game board) {
        if (hasMoved()) return new boolean[]{false, false};
        boolean[] castle = new boolean[2];
        ArrayList<BoardPiece> rooks = board.getPiece(Rook.class, isWhite());
        BoardPiece rook1 = null;
        BoardPiece rook2 = null;
        for (BoardPiece rook: rooks) {
            if (rook.getXPos() == 7) rook1 = rook;
            if (rook.getXPos() == 0) rook2 = rook;
        }
        ArrayList<Vector2d> squares = getSquaresControlledByEnemy(board);
        Vector2d squareKing1 = new Vector2d(getXPos() + 1, getYPos());
        Vector2d squareKing2 = new Vector2d(getXPos() + 2, getYPos());
        Vector2d squareQueen1 = new Vector2d(getXPos() - 1, getYPos());
        Vector2d squareQueen2 = new Vector2d(getXPos() - 2, getYPos());
        Vector2d squareQueen3 = new Vector2d(getXPos() - 3, getYPos());
        boolean squaresEmptyKing = board.getPieceVec2D(squareKing1) == null && board.getPieceVec2D(squareKing2) == null;
        boolean squaresEmptyQueen = board.getPieceVec2D(squareQueen1) == null && board.getPieceVec2D(squareQueen2) == null && board.getPieceVec2D(squareQueen3) == null;
        boolean enemyControlSquaresEmptyKing = squares.contains(squareKing1) || squares.contains(squareKing2);
        boolean enemyControlSquaresEmptyQueen = squares.contains(squareQueen1) || squares.contains(squareQueen2);
        if (rook1 != null && squaresEmptyKing && !enemyControlSquaresEmptyKing && !rook1.hasMoved()) castle[1] = true;
        if (rook2 != null && squaresEmptyQueen && !enemyControlSquaresEmptyQueen && !rook2.hasMoved()) castle[0] = true;
        return castle;
    }
}
