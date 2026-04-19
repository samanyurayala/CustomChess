import java.util.ArrayList;

public class CustomPiece extends BoardPiece {
    private int xPos, yPos;
    private boolean isWhite;
    private int x, y;
    private boolean hasMoved;
    private boolean enpassant;
    private final Vector2d[] BASE_MOVEMENT;
    private final int MAX_MULTIPLIER;
    private final boolean CAN_JUMP;
    private String name;

    public CustomPiece(int xPos, int yPos, boolean isWhite, int size, Vector2d baseMovement, int maxMultiplier, boolean canJump, String name) {
        super(xPos, yPos, isWhite, size);
        BASE_MOVEMENT = createBaseMovement(baseMovement);
        MAX_MULTIPLIER = maxMultiplier;
        CAN_JUMP = canJump;
        this.name = name;
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        int currentXPos = getXPos();
        int currentYPos = getYPos();
        ArrayList<Vector2d> moves = new ArrayList<>();
        for (Vector2d vector2d: BASE_MOVEMENT) {
            for (int i = 1; i < MAX_MULTIPLIER; i++) {
                Vector2d testVector = new Vector2d(currentXPos + vector2d.x * i, currentYPos + vector2d.y * i);
                if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) break;
                BoardPiece piece = board.getPieceVec2D(testVector);
                if (piece != null) {
                    if (piece.isWhite() != isWhite()) moves.add(testVector);
                    break;
                } else moves.add(testVector);
            }
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
            for (int i = 1; i < MAX_MULTIPLIER; i++) {
                Vector2d testVector = new Vector2d(currentXPos + vector2d.x * i, currentYPos + vector2d.y * i);
                if (testVector.x < Game.LEFT_FILE || testVector.x > Game.RIGHT_FILE || testVector.y < Game.TOP_RANK || testVector.y > Game.BOTTOM_RANK) break;
                BoardPiece piece = board.getPieceVec2D(testVector);
                moves.add(testVector);
                if (piece != null) {
                    if (piece instanceof King && piece.isWhite() != isWhite()) moves.add(new Vector2d(testVector.x + vector2d.x, testVector.y + vector2d.y));
                    break;
                }
            }
        }
        return moves;
    }

    public Vector2d[] createBaseMovement(Vector2d baseMovement) {
        Vector2d[] newBaseMovement = new Vector2d[8];
        int currentX = baseMovement.x;
        int currentY = baseMovement.y;
        newBaseMovement[0] = new Vector2d(currentX, currentY);
        newBaseMovement[1] = new Vector2d(-currentX, currentY);
        newBaseMovement[2] = new Vector2d(-currentX, -currentY);
        newBaseMovement[3] = new Vector2d(currentX, -currentY);
        newBaseMovement[4] = new Vector2d(currentY, currentX);
        newBaseMovement[5] = new Vector2d(-currentY, currentX);
        newBaseMovement[6] = new Vector2d(-currentY, -currentX);
        newBaseMovement[7] = new Vector2d(currentY, -currentX);
        return newBaseMovement;
    }
}
