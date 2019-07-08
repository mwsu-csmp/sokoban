package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Game;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ANSIClient implements Runnable {

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    private final Socket socket;
    private final ANSIGameServer server;
    private final PrintWriter out;
    private final Game game;

    public ANSIClient(ANSIGameServer server, Socket socket) throws IOException {
        this.socket = socket;
        this.server = server;
        this.game = server.getGame();
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public void run() {
        logger.info("starting client handler");
        // TODO: login

        // TODO: command loop

        // temporary test code
        out.println(game.getBoard("foyer"));out.flush();

        // TODO: cleanup / remove from server
        logger.info("client complete, cleaning up");
        try {
            socket.close();
        } catch(IOException e) {
            logger.info("failed to close client connection: " + e);
        }
        server.removeClient(this);
    }
}
