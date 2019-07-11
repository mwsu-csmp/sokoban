package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
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
                    try{
                        var clientSocket = socket.accept();
                        taskExecutor.execute(() -> {
                            try {server.processLogin(clientSocket);}
                            catch(IOException e) { logger.info("failed to connect client"); }
                        });
                    } catch(IOException e) { logger.info("failed to connect client"); }
                    logger.info("accepting incoming client connection");
                }
            }
        });
    }

    public Game getGame() { return game; }

    public void removeClient(ANSIClient client) {
        clients.remove(client);
    }

    public void processLogin(Socket socket) throws IOException {
        // TODO: manage login or login token
        var out = new PrintWriter(socket.getOutputStream());
        var in = new Scanner(socket.getInputStream());
        out.print("What is your name: ");
        out.flush();
        var name = in.nextLine().trim();
        while(game.getPlayer(name) != null) {
            out.print("That name is taken, try again: ");
            out.flush();
            name = in.nextLine().trim();
        }
        var client =  new ANSIClient(this, socket, name);
        clients.add(client);
        taskExecutor.execute(client);
    }

}
