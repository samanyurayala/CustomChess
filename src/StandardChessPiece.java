public abstract class StandardChessPiece {
    boolean hasDiagonalMovement = false;
    boolean hasStraightMovement = false;
    boolean hasPawnMovement = false;
    int maxSquaresMovement = 8;

    public StandardChessPiece(int maxSquaresMovement, boolean hasDiagonalMovement, boolean hasStraightMovement, boolean hasPawnMovement) {
        this.maxSquaresMovement = maxSquaresMovement;
        this.hasDiagonalMovement = hasDiagonalMovement;
        this.hasStraightMovement = hasStraightMovement;
        this.hasPawnMovement = hasPawnMovement;
    }
}
