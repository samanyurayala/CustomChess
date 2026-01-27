import java.util.ArrayList;

public class BoardPiece {
    public int xPos, yPos;
    public int scale;
    public boolean isWhite;
    public ArrayList<BoardPiece> pieces;
    public int x, y;
    public boolean hasMoved;

    public BoardPiece(int xPos, int yPos, boolean isWhite, ArrayList<BoardPiece> pieces, int scale) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.scale = Main.size;
        x = xPos * scale;
        y = yPos * scale;
        this.isWhite = isWhite;
        this.pieces = pieces;
        pieces.add(this);
        hasMoved = false;
    }

    public void move(int xPos, int yPos) {
        BoardPiece piece = Main.getPiece(xPos * scale, yPos * scale);
        ArrayList<Vector2d> legalSquares = getLegalSquares();
        Vector2d testVector = new Vector2d(xPos, yPos);
        if (!legalSquares.contains(testVector) || !(Main.isWhiteTurn == isWhite)) {
            x = this.xPos * scale;
            y = this.yPos * scale;
            return;
        }
        if (piece != null && piece.isWhite != isWhite) {
            piece.kill();
        }
        this.xPos = xPos;
        this.yPos = yPos;
        x = xPos * scale;
        y = yPos * scale;
        if (!hasMoved) hasMoved = true;
        Main.isWhiteTurn = !Main.isWhiteTurn;
    }

    public void kill() {
        pieces.remove(this);
    }

    public ArrayList<Vector2d> getLegalSquares() {
        return null;
    }

    public void printMoves(ArrayList<Vector2d> moves) {
        for (Vector2d move : moves) {
            System.out.print(move.x + " " + move.y + " ");
        }
    }

    public BoardPiece getPieceVec2D(Vector2d vec) {;
        for (BoardPiece piece: pieces) {
            if (piece.xPos == vec.x && piece.yPos == vec.y) {
                return piece;
            }
        }
        return null;
    }
}
