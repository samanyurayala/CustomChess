import java.util.ArrayList;

public class BoardPiece {
    private int xPos, yPos;
    private boolean isWhite;
    private int x, y;
    private boolean hasMoved;
    private boolean enpassant;

    public BoardPiece(int xPos, int yPos, boolean isWhite, int size) {
        this.xPos = xPos;
        this.yPos = yPos;
        x = xPos * size;
        y = yPos * size;
        this.isWhite = isWhite;
        hasMoved = false;
        enpassant = false;
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        return new ArrayList<>();
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        return new ArrayList<>();
    }

    public boolean isInCheck(Game board) {
        return false;
    }

    public ArrayList<Vector2d> getSquaresBetweenCheckingPiece(Game board) {
        return new ArrayList<>();
    }

    public boolean[] canCastle(Game board) {
        return new boolean[]{false, false};
    }

    public ArrayList<Vector2d> pieceIsPinningSquares(Game board) {
        return new ArrayList<>();
    }

    public BoardPiece getPinningPiece(Game board) {
        for (BoardPiece piece: board.getPieces()) {
            if (piece.isWhite() != isWhite() && piece.pieceIsPinningSquares(board).contains(new Vector2d(getXPos(), getYPos()))) return piece;
        }
        return null;
    }

    public ArrayList<Vector2d> getSquaresBetweenPinningPiece(Game board) {
        ArrayList<Vector2d> squares = new ArrayList<>();
        BoardPiece pinningPiece = getPinningPiece(board);
        int adderX = Integer.compare(getXPos() - pinningPiece.getXPos(), 0);
        int adderY = Integer.compare(getYPos() - pinningPiece.getYPos(), 0);
        for (int i = 1; i < Math.max(Math.abs(getXPos() - pinningPiece.getXPos()), Math.abs(getYPos() - pinningPiece.getYPos())); i++) {
            squares.add(new Vector2d(getXPos() - adderX * i, getYPos() - adderY * i));
        }
        squares.add(new Vector2d(pinningPiece.getXPos(), pinningPiece.getYPos()));
        return squares;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public boolean isEnpassant() {
        return enpassant;
    }

    public void setEnpassant(boolean enpassant) {
        this.enpassant = enpassant;
    }
}
