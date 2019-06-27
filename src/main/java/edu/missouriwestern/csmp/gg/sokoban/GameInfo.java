package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class GameInfo {


    @Autowired
    private Game game;

    @GetMapping("/board/{boardId}")
    @ResponseBody
    public String getBoardInfo(@PathVariable("boardId") String boardId) {
        //return game.getBoard(boardId).toString();
        return game.getBoard(boardId).toString();
    }
}
