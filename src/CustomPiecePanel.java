import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Map;

public class CustomPiecePanel extends JPanel implements MouseListener, MouseMotionListener {
    private final Color MEDIUM_COLOR = new Color(0x8EB2C2);
    private final int SIZE;
    private ArrayList<BoardPiece> pieces;
    private Map<String, Image> customPieces;

    public CustomPiecePanel(int size) {
        SIZE = size;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(MEDIUM_COLOR);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void setCustomPieces(Map<String, Image> customPieces) {
        this.customPieces = customPieces;
    }

    public Map<String, Image> getCustomPieces() {
        return customPieces;
    }
}
