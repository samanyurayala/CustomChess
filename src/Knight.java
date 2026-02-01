import java.util.ArrayList;

public class Knight extends BoardPiece {
    private final Vector2d[] BASE_MOVEMENT = {
            new Vector2d(2, -1), // Not even going to bother trying to name the knight movement
            new Vector2d(2, 1),
            new Vector2d(1, -2),
            new Vector2d(1, 2),
            new Vector2d(-2, -1),
            new Vector2d(-2, 1),
            new Vector2d(-1, -2),
            new Vector2d(-1, 2)
    };

    public Knight(int xPos, int yPos, boolean isWhite, int scale) {
        super(xPos, yPos, isWhite, scale);
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d: BASE_MOVEMENT) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) continue;
            BoardPiece piece = board.getPieceVec2D(testVector);
            if (piece == null || piece.isWhite() != isWhite()) moves.add(testVector);
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
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) continue;
            moves.add(testVector);
        }
        return moves;
    }
}
