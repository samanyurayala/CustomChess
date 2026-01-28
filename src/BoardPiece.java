import java.util.ArrayList;

public class BoardPiece {
    public int xPos, yPos;
    public final int SIZE;
    public boolean isWhite;
    public int x, y;
    public boolean hasMoved;

    public BoardPiece(int xPos, int yPos, boolean isWhite, int size) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.SIZE = size;
        x = xPos * size;
        y = yPos * size;
        this.isWhite = isWhite;
        hasMoved = false;
    }

    public ArrayList<Vector2d> getLegalSquares(Game board) {
        return new ArrayList<>();
    }
}
