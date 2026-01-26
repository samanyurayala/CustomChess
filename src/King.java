public class King implements StandardChessPiece {
    private Vector2d baseHorizontalMovement = new Vector2d(1, 0);
    private Vector2d baseVerticalMovement = new Vector2d(0, 1);
    private Vector2d baseDiagonalLeftMovement = new Vector2d(-1, 1);
    private Vector2d baseDiagonalRightMovement = new Vector2d(1, 1);
    private int maxMultiplier = 1;
}
