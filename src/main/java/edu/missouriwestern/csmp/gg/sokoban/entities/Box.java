package edu.missouriwestern.csmp.gg.sokoban.entities;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.sokoban.tiles.BoxSpawn;

import java.util.Map;

public class Box extends Entity implements EventListener {

    public Box(Game game) {
        super(game, Map.of("sprites", "box-normal",
                "character", "☒",
                "impassable", "true",
                "description", "a heavy box"));
    }

    @Override
    public synchronized void acceptEvent(Event event) {
        var entityLocation = getGame().getEntityLocation(this);

        switch (event.getType()) {
            case "push":
                var location = event.getTile();
                if (location.isPresent() && location.get() == getGame().getEntityLocation(this)) {
                    // we're being pushed. Check to see if the adjacent location is unoccupied
                    var board = location.get().getBoard();
                    var dir = event.getDirection();
                    if(dir.isPresent()) {
                        var adjacentLocation = board.getAdjacentTile(location.get(), dir.get());
                        if (adjacentLocation != null &&
                                !adjacentLocation.hasProperty("impassable") &&
                                adjacentLocation.getEntities()
                                        .filter(ent -> ent.hasProperty("impassable"))
                                        .count() == 0) {
                            // if so, move the box to that location
                            getGame().moveEntity(this, adjacentLocation);
                        }
                    }
                }
                break;
            case "entity-moved":
                if (event.getProperty("entity").equals(this.getID() + "")) {
                    // we just moved. Check to see if we've reached a goal condition
                    if (entityLocation instanceof Tile) {
                        var tile = (Tile) entityLocation;
                        if (tile.getType().equals("goal")) {
                            this.setProperty("character", "☑");
                            this.setProperty("sprites", "box-in-goal");
                            getGame().propagateEvent(tile.tileStatusUpdateEvent());
                        } else { // reset icon to normal
                            this.setProperty("character", "☒");
                            this.setProperty("sprites", "box-normal");
                            getGame().propagateEvent(tile.tileStatusUpdateEvent());
                        }
                    }
                }
                break;
            case "command":
                var player = getGame().getAgent(event.getProperty("username"));
                // if a player resets in our room, and we're not on a box spawn...
                if(player instanceof SokobanPlayer &&
                        event.getProperty("command").equals("reset") &&
                        entityLocation instanceof Tile &&
                        !(entityLocation instanceof BoxSpawn)) { // ...then remove the box
                    getGame().removeEntity(this);
                }
                break;
        }
    }

    @Override
    public String getType() {
        return "box";
    }
}
