package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.CommandEvent;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;

import java.util.Map;

public class SokobanPlayer extends Entity implements Agent {

    private final String id;

    public SokobanPlayer(String id, Game game) {
        super(game, Map.of("sprites", "player",
                "character", "☺",
                "description", "a heroic sokobon player"));
        this.id = id;
    }

    @Override
    public void accept(Event event) {
        if(event instanceof GameStartEvent) reset(); // move to spawn point at start of game
        if(event instanceof CommandEvent) {
            if(event.getProperty("player").equals(getAgentID())) {
                switch(event.getProperty("command")) {
                    case "MOVE":
                        var location = getGame().getEntityLocation(this);
                        if(!(location instanceof Tile)) return;
                        var tile = (Tile)location;
                        var board = tile.getBoard();
                        var direction = Direction.valueOf(event.getProperty("parameter"));
                        var destination = board.getAdjacentTile(tile, direction);
                        if(destination != null) {
                            if(destination.hasProperty("impassable") &&
                                    !destination.getProperty("impassable").equals("false"))
                                break;  // can't walk on to an impassable tile

                            if(destination.getEntities()
                                    .filter(ent -> ent.hasProperty("impassable") &&
                                            !ent.getProperty("impassable").equals("false"))
                                    .count() > 0) {
                                // can't walk through boxes
                                // but we can push them
                                getGame().accept(new PushEvent(getGame(), destination, direction));
                                break;
                            }

                            if(destination.hasProperty("portal-destination-board")) {
                                destination = getGame()  // update destination to new board
                                        .getBoard(destination.getProperty("portal-destination-board"))
                                        .getTileStream()
                                        .filter(t -> t.hasProperty("entering-entity-spawn"))
                                        .findFirst().get();
                            }

                            getGame().moveEntity(this, destination);
                        }
                        break;
                    case "RESET":
                        location = getGame().getEntityLocation(this);
                        if(!(location instanceof Tile)) return;
                        tile = (Tile)location;
                        board = tile.getBoard();

                        // remove all boxes from board
                        board.getTileStream()
                                .filter(t -> t.getEntities()
                                        .filter(ent -> ent.getType().equals("box"))
                                        .findFirst().isPresent())
                                .forEach(t ->
                                        t.getEntities()
                                                .filter(ent -> ent.getType().equals("box"))
                                                .forEach(ent -> getGame().removeEntity(ent)));

                        // add a box to each box spawn
                        board.getTileStream()
                                .filter(t -> t.getType().equals("box-spawn"))
                                .forEach(t -> t.addEntity(new Box(getGame())));
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

    @Override
    public String getType() {
        return "player";
    }

    @Override
    public String getRole() {
        return getType();
    }

    @Override
    public String getAgentID() { return id; }
}
