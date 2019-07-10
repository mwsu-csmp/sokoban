package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.Player;
import edu.missouriwestern.csmp.gg.base.Tile;
import edu.missouriwestern.csmp.gg.base.events.EntityMovedEvent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

// useful stuff:
// https://en.wikipedia.org/wiki/Miscellaneous_Symbols
// https://en.wikipedia.org/wiki/ANSI_escape_code
// https://en.wikipedia.org/wiki/Code_page_437

public class ANSIClient extends Player implements Runnable {

    public static final String CSI = "\033[";
    public static final String CLEAR_SCREEN = CSI+"2J"+CSI+"1;1H";

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    private final Socket socket;
    private final ANSIGameServer server;
    private final PrintWriter out;
    private final Game game;
    private final PlayerAvatar avatar;

    public ANSIClient(ANSIGameServer server, Socket socket, String playerId) throws IOException {
        super(playerId, server.getGame());
        this.socket = socket;
        this.server = server;
        this.game = server.getGame();
        this.avatar = new PlayerAvatar(game, this);
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        game.registerListener(this);
    }

    @Override
    public void accept(Event e) {
        if(e instanceof EntityMovedEvent) {
            var location = game.getEntityLocation(avatar);
            if(location != null && (location instanceof Tile)) {
                var board = ((Tile) location).getBoard();
                var props = e.getProperties();
                if (props.containsKey("column")) {
                    int row = Integer.parseInt(props.get("row"));
                    int column = Integer.parseInt(props.get("column"));
                    moveCursor(column, row);
                    out.print(board.getTile(column,row).getProperties().get("character"));
                    var ent = game.getEntity(Integer.parseInt(props.get("entity")));
                    var newLoc = game.getEntityLocation(ent);
                    if(newLoc instanceof Tile) {
                        row = ((Tile)newLoc).getRow();
                        column = ((Tile)newLoc).getColumn();
                        moveCursor(column, row);
                        out.print(ent.getProperties().get("character"));
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        logger.info("starting client handler");
        // TODO: command loop

        avatar.reset();

        // temporary test code
        drawBoard();

        try {Thread.sleep(2000);} catch(Exception e) {}
        game.moveEntity(avatar, game.getBoard("foyer").getTile(3,3));

        try {Thread.sleep(2000);} catch(Exception e) {}
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
