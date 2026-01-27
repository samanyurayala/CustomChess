import java.util.ArrayList;

public class Bishop extends BoardPiece {
    private final Vector2d[] baseMovement = {
            new Vector2d(-1, -1), // Diagonal Left Up
            new Vector2d(1, -1), // Diagonal Right Up
            new Vector2d(-1, 1), // Diagonal Left Down
            new Vector2d(1, 1)  // Diagonal Right Down
    };

    public Bishop(int xPos, int yPos, boolean isWhite, ArrayList<BoardPiece> pieces, int scale) {
        super(xPos, yPos, isWhite, pieces, scale);
    }

    public ArrayList<Vector2d> getLegalSquares() {
        int currentXPos = xPos;
        int currentYPos = yPos;
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d: baseMovement) {
            for (int i = 1; i < 8; i++) {
                Vector2d testVector = new Vector2d(currentXPos + vector2d.x * i, currentYPos + vector2d.y * i);
                if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) break;
                BoardPiece piece = getPieceVec2D(testVector);
                if (piece != null) {
                    if (piece.isWhite != isWhite) moves.add(testVector);
                    break;
                } else moves.add(testVector);
            }
        }
        return moves;
    }
}
