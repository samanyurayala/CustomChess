import java.util.ArrayList;

public class BoardPiece {
    private int xPos, yPos;
    private boolean isWhite;
    private int x, y;
    private boolean hasMoved;

    public BoardPiece(int xPos, int yPos, boolean isWhite, int size) {
        this.xPos = xPos;
        this.yPos = yPos;
        x = xPos * size;
        y = yPos * size;
        this.isWhite = isWhite;
        hasMoved = false;
    }

    public ArrayList<Vector2d> getAttackSquares(Game board) {
        return new ArrayList<>();
    }

    public ArrayList<Vector2d> getLegalMoves(Game board) {
        return new ArrayList<>();
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
}
