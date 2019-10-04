package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Tile;
import net.sourcedestination.funcles.function.Function2;

import java.util.Map;

public class Wall extends Tile {
    public Wall(int column, int row) {
        super(column, row, "wall", '#', Map.of(
                "impassable", "true"
        ));
    }

    public static Function2<Integer,Integer,Tile> getGenerator() {
        return Wall::new;
    }
}
