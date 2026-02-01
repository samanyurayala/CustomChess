import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class Sound {
    private static Clip move;
    private static Clip check;
    private static Clip capture;
    private static Clip end;
    private static Clip castle;
    private static Clip promote;

    public Sound() {
        move = load("/sounds/move-self.wav");
        check = load("/sounds/move-check.wav");
        capture = load("/sounds/capture.wav");
        end = load("/sounds/game-end.wav");
        castle = load("/sounds/castle.wav");
        promote = load("/sounds/promote.wav");
    }

    public static Clip load(String s) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(Sound.class.getResource(s)));
            Clip sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            return sound;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private void play(Clip sound) {
        if (sound.isRunning()) {
            sound.stop();
        }
        sound.setFramePosition(0);
        sound.start();
    }

    public void playMove() {
        play(move);
    }

    public void playCheck() {
        play(check);
    }

    public void playCapture() {
        play(capture);
    }

    public void playEnd() {
        play(end);
    }

    public void playCastle() {
        play(castle);
    }

    public void playPromote() {
        play(promote);
    }
}
