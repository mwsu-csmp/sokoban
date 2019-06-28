package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component("game")
public class SokobanGame extends Game {

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    /** loads boards at start of server */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        var maps = event.getApplicationContext().getBeansOfType(Board.class);
        for(var mapName : maps.keySet()) {
            this.addBoard(mapName, maps.get(mapName));
            logger.info("loading map " + mapName + ": \n" + maps.get(mapName));
        }
        registerListener(new EventLogger());  // log all events
        accept(new GameStartEvent(this));     // indicate game is starting
    }

    /** loads a text file resource as a string */
    public static String loadMap(String mapFileName) throws IOException {
        var mapString = new BufferedReader(new InputStreamReader(
                SokobanGame.class.getClassLoader()
                        .getResourceAsStream(mapFileName)))
                .lines().collect(Collectors.joining("\n"));
        return mapString;
    }
}
