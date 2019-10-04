package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Tile;
import net.sourcedestination.funcles.function.Function2;

import java.util.Map;

public class PlayerSpawn extends Tile {

    public PlayerSpawn(int column, int row) {
        super(column,row,"player-spawn", '*',
                Map.of(
                        "character", "*",
                        "description", "starting spot for entering players"
                ));
    }
    public static Function2<Integer,Integer,Tile> getGenerator() {
        return PlayerSpawn::new;
    }
}
