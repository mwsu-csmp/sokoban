package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;

import java.util.Map;

public class PlayerAvatar extends Entity {

    public PlayerAvatar(Game game, Player player) {
        super(game, Map.of("sprites", "sokoban-player",
                "character", "☺",
                "description", "a heroic sokobon player"));
    }

    public void accept(Event event) {
        if(event instanceof GameStartEvent) reset(); // move to spawn point at start of game
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