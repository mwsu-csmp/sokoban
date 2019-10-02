package edu.missouriwestern.csmp.gg.sokoban.entities;

import edu.missouriwestern.csmp.gg.base.*;

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
    public void acceptEvent(Event event) {
        switch (event.getType()) {
            case "game-restart":
                reset();
                break;
            case "command":
                if (event.getProperty("username").equals(getAgentID())) {
                    switch (event.getProperty("command")) {
                        case "MOVE":
                            var location = getGame().getEntityLocation(this);
                            if (!(location instanceof Tile)) return;
                            var tile = (Tile) location;
                            var board = tile.getBoard();
                            var direction = Direction.valueOf(event.getProperty("parameter"));
                            var destination = board.getAdjacentTile(tile, direction);
                            if (destination != null) {
                                if (destination.hasProperty("impassable") &&
                                        !destination.getProperty("impassable").equals("false"))
                                    break;  // can't walk on to an impassable tile

                                if (destination.getEntities()
                                        .filter(ent -> ent.hasProperty("impassable") &&
                                                !ent.getProperty("impassable").equals("false"))
                                        .count() > 0) {
                                    // can't walk through boxes
                                    // but we can push them
                                    getGame().propagateEvent(new Event(getGame(), "push",
                                            Map.of(
                                                    "row", destination.row + "",
                                                    "column", destination.column + "",
                                                    "direction", direction.toString())));
                                    break;
                                }

                                if (destination.hasProperty("portal-destination-board")) {
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
                            if (!(location instanceof Tile)) return;
                            tile = (Tile) location;
                            board = tile.getBoard();

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
    public String getAgentID() {
        return id;
    }
}
