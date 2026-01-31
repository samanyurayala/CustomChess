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

    private boolean enpassant;

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
            if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) continue;
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
        moves = enpassant(moves, board);
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
        Vector2d[] colorMoves = new Vector2d[3];
        if (isWhite()) System.arraycopy(BASE_WHITE_MOVEMENT, 0, colorMoves, 0, 3);
        else System.arraycopy(BASE_BLACK_MOVEMENT, 0, colorMoves, 0, 3);
        for (Vector2d vector2d: colorMoves) {
            Vector2d testVector = new Vector2d(currentXPos + vector2d.x, currentYPos + vector2d.y);
            if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK || vector2d.x == 0) continue;
            moves.add(testVector);
        }
        return moves;
    }

    public ArrayList<Vector2d> enpassant(ArrayList<Vector2d> moves, Game board) {
        int test = isWhite() ? 3 : 4;
        int newY = isWhite() ? -1 : 1;
        if (getYPos() != test) return moves;
        BoardPiece testPawn1 = board.getPieceXPosYPos(getXPos() + 1, getYPos());
        BoardPiece testPawn2 = board.getPieceXPosYPos(getXPos() - 1, getYPos());
        if (testPawn1 instanceof Pawn && testPawn1.isEnpassant()) moves.add(new Vector2d(getXPos() + 1, getYPos() + newY));
        if (testPawn2 instanceof Pawn && testPawn2.isEnpassant()) moves.add(new Vector2d(getXPos() - 1, getYPos() + newY));
        return moves;
    }

    public boolean isEnpassant() {
        return enpassant;
    }

    public void setEnpassant(boolean enpassant) {
        this.enpassant = enpassant;
    }
}
