import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
    private final Map<Class<? extends BoardPiece>, Integer> SPRITES = Map.of(
            King.class, 0,
            Queen.class, 1,
            Bishop.class, 2,
            Knight.class, 3,
            Rook.class, 4,
            Pawn.class, 5
    );
    private final Color LIGHT_COLOR = new Color(0xEAE9D2);
    private final Color DARK_COLOR = new Color(0x4B7399);
    private final int SIZE;
    private ArrayList<BoardPiece> pieces;
    private Image[] chess_pieces;
    private Game game;

    public BoardPanel(int size, ArrayList<BoardPiece> pieces, Image[] chess_pieces, Game chessGame) {
        setFocusable(true);
        this.SIZE = size;
        this.pieces = pieces;
        this.chess_pieces = chess_pieces;
        this.game = chessGame;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    public void makeEngineMove() {
        if (!game.isWhiteTurn()) {
            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() throws Exception {
                    return game.engine.bestMove(game.readFenFromPosition(pieces));
                }

                @Override
                protected void done() {
                    try {
                        String move = get();
                        System.out.println(move);
                        game.makeMove(move);
                        repaint();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            worker.execute();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? LIGHT_COLOR : DARK_COLOR);
                g.fillRect(col * SIZE, row * SIZE, SIZE, SIZE);
            }
        }
        for (BoardPiece piece: pieces) {
            int index = SPRITES.get(piece.getClass());
            if (!piece.isWhite()) index += 6;
            g.drawImage(chess_pieces[index], piece.getX(), piece.getY(), this);
        }
        makeEngineMove();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        game.selectPiece(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        game.dropPiece(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        game.movePiece(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'l') {
            loadGame();
        }
        if (e.getKeyChar() == 'r') {
            game = new Game(SIZE, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"); // Starting game FEN
            pieces = game.getPieces();
            repaint();
        }
        if (e.getKeyChar() == 'c') {
            customGame();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void loadGame() {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home")));
        chooser.setDialogTitle("Select Game");
        chooser.setFileFilter(new FileNameExtensionFilter(".fen, .txt", "fen", "txt"));
        chooser.setAcceptAllFileFilterUsed(false);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String fen;
            try {
                fen = Files.readString(file.toPath()).trim();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            game = new Game(SIZE, fen);
            this.pieces = game.getPieces();
            repaint();
        }
    }

    public void customGame() {
        JDialog dialog = new JDialog(new Frame(), "Custom Game", true);
        JLayeredPane pane = new JLayeredPane();
        pane.setLayout(null);
        pane.setPreferredSize(new Dimension(SIZE * 6, SIZE * 4));
        dialog.setLayout(new BorderLayout());
        EditPanel edit = new EditPanel(SIZE / 2, new ArrayList<>(), chess_pieces, game);
        edit.setBounds(0, 0, SIZE * 7, SIZE * 4);
        pane.add(edit, JLayeredPane.DEFAULT_LAYER);
        JButton turn = new JButton("White to move");
        turn.addActionListener(e -> {
            turn.setText(edit.onTurnButtonPressed());
        });
        turn.setBounds(SIZE * 4, SIZE * 7 / 2, SIZE, SIZE / 2);
        turn.setFont(new Font("Rubik", Font.ITALIC, 11));
        JButton getFEN = new JButton("FEN");
        getFEN.addActionListener(e -> {
            StringSelection fen = new StringSelection(edit.getFen());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(fen, null);
        });
        getFEN.setBounds(SIZE * 9 / 2, SIZE * 3, SIZE / 2, SIZE / 2);
        getFEN.setFont(new Font("Rubik", Font.PLAIN, 14));
        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            String fen = edit.getFen();
            if (fen.equals("Not valid position")) return;
            game = new Game(SIZE, fen);
            pieces = game.getPieces();
            repaint();
        });
        start.setBounds(SIZE * 4, SIZE * 3, SIZE / 2, SIZE / 2);
        start.setFont(new Font("Rubik", Font.BOLD, 12));
        JCheckBox whiteCastleKing = new JCheckBox("O-O (W)");
        whiteCastleKing.setSelected(true);
        whiteCastleKing.addActionListener(e -> {
            edit.getWhiteCanCastle()[1] = whiteCastleKing.isSelected();
        });
        whiteCastleKing.setBounds(SIZE * 5, 0, SIZE, SIZE / 2);
        whiteCastleKing.setFont(new Font("Rubik", Font.BOLD, 12));
        JCheckBox whiteCastleQueen = new JCheckBox("O-O-O (W)");
        whiteCastleQueen.setSelected(true);
        whiteCastleQueen.addActionListener(e -> {
            edit.getWhiteCanCastle()[0] = whiteCastleQueen.isSelected();
        });
        whiteCastleQueen.setBounds(SIZE * 5, SIZE / 2, SIZE, SIZE / 2);
        whiteCastleQueen.setFont(new Font("Rubik", Font.BOLD, 10));
        JCheckBox blackCastleKing = new JCheckBox("O-O (B)");
        blackCastleKing.setSelected(true);
        blackCastleKing.addActionListener(e -> {
            edit.getBlackCanCastle()[1] = blackCastleKing.isSelected();
        });
        blackCastleKing.setBounds(SIZE * 5, SIZE, SIZE, SIZE / 2);
        blackCastleKing.setFont(new Font("Rubik", Font.BOLD, 12));
        JCheckBox blackCastleQueen = new JCheckBox("O-O-O (B)");
        blackCastleQueen.setSelected(true);
        blackCastleQueen.addActionListener(e -> {
            edit.getBlackCanCastle()[0] = blackCastleQueen.isSelected();
        });
        blackCastleQueen.setBounds(SIZE * 5, SIZE * 3 / 2, SIZE, SIZE / 2);
        blackCastleQueen.setFont(new Font("Rubik", Font.BOLD, 10));
        pane.add(turn, JLayeredPane.PALETTE_LAYER);
        pane.add(getFEN, JLayeredPane.PALETTE_LAYER);
        pane.add(start, JLayeredPane.PALETTE_LAYER);
        pane.add(whiteCastleKing, JLayeredPane.PALETTE_LAYER);
        pane.add(whiteCastleQueen, JLayeredPane.PALETTE_LAYER);
        pane.add(blackCastleKing, JLayeredPane.PALETTE_LAYER);
        pane.add(blackCastleQueen, JLayeredPane.PALETTE_LAYER);
        dialog.add(pane, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
