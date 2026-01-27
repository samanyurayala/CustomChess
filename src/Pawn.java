import java.util.LinkedList;

public class Pawn extends BoardPiece {
    private final Vector2d[] baseWhiteMovement = {
            new Vector2d(0, -1), // Normal Movement White
            new Vector2d(-1, -1), // Attack Movement Left White
            new Vector2d(1, -1), // Attack Movement Right White
    };

    private final Vector2d[] baseBlackMovement = {
            new Vector2d(0, 1), // Normal Movement Black
            new Vector2d(1, 1), // Attack Movement Left Black
            new Vector2d(-1, 1)  // Attack Movement Right Black
    };

    public Pawn(int xPos, int yPos, boolean isWhite, LinkedList<BoardPiece> pieces, int scale) {
        super(xPos, yPos, isWhite, pieces, scale);
    }

    public LinkedList<Vector2d> getLegalSquares() {
        int currentXPos = xPos;
        int currentYPos = yPos;
        LinkedList<Vector2d> moves = new LinkedList<>();
        Vector2d[] colorMoves = new Vector2d[3];
        if (isWhite) System.arraycopy(baseWhiteMovement, 0, colorMoves, 0, 3);
        else System.arraycopy(baseBlackMovement, 0, colorMoves, 0, 3);
        for (Vector2d vector2d: colorMoves) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
            BoardPiece piece = getPieceVec2D(testVector);
            if (vector2d.x == 0) {
                if (piece == null) {
                    moves.add(testVector);
                    Vector2d testVector2 = new Vector2d(currentXPos, currentYPos + vector2d.y * 2);
                    if (getPieceVec2D(testVector2) == null && !hasMoved) moves.add(testVector2);
                }
            } else {
                if (piece != null) moves.add(testVector);
            }
        }
        return moves;
    }
}
