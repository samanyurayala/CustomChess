import java.util.LinkedList;

public class King extends BoardPiece {
    private final Vector2d[] baseMovement = {
            new Vector2d(1, 0), // Horizontal Right
            new Vector2d(-1, 0), // Horizontal Left
            new Vector2d(0, -1), // Vertical Up
            new Vector2d(0, 1), // Vertical Down
            new Vector2d(-1, -1), // Diagonal Left Up
            new Vector2d(1, -1), // Diagonal Right Up
            new Vector2d(-1, 1), // Diagonal Left Down
            new Vector2d(1, 1)  // Diagonal Right Down
    };

    public King(int xPos, int yPos, boolean isWhite, LinkedList<BoardPiece> pieces, int scale) {
        super(xPos, yPos, isWhite, pieces, scale);
    }

    public LinkedList<Vector2d> getLegalSquares() {
        int currentXPos = xPos;
        int currentYPos = yPos;
        LinkedList<Vector2d> moves = new LinkedList<>();
        for (Vector2d vector2d : baseMovement) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
            BoardPiece piece = getPieceVec2D(testVector);
            if (piece != null) {
                if (piece.isWhite != isWhite) moves.add(testVector);
            } else moves.add(testVector);
        }
        return moves;
    }
}
