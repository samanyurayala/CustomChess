import java.util.ArrayList;

public class Pawn extends BoardPiece {
    private final Vector2d[] BASE_WHITE_MOVEMENT = {
            new Vector2d(0, -1), // Normal Movement White
            new Vector2d(-1, -1), // Attack Movement Left White
            new Vector2d(1, -1), // Attack Movement Right White
    };

    private final Vector2d[] BASE_BLACK_MOVEMENT = {
            new Vector2d(0, 1), // Normal Movement Black
            new Vector2d(1, 1), // Attack Movement Left Black
            new Vector2d(-1, 1)  // Attack Movement Right Black
    };

    public Pawn(int xPos, int yPos, boolean isWhite, int scale) {
        super(xPos, yPos, isWhite, scale);
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        Vector2d[] colorMoves = new Vector2d[3];
        if (isWhite()) System.arraycopy(BASE_WHITE_MOVEMENT, 0, colorMoves, 0, 3);
        else System.arraycopy(BASE_BLACK_MOVEMENT, 0, colorMoves, 0, 3);
        for (Vector2d vector2d: colorMoves) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
            BoardPiece piece = board.getPieceVec2D(testVector);
            if (vector2d.x == 0) {
                if (piece == null) {
                    moves.add(testVector);
                    Vector2d testVector2 = new Vector2d(currentXPos, currentYPos + vector2d.y * 2);
                    if (board.getPieceVec2D(testVector2) == null && !hasMoved()) {
                        moves.add(testVector2);
                    }
                }
            } else {
                if (piece != null && piece.isWhite() != isWhite()) moves.add(testVector);
            }
        }
        BoardPiece king = board.getPiece(King.class, isWhite());
        if (king.isInCheck(board)) {
            ArrayList<Vector2d> squares = king.getSquaresBetweenCheckingPiece(board);
            moves.retainAll(squares);
        }
        return moves;
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        Vector2d[] colorMoves = new Vector2d[3];
        if (isWhite()) System.arraycopy(BASE_WHITE_MOVEMENT, 0, colorMoves, 0, 3);
        else System.arraycopy(BASE_BLACK_MOVEMENT, 0, colorMoves, 0, 3);
        for (Vector2d vector2d: colorMoves) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7 || vector2d.x == 0) continue;
            moves.add(testVector);
        }
        return moves;
    }
}
