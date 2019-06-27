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
}
