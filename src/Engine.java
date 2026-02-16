import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Engine {
    private ProcessBuilder pb;
    private Process engine;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean isWhite;
    private boolean thinking;
    private static final Logger LOGGER = Logger.getLogger(Engine.class.getName());

    public Engine(String enginePath, boolean isWhite){
        this.isWhite = isWhite;
        thinking = false;
        String path = "resources/engines/" + enginePath + "/" + enginePath;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) path += ".exe";
        pb = new ProcessBuilder(path);
        try {
            engine = pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer = new BufferedWriter(new OutputStreamWriter(engine.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(engine.getInputStream()));
        try {
            writer.write("uci\n");
            writer.flush();
            String ok;
            while ((ok = reader.readLine()) != null) {
                if (ok.equals("uciok")) break;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading file", e);
        }
    }

    public String bestMove(String fen) {
        if (engine == null) return "";
        thinking = true;
        try {
            writer.write("position fen " + fen + "\n");
            writer.write("go movetime 1000\n");
            writer.flush();
            String reportedMove;
            while ((reportedMove = reader.readLine()) != null) {
                if (reportedMove.startsWith("bestmove")) {
                    thinking = false;
                    return reportedMove.split(" ")[1].trim();
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading file", e);
        }
        thinking = false;
        return "";
    }

    public synchronized void close() {
        try {
            if (writer != null) {
                writer.write("stop\n");
                writer.write("quit\n");
                writer.flush();
                writer.close();
                writer = null;
            }
            if (engine != null) {
                engine.destroy();
                if (!engine.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    engine.destroyForcibly();
                    engine.waitFor();
                }
                engine.getOutputStream().close();
                engine.getInputStream().close();
                engine.getErrorStream().close();
                engine = null;
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Error while closing engine", e);
        }
    }

    public boolean isWhite() {
        return isWhite;
    }

    public boolean isThinking() {
        return thinking;
    }

    public void setThinking(boolean thinking) {
        this.thinking = thinking;
    }
}
