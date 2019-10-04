package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Tile;
import net.sourcedestination.funcles.function.Function2;

import java.util.Map;

public class Goal extends Tile {
    public Goal(int column, int row) {
        super(column,row,"goal", 'G',
                Map.of(
                        "character", "G",
                        "description", "nothing to see here"
                ));
    }

    public static Function2<Integer,Integer,Tile> getGenerator() {
        return Goal::new;
    }
}
