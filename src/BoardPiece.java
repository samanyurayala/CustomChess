import java.util.LinkedList;

public class BoardPiece {
    public int xPos, yPos;
    public int scale;
    public boolean isWhite;
    public LinkedList<BoardPiece> pieces;
    public String name;
    public int x, y;

    public BoardPiece(int xPos, int yPos, String name, boolean isWhite, LinkedList<BoardPiece> pieces, int scale) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.scale = scale;
        x = xPos * scale;
        y = yPos * scale;
        this.isWhite = isWhite;
        this.pieces = pieces;
        this.name = name;
        pieces.add(this);
    }

    public void move(int xPos, int yPos) {
        BoardPiece piece = Main.getPiece(xPos * scale, yPos * scale);
        if (piece != null) {
            if (piece.isWhite != isWhite) {
                piece.kill();
            } else {
                x = this.xPos * scale;
                y = this.yPos * scale;
                return;
            }
        }
        this.xPos = xPos;
        this.yPos = yPos;
        x = xPos * scale;
        y = yPos * scale;
    }

    public void kill() {
        pieces.remove(this);
    }
}
