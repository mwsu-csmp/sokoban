package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Tile;
import edu.missouriwestern.csmp.gg.sokoban.entities.Box;
import edu.missouriwestern.csmp.gg.sokoban.entities.SokobanPlayer;
import net.sourcedestination.funcles.function.Function2;

import java.util.Map;

public class BoxSpawn extends Tile implements EventListener {
    public BoxSpawn(int column, int row) {
        super(column,row,"box-spawn", 'B',
                Map.of(
                        "character", "B",
                        "description", "where boxes appear"
                ));
    }

    @Override
    public void acceptEvent(Event event) {
        switch(event.getType()) {
            case "command":
                var player = getGame().getAgent(event.getProperty("username"));
                if(player instanceof SokobanPlayer &&
                        event.getProperty("command").equals("reset")) { // spawn a box
                    var playerLocation = getGame().getEntityLocation((SokobanPlayer)player);
                    if(playerLocation instanceof Tile &&
                            ((Tile) playerLocation).getBoard() == getBoard()) {
                        // if no box on this tile, make a new one
                        if(getEntities().noneMatch(ent -> ent.getType().equals("box")))
                            addEntity(new Box(getGame()));
                    }
                }
                break;
        }
    }

    public static Function2<Integer,Integer,Tile> getGenerator() {
        return BoxSpawn::new;
    }
}
