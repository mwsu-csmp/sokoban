package edu.missouriwestern.csmp.gg.sokoban.entities;

import edu.missouriwestern.csmp.gg.base.*;

import java.util.Map;
import java.util.logging.Logger;

public class Guide extends Entity implements EventListener {

    public static final int GUIDE_MOVEMENT_TIMEOUT = 1000; // how often (msec) the guide moves
    private static Logger logger = Logger.getLogger(Guide.class.getCanonicalName());
    private final String[] messages = {"Welcome to Sokoban!",
            "I'll guide you around!",
            "solve puzzles for a prize!",
            "each door above leads to a puzzle!"};

    public Guide(Game game) {
        super(game, Map.of("sprites", "sokoban-guide",
                "character", "?",
                "description", "a friendly guide"));
        game.registerListener(this);
    }

    public void acceptEvent(Event event) {
        switch (event.getType()) {
            case "game-start":
                reset(); // move to spawn point at start of game
                getGame().propagateEvent(new Event(getGame(), "timer",
                        Map.of(
                               "entity", ""+getID()
                        )), GUIDE_MOVEMENT_TIMEOUT);
                break;
            case "command": // see if someone wants you to talk to them
                if (event.getProperty("command").equals("INTERACT")) {
                    var player = getGame().getAgent(event.getProperty("player"));
                    if (player instanceof SokobanPlayer) {
                        var avatar = ((SokobanPlayer) player);
                        var avatarLocation = getGame().getEntityLocation(avatar);
                        if (avatarLocation instanceof Tile) {
                            var tile = (Tile) avatarLocation;
                            var board = tile.getBoard();
                            var target = board.getAdjacentTile(tile, Direction.valueOf(event.getProperty("parameter")));
                            if (target == getGame().getEntityLocation(this)) { // someone is interacting with us
                                getGame().propagateEvent(new Event(getGame(), "speech",
                                        Map.of("entity", this.getID() + "",
                                                "message", messages[(int) (Math.random() * messages.length)])));
                            }
                        }
                    }

                }
                break;
            case "timer":
                if(event.getProperty("entity").equals(getID()+"")) { // walk aimlessly
                    var direction = Direction.values()[(int) (Math.random() * 4)];
                    var location = (Tile) getGame().getEntityLocation(this);
                    var board = location.getBoard();
                    var destination = board.getAdjacentTile(location, direction);
                    if (destination != null && !destination.hasProperty("impassable")) {
                        destination.addEntity(this);
                    }
                    getGame().propagateEvent(new Event(getGame(), "timer",
                            Map.of(
                                    "entity", ""+getID()
                            )), GUIDE_MOVEMENT_TIMEOUT);
                }
        }
    }

    public void reset() {
        var foyer = getGame().getBoard("foyer");
        var location = foyer.getTileStream()  // find location of guide spawn tile (should be exactly 1)
                .filter(tile -> tile.getType().equals("guide-spawn"))
                .findFirst().get();
        getGame().moveEntity(this, location);
    }
    public String getType() {
        return "npc";
    }

    public String toString() {
        return super.toString();
    }
}
