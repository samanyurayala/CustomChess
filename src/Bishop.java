import java.util.ArrayList;

public class Bishop extends BoardPiece {
    private final Vector2d[] BASE_MOVEMENT = {
            new Vector2d(-1, -1), // Diagonal Left Up
            new Vector2d(1, -1), // Diagonal Right Up
            new Vector2d(-1, 1), // Diagonal Left Down
            new Vector2d(1, 1)  // Diagonal Right Down
    };

    public Bishop(int xPos, int yPos, boolean isWhite, int scale) {
        super(xPos, yPos, isWhite, scale);
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d: BASE_MOVEMENT) {
            for (int i = 1; i < Game.BOARD_SIZE; i++) {
                Vector2d testVector = new Vector2d(currentXPos + vector2d.x * i, currentYPos + vector2d.y * i);
                if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) break;
                BoardPiece piece = board.getPieceVec2D(testVector);
                if (piece != null) {
                    if (piece.isWhite() != isWhite()) moves.add(testVector);
                    break;
                } else moves.add(testVector);
            }
        }
        BoardPiece king = board.getPiece(King.class, isWhite()).getFirst();
        if (king.isInCheck(board)) {
            ArrayList<Vector2d> squares = king.getSquaresBetweenCheckingPiece(board);
            moves.retainAll(squares);
        }
        if (getPinningPiece(board) != null) {
            moves.retainAll(getSquaresBetweenPinningPiece(board));
        }
        return moves;
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d: BASE_MOVEMENT) {
            for (int i = 1; i < Game.BOARD_SIZE; i++) {
                Vector2d testVector = new Vector2d(currentXPos + vector2d.x * i, currentYPos + vector2d.y * i);
                if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) break;
                BoardPiece piece = board.getPieceVec2D(testVector);
                moves.add(testVector);
                if (piece != null) {
                    if (piece instanceof King && piece.isWhite() != isWhite()) moves.add(new Vector2d(testVector.x + vector2d.x, testVector.y + vector2d.y));
                    break;
                }
            }
        }
        return moves;
    }

    public ArrayList<Vector2d> pieceIsPinningSquares(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> squares = new ArrayList<>();
        for (Vector2d vector2d: BASE_MOVEMENT) {
            for (int i = 1; i < Game.BOARD_SIZE; i++) {
                Vector2d testVector = new Vector2d(currentXPos + vector2d.x * i, currentYPos + vector2d.y * i);
                if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) break;
                BoardPiece piece = board.getPieceVec2D(testVector);
                boolean isEmpty = false;
                if (piece != null && piece.isWhite() != isWhite()) {
                    for (int j = 1; j < Game.BOARD_SIZE - i + 1; j++) {
                        Vector2d testVector1 = new Vector2d(currentXPos + vector2d.x * (i + j), currentYPos + vector2d.y * (i + j));
                        BoardPiece testPiece = board.getPieceVec2D(testVector1);
                        if (testPiece != null) {
                            if (testPiece instanceof King && testPiece.isWhite() != isWhite()) isEmpty = true;
                            break;
                        }
                    }
                    if (isEmpty) squares.add(testVector);
                    break;
                }
                if (piece != null && piece.isWhite() == isWhite()) break;
            }
        }
        return squares;
    }
}
