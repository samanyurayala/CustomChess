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
        return getAttackSquares(board);
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d: BASE_MOVEMENT) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
            BoardPiece piece = board.getPieceVec2D(testVector);
            if (piece == null || piece.isWhite() != isWhite()) moves.add(testVector);
        }
        return moves;
    }
}
