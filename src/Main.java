import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        final Color lightColor = new Color(0xeeeed2);
        final Color darkColor = new Color(0x769656);
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 512, 540);
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        boolean light = (row + col) % 2 == 0;
                        g.setColor(light ? lightColor : darkColor);
                        g.fillRect(col * 64, row * 64, 64, 64);
                    }
                }
            }
        };
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
