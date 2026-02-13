import java.io.*;

public class Engine {
    private ProcessBuilder pb;
    private Process engine;
    BufferedWriter writer;
    BufferedReader reader;

    public Engine(String enginePath){
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String bestMove(String fen) {
        try {
            writer.write("position fen " + fen + "\n");
            writer.flush();
            writer.write("go movetime 1000\n");
            writer.flush();
            String reportedMove;
            while ((reportedMove = reader.readLine()) != null) {
                if (reportedMove.startsWith("bestmove")) {
                    return reportedMove.split(" ")[1].trim();
                }
            }
            return "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void close() {
        try {
            writer.write("quit\n");
            writer.flush();
            engine.destroy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
