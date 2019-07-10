package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameInfo {

    @Autowired
    private Game game;

    @GetMapping(value = "/map/{boardId}", produces = "text/plain; charset=UTF-8")
    @ResponseBody
    public String getBoardMap(@PathVariable("boardId") String boardId) {
        var board = game.getBoard(boardId);
        return board.getTileMap();
    }

    @GetMapping(value = "/board/{boardId}", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getBoardInfo(@PathVariable("boardId") String boardId) {
        return game.getBoard(boardId).toString();
    }

    @GetMapping(value = "/tile/{boardId}/{column}/{row}", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getTileInfo(@PathVariable("boardId") String boardId,
                              @PathVariable("column") int column,
                              @PathVariable("row") int row) {
        var board = game.getBoard(boardId);
        var tile = board.getTile(row, column);
        return tile.toString();
    }

    @GetMapping(value = "/game", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getGameInfo() {
        return game.toString();
    }

    @GetMapping(value = "/entity/{id}", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public String getEntityInfo(@PathVariable("id") int id) {
        var entity = game.getEntity(id);
        return entity.toString();
    }
}
