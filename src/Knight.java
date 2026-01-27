import java.util.LinkedList;

public class Knight extends BoardPiece {
    private final Vector2d[] baseMovement = {
            new Vector2d(2, -1), // Not even going to bother trying to name the knight movement
            new Vector2d(2, 1),
            new Vector2d(1, -2),
            new Vector2d(1, 2),
            new Vector2d(-2, -1),
            new Vector2d(-2, 1),
            new Vector2d(-1, -2),
            new Vector2d(-1, 2)
    };

    public Knight(int xPos, int yPos, boolean isWhite, LinkedList<BoardPiece> pieces, int scale) {
        super(xPos, yPos, isWhite, pieces, scale);
    }

    public LinkedList<Vector2d> getLegalSquares() {
        int currentXPos = xPos;
        int currentYPos = yPos;
        LinkedList<Vector2d> moves = new LinkedList<>();
        for (Vector2d vector2d: baseMovement) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
            BoardPiece piece = getPieceVec2D(testVector);
            if (piece == null || piece.isWhite != isWhite) moves.add(testVector);
        }
        return moves;
    }
}
