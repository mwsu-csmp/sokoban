package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;

import java.util.logging.Logger;

public class EventLogger implements EventListener {

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());

    @Override
    public void accept(Event event) {
        logger.info(event.toString());
    }
}
