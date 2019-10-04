package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Tile;
import net.sourcedestination.funcles.function.Function2;

import java.util.Map;

public class Floor extends Tile {
    public Floor(int column, int row) {
        super(column,row,"floor", ' ',
                Map.of(
                        "character", " ",
                        "description", "nothing to see here"
                ));
    }
    public static Function2<Integer,Integer,Tile> getGenerator() {
        return Floor::new;
    }

}
