package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Entity;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("guide")
public class Guide extends Entity implements EventListener {

    @Autowired
    public Guide(@Qualifier("game") Game game) {
        super(game, Map.of("sprites", "sokoban-guide",
                           "character", "?",
                            "description", "a friendly guide"));
        game.registerListener(this);
    }

    public void accept(Event event) {
        if(event instanceof GameStartEvent) reset(); // move to spawn point at start of game
    }

    public void reset() {
        var foyer = getGame().getBoard("foyer");
        var location = foyer.getTileStream()  // find location of guide spawn tile (should be exactly 1)
                .filter(tile -> tile.getType().equals("guide-spawn"))
                .findFirst().get().getLocation();
        getGame().moveEntity(this, location);
    }

    public String getType() {
        return "npc";
    }

    public String toString() {
        return super.toString();
    }
}
