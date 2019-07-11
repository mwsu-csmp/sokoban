package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Entity;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.events.EntityMovedEvent;

import java.util.Map;

public class Box extends Entity implements EventListener {

    public Box(Game game) {
        super(game, Map.of("sprites", "box-normal",
                "character", "☒",
                "impassable", "true",
                "description", "a heavy box"));
    }


    @Override
    public void accept(Event event) {
        if(event instanceof PushEvent) {
            var pushEvent = (PushEvent)event;
            var location = pushEvent.getLocation();
            if(location == getGame().getEntityLocation(this)) {
                // we're being pushed. Check to see if the adjacent location is unoccupied
                var board = location.getBoard();
                var adjacentLocation = board.getAdjacentTile(location, pushEvent.getDirection());
                if(adjacentLocation != null &&
                    !adjacentLocation.hasProperty("impassable") &&
                    adjacentLocation.getEntities()
                            .filter(ent -> ent.hasProperty("impassable"))
                            .count() == 0) {
                    // if so, move the box to that location
                    getGame().moveEntity(this, adjacentLocation);
                }

            }
        } else if(event instanceof EntityMovedEvent &&
            ((EntityMovedEvent)event).getEntity() == this) {
            // we just moved. Check to see if we've reached a goal condition

        }
    }

    @Override
    public String getType() { return "box"; }
}
