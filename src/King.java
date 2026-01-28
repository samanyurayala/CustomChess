import java.util.ArrayList;

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

    public ArrayList<Vector2d> getLegalSquares(Game board) {
        int currentXPos = xPos;
        int currentYPos = yPos;
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d : BASE_MOVEMENT) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
            BoardPiece piece = board.getPieceVec2D(testVector);
            if (piece != null) {
                if (piece.isWhite != isWhite) moves.add(testVector);
            } else moves.add(testVector);
        }
        return moves;
    }

    public boolean isInCheck(Game board) {
        Vector2d currentPos = new Vector2d(xPos, yPos);
        for (BoardPiece piece: board.pieces) {
            if (piece.isWhite != isWhite && !(piece instanceof King) && piece.getLegalSquares(board).contains(currentPos)) return true;
        }
        return false;
    }
}
