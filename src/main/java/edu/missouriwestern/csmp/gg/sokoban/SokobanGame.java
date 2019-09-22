package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Agent;
import edu.missouriwestern.csmp.gg.base.DataStore;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.sokoban.entities.SokobanPlayer;
import edu.missouriwestern.csmp.gg.sokoban.tiles.BoxSpawn;
import edu.missouriwestern.csmp.gg.sokoban.tiles.Door;
import edu.missouriwestern.csmp.gg.sokoban.tiles.GoalBarrier;
import edu.missouriwestern.csmp.gg.sokoban.tiles.Wall;

import java.util.Map;
import java.util.function.Consumer;

public class SokobanGame extends Game {

    public SokobanGame(DataStore dataStore,
                EventListener eventPropagator,
                Consumer<EventListener> incomingEventCallback) {
        super(dataStore, eventPropagator, incomingEventCallback,
                Map.of(
                        "door", Door::new,
                        "box-spawn", BoxSpawn::new,
                        "wall", Wall::new,
                        "goal-barrier", GoalBarrier::new
                ));
    }

    @Override
    public Agent getAgent(String id, String role) {
        if(getAgent(id) != null) return getAgent(id);
        if(role.equals("player")) {
            var agent = new SokobanPlayer(id, this);
            addAgent(agent);
            return agent;
        }
        throw new IllegalArgumentException("no agent with id " + id +
                " and role " + role + " allowed.");
    }

}
