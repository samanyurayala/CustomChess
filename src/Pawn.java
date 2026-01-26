public class Pawn implements StandardChessPiece{
    private Vector2d baseMovement = new Vector2d(0, 1);
    private Vector2d baseAttackMovement = new Vector2d(1, 1);
    private int maxMultiplier = 1;
    private boolean hasMoved = false;
    private boolean hasMovedTwoSquares = false;
}
