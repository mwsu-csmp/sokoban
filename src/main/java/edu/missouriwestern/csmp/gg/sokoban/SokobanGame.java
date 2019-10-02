package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.sokoban.entities.SokobanPlayer;
import edu.missouriwestern.csmp.gg.sokoban.tiles.BoxSpawn;
import edu.missouriwestern.csmp.gg.sokoban.tiles.Door;
import edu.missouriwestern.csmp.gg.sokoban.tiles.GoalBarrier;
import edu.missouriwestern.csmp.gg.sokoban.tiles.Wall;

import java.util.Map;
import java.util.function.Consumer;

public class SokobanGame extends Game {

    private Tile spawn; // player spawn
    private final String initialMapName;

    public SokobanGame(DataStore dataStore,
                EventListener eventPropagator,
                Consumer<EventListener> incomingEventCallback,
                String initialMapName) {
        super(dataStore, eventPropagator, incomingEventCallback,
                Map.of(
                        "box-spawn", BoxSpawn::new,
                        "wall", Wall::new,
                        "goal-barrier", GoalBarrier::new
                ));

        this.initialMapName = initialMapName;
    }

    @Override
    public void addBoard(String boardId, Board board) {
        if (boardId.equals(initialMapName)) {
            var spawn = board.getTileStream()
                    .filter(tile -> tile.getType().equals("player-spawn"))
                    .findFirst();
            if (spawn.isPresent()) this.spawn = spawn.get();
        }
        super.addBoard(boardId, board);
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
