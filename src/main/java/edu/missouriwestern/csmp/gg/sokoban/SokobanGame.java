package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.logging.Logger;

public class SokobanGame extends Game {

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    /** loads boards at start of server */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        var maps = event.getApplicationContext().getBeansOfType(Board.class);
        for(var mapName : maps.keySet()) {
            this.addBoard(mapName, maps.get(mapName));
            logger.info(mapName + maps.get(mapName));
        }
    }
}
