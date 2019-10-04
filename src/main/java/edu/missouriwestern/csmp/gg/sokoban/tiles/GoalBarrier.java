package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Tile;
import edu.missouriwestern.csmp.gg.sokoban.entities.Box;
import net.sourcedestination.funcles.function.Function2;

import java.util.Map;

public class GoalBarrier extends Tile implements EventListener {
    public GoalBarrier(int column, int row) {
        super(column, row, "goal-barrier", 'G', Map.of(
                "impassable", "true"
        ));
    }

    @Override
    public void acceptEvent(Event event) {
        switch(event.getType()) {
            case "entity-moved": // if an entity moves on to us from a tile on our board, move it to destination
                var entity = event.getEntity().get();
                var tile = event.getTile();
                if(entity instanceof Box && // a box moved
                        tile.isPresent() && // to a tile
                        tile.get().getBoard() == getBoard() && // on our board
                        tile.get().getType().equals("goal") && // and it's a goal
                         getBoard().getTileStream()
                           .filter(t -> t.getType().equals("goal"))  // and all goals on our board
                           .allMatch(t -> t.getEntities().anyMatch(e -> e instanceof Box))) { // have a box
                    setProperty("impassable", "false");
                    setProperty("character", " ");
                }
                break;
        }
    }

    public static Function2<Integer,Integer,Tile> getGenerator() {
        return GoalBarrier::new;
    }

}
