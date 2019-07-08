package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

@Component("server")
public class ANSIGameServer {

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    public final int PORT = 9000;
    private final List<ANSIClient> clients = new Vector<ANSIClient>();
    private final TaskExecutor taskExecutor;

    private final ServerSocket socket;
    private final Game game;

    public ANSIGameServer(@Autowired Game game, @Autowired TaskExecutor taskExecutor) throws IOException {
        socket = new ServerSocket(PORT);
        this.game = game;
        this.taskExecutor = taskExecutor;
        var server = this; // needed for anonymous class below
        // launch socket listening thread
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting up ANSI terminal server");
                while(true) {
                    try {
                        var client = new ANSIClient(server, socket.accept());
                        logger.info("accepting incoming client connection");
                        taskExecutor.execute(client);
                        clients.add(client);
                    } catch (IOException e) {
                        logger.info("Error connecting client: " + e);
                    }
                }
            }
        });
    }

    public Game getGame() { return game; }

    public void removeClient(ANSIClient client) {
        clients.remove(client);
    }
}
