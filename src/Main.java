import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class Main {
    public static LinkedList<BoardPiece> pieces = initChessPieces();
    public static BoardPiece selectedPiece = null;
    public static final int size = 80;
    public static void main(String[] args) throws IOException {
        Image[] chess_pieces = readChessPieces(size);
        drawFrameHandleInput(pieces, chess_pieces, size);
    }

    public static void drawFrameHandleInput(LinkedList<BoardPiece> pieces, Image[] chess_pieces, int size) {
        JFrame frame = drawFrame(pieces, chess_pieces, size);
        frame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null) {
                    selectedPiece.x = e.getX() - size / 2;
                    selectedPiece.y = e.getY() - size / 2;
                    frame.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                selectedPiece = getPiece(e.getX(), e.getY());
                if (selectedPiece != null) {
                    System.out.println("TRUE");
                    selectedPiece.printMoves(selectedPiece.getLegalSquares());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedPiece != null) selectedPiece.move(e.getX() / size, e.getY() / size);
                frame.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static JFrame drawFrame(LinkedList<BoardPiece> pieces, Image[] chess_pieces, int size) {
        final Color lightColor = new Color(0xEAE9D2);
        final Color darkColor = new Color(0x4B7399);
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, size * 8, size * 8 + 28);
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        boolean light = (row + col) % 2 == 0;
                        g.setColor(light ? lightColor : darkColor);
                        g.fillRect(col * size, row * size, size, size);
                    }
                }
                for (BoardPiece piece: pieces) {
                    int index = 0;
                    if (piece instanceof King) index = 0;
                    if (piece instanceof Queen) index = 1;
                    if (piece instanceof Bishop) index = 2;
                    if (piece instanceof Knight) index = 3;
                    if (piece instanceof Rook) index = 4;
                    if (piece instanceof Pawn) index = 5;
                    if (!piece.isWhite) index += 6;
                    g.drawImage(chess_pieces[index], piece.x, piece.y, this);
                }
            }
        };
        frame.add(panel);
        return frame;
    }

    public static Image[] readChessPieces(int size) throws IOException{
        BufferedImage chessPieces = ImageIO.read(Objects.requireNonNull(Main.class.getResourceAsStream("/chess_pieces.png")));
        Image[] chess_pieces = new Image[12];
        int index = 0;
        for (int y = 0; y < 400; y += 200) {
            for (int x = 0; x < 1200; x += 200) {
                chess_pieces[index] = chessPieces.getSubimage(x, y, 200, 200).getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH);
                index++;
            }
        }
        return chess_pieces;
    }

    public static LinkedList<BoardPiece> initChessPieces() {
        LinkedList<BoardPiece> pieces = new LinkedList<>();
        BoardPiece piece1 = new Rook(0, 0,false, pieces, size);
        BoardPiece piece2 = new Knight(1, 0,false, pieces, size);
        BoardPiece piece3 = new Bishop(2, 0,false, pieces, size);
        BoardPiece piece4 = new Queen(3, 0,false, pieces, size);
        BoardPiece piece5 = new King(4, 0,false, pieces, size);
        BoardPiece piece6 = new Bishop(5, 0,false, pieces, size);
        BoardPiece piece7 = new Knight(6, 0,false, pieces, size);
        BoardPiece piece8 = new Rook(7, 0,false, pieces, size);
        BoardPiece piece9 = new Pawn(0, 1,false, pieces, size);
        BoardPiece piece10 = new Pawn(1, 1,false, pieces, size);
        BoardPiece piece11 = new Pawn(2, 1,false, pieces, size);
        BoardPiece piece12 = new Pawn(3, 1,false, pieces, size);
        BoardPiece piece13 = new Pawn(4, 1,false, pieces, size);
        BoardPiece piece14 = new Pawn(5, 1,false, pieces, size);
        BoardPiece piece15 = new Pawn(6, 1,false, pieces, size);
        BoardPiece piece16 = new Pawn(7, 1,false, pieces, size);
        BoardPiece piece17 = new Rook(0, 7,true, pieces, size);
        BoardPiece piece18 = new Knight(1, 7,true, pieces, size);
        BoardPiece piece19 = new Bishop(2, 7,true, pieces, size);
        BoardPiece piece20 = new Queen(3, 7,true, pieces, size);
        BoardPiece piece21 = new King(4, 7,true, pieces, size);
        BoardPiece piece22 = new Bishop(5, 7,true, pieces, size);
        BoardPiece piece23 = new Knight(6, 7,true, pieces, size);
        BoardPiece piece24 = new Rook(7, 7,true, pieces, size);
        BoardPiece piece25 = new Pawn(0, 6,true, pieces, size);
        BoardPiece piece26 = new Pawn(1, 6,true, pieces, size);
        BoardPiece piece27 = new Pawn(2, 6,true, pieces, size);
        BoardPiece piece28 = new Pawn(3, 6,true, pieces, size);
        BoardPiece piece29 = new Pawn(4, 6,true, pieces, size);
        BoardPiece piece30 = new Pawn(5, 6,true, pieces, size);
        BoardPiece piece31 = new Pawn(6, 6,true, pieces, size);
        BoardPiece piece32 = new Pawn(7, 6,true, pieces, size);
        return pieces;
    }

    public static BoardPiece getPiece(int x, int y) {
        int xPos = x / size;
        int yPos = y / size;
        for (BoardPiece piece: pieces) {
            if (piece.xPos == xPos && piece.yPos == yPos) {
                return piece;
            }
        }
        return null;
    }
}
