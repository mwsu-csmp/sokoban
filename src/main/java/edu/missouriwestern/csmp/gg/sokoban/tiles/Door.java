package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Tile;

import java.util.logging.Logger;

import java.util.Map;

public class Door extends Tile implements EventListener {

    private static Logger logger = Logger.getLogger(Door.class.getCanonicalName());

    private final String destination;
    private final int destColumn;
    private final int destRow;

    public Door(int column, int row, String destination, int destColumn, int destRow) {
        super(column, row, "door",
            Map.of("destination-board", destination,
                    "destination-row", ""+destRow,
                    "destination-column", ""+destColumn)
        );

        this.destColumn = destColumn;
        this.destRow = destRow;
        this.destination = destination;
    }

    @Override
    public void acceptEvent(Event event) {
        switch(event.getType()) {
            case "entity-moved": // if an entity moves on to us from a tile on our board, move it to destination
                if(event.hasProperty("previous-board") &&
                   event.getProperty("previous-board").equals(getBoard().getName())
                        && event.getTile().isPresent() && event.getTile().get() == this) {
                    var entity = event.getEntity().get();
                    logger.info("TILE INFO" + this.serializeProperties());
                    getGame().moveEntity(entity, getGame().getBoard(destination).getTile(destColumn, destRow));
                }
                break;
        }
    }
}
