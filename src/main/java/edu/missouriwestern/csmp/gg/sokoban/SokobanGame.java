package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.sokoban.entities.SokobanPlayer;
import edu.missouriwestern.csmp.gg.sokoban.tiles.PlayerSpawn;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class SokobanGame extends Game {

    private static Logger logger = Logger.getLogger(SokobanGame.class.getCanonicalName());
    private Tile spawn; // player spawn

    public SokobanGame(DataStore dataStore,
                EventListener eventPropagator,
                Consumer<EventListener> incomingEventCallback,
                String initialMapName,
                Board ... boards) {
        super(dataStore, eventPropagator, incomingEventCallback, boards);
    }

    @Override
    public void addBoard(Board board) {
        logger.info("adding board " + board);
        var spawn = board.getTileStream()
                .filter(tile -> tile instanceof PlayerSpawn)
                .findFirst();
        if (spawn.isPresent()) this.spawn = spawn.get();

        logger.info("spawn? : " + spawn);
        super.addBoard(board);
    }

    @Override
    public Agent getAgent(String id, String role) {
        if(getAgent(id) != null) return getAgent(id);
        if(role.equals("player")) {
            var agent = new SokobanPlayer(id, this);
            addAgent(agent);
            this.moveEntity(agent, spawn);
            return agent;
        }
        throw new IllegalArgumentException("no agent with id " + id +
                " and role " + role + " allowed.");
    }

}
