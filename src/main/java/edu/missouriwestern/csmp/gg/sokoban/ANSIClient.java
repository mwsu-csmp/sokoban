package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.EntityMovedEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

// useful stuff:
// https://en.wikipedia.org/wiki/Miscellaneous_Symbols
// https://en.wikipedia.org/wiki/ANSI_escape_code
// https://en.wikipedia.org/wiki/Code_page_437
// https://shtrom.ssji.net/skb/getc.html  // need to do this in Java
public class ANSIClient extends Player implements Runnable {

    public static final String CSI = "\033[";
    public static final String CLEAR_SCREEN = CSI+"2J"+CSI+"1;1H";

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    private final Socket socket;
    private final ANSIGameServer server;
    private final PrintWriter out;
    private final InputStreamReader in;
    private final Game game;
    private final PlayerAvatar avatar;

    public ANSIClient(ANSIGameServer server, Socket socket, String playerId) throws IOException {
        super(playerId, server.getGame());
        this.socket = socket;
        this.server = server;
        this.game = server.getGame();
        this.avatar = new PlayerAvatar(game, this);
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new InputStreamReader(socket.getInputStream());
        game.registerListener(this);
    }

    @Override
    public void accept(Event e) {
        if(e instanceof EntityMovedEvent) {
            var location = game.getEntityLocation(avatar);

            // if the entity moved away from a tile...
            if(location != null && (location instanceof Tile)) {
                redrawTile((Tile) location);
            }

            // if the entity moved to another tile...
            if(e.hasProperty("column")) {
                redrawTile(
                        Integer.parseInt(e.getProperty("column")),
                        Integer.parseInt(e.getProperty("row")));
            }

            out.flush();
        }
    }

    @Override
    public void run() {
        logger.info("starting client handler");
        // TODO: command loop
out.print(CSI+"[?5h"); // turn off local echo
        avatar.reset();

        drawBoard();

        var connected = true;
        while(connected) { // read and issue commands
            try {
                int key = in.read();
                if(key == 'q') { // quit
                    connected = false;
                } else if(key == 'w') { // move up
                    moveAvatar(Direction.NORTH);
                } else if(key == 'a') { // move up
                    moveAvatar(Direction.WEST);
                } else if(key == 's') { // move up
                    moveAvatar(Direction.SOUTH);
                } else if(key == 'd') { // move up
                    moveAvatar(Direction.EAST);
                }
            } catch(Exception e) {
                logger.info("failed to read from client");
                connected = false;
            }
        }


        // TODO: cleanup / remove from server
        logger.info("client complete, cleaning up");
        try {
            socket.close();
        } catch(IOException e) {
            logger.info("failed to close client connection: " + e);
        }
        server.removeClient(this);
    }

    private void moveCursor(int col, int row) {
        out.println(CSI+row+";"+0+"f");
        out.print(CSI+col+"C");
    }

    private void redrawTile(Tile tile) {
        redrawTile(tile.getColumn(), tile.getRow());
    }

    private void redrawTile(int col, int row) {
        var location = game.getEntityLocation(avatar);
        if(location == null || !(location instanceof Tile)) {
            // avatar is not on a tile, thus not on a board that can be drawn. probably an error
            return;
        }
        var board = ((Tile)location).getBoard();
        var tile = board.getTile(col, row);
        var entity = tile.getEntities()
                .filter(ent -> ent.hasProperty("character"))
                .findFirst();

        moveCursor(col, row);
        if(entity.isPresent()) {
            out.print(entity.get().getProperty("character"));
        } else {
            out.print(tile.getProperty("character"));
        }
    }


    private void moveAvatar(Direction direction) {
        var location = game.getEntityLocation(avatar);
        if(!(location instanceof Tile)) return;
        var tile = (Tile)location;
        var board = tile.getBoard();
        var destination = board.getAdjacentTile(tile, direction);
        if(destination != null)
            game.moveEntity(avatar, destination);
    }

    private void drawEntities() {
        var location = game.getEntityLocation(avatar);
        if(location == null || !(location instanceof Tile)) {
            // avatar is not on a tile, thus not on a board that can be drawn. probably an error
            return;
        }
        var board = ((Tile)location).getBoard();

        board.getTileStream()
                .filter(t -> !t.isEmpty())
                .forEach(t -> {
                    t.getEntities()
                            .filter(e -> e.getProperties().containsKey("character"))
                            .forEach(e -> {
                                moveCursor(t.getColumn(), t.getRow());
                                out.print(e.getProperties().get("character"));
                            });
                });
    }

    private void drawBoard() {
        // TODO: inject color attributes
        var location = game.getEntityLocation(avatar);
        if(location == null || !(location instanceof Tile)) {
            // avatar is not on a tile, thus not on a board that can be drawn. probably an error
            return;
        }
        var board = ((Tile)location).getBoard();
        out.print(CLEAR_SCREEN);
        out.print(board.getTileMap());
        out.flush();
        drawEntities();
        moveCursor(board.getWidth(), board.getHeight());
        out.flush();
    }
}
