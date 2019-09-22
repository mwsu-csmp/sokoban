package edu.missouriwestern.csmp.gg.sokoban.tiles;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Tile;

import java.util.Map;

public class Wall extends Tile {
    public Wall(Board board, int column, int row) {
        super(board, column, row, "wall", Map.of(
                "impassable", "true"
        ));
    }

}
