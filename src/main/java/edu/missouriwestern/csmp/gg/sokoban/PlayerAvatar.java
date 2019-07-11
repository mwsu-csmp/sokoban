package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.CommandEvent;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;

import java.util.Map;

public class PlayerAvatar extends Entity implements EventListener {

    private final Player player;

    public PlayerAvatar(Game game, Player player) {
        super(game, Map.of("sprites", "sokoban-player",
                "character", "☺",
                "description", "a heroic sokobon player"));
        this.player = player;
    }

    @Override
    public void accept(Event event) {
        if(event instanceof GameStartEvent) reset(); // move to spawn point at start of game
        if(event instanceof CommandEvent) {
            if(event.getProperty("player").equals(player.getID())) {
                switch(event.getProperty("command")) {
                    case "MOVE":
                        var location = getGame().getEntityLocation(this);
                        if(!(location instanceof Tile)) return;
                        var tile = (Tile)location;
                        var board = tile.getBoard();
                        var destination = board.getAdjacentTile(tile,
                                Direction.valueOf(event.getProperty("parameter")));
                        if(destination != null) {
                            if(destination.hasProperty("impassable") &&
                               !destination.getProperty("impassable").equals("false"))
                                break;  // can't walk on to an impassable tile

                            if(destination.getEntities()
                                    .filter(ent -> ent.hasProperty("impassable") &&
                                                   !ent.getProperty("impassable").equals("false"))
                                    .count() > 0) {
                                break;  // can't walk through boxes
                            }
                            getGame().moveEntity(this, destination);
                        }
                        break;
                }
            }
        }
    }

    public void reset() {
        var foyer = getGame().getBoard("foyer");
        var location = foyer.getTileStream()  // find location of guide spawn tile (should be exactly 1)
                .filter(tile -> tile.getType().equals("player-spawn"))
                .findFirst().get();
        getGame().moveEntity(this, location);
    }

    public String getType() {
        return "player-avatar";
    }

    public String toString() {
        return super.toString();
    }
}