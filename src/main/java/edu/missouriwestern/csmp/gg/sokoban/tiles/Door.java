package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Tile;

public class Door extends Tile implements EventListener {
    public Door(Board board, int column, int row) {
        super(board, column, row, "door");
    }

    @Override
    public void acceptEvent(Event event) {
        switch(event.getType()) {
            case "entity-moved": // if an entity moves on to us from a tile on our board, move it to destination
                if(event.hasProperty("previous-board") &&
                   event.getProperty("previous-board").equals(getBoard().getName()) &&
                   event.getTile().isPresent() && event.getTile().get() == this) {
                    var entity = event.getEntity().get();
                    getGame().moveEntity(entity, getTile("destination-").get());
                }
                break;
        }
    }
}
