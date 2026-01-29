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
        //System.out.println(Arrays.deepToString(enemyControlledSquares.toArray()));
        moves.removeIf(enemyControlledSquares::contains);
        return moves;
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d : BASE_MOVEMENT) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < 0 || testVector.x > 7 || testVector.y < 0 || testVector.y > 7) continue;
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
            if (piece.isWhite() != isWhite() && !(piece instanceof King) && piece.getAttackSquares(board).contains(currentPos)) return true;
        }
        return false;
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
}
