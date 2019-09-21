package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.Agent;
import edu.missouriwestern.csmp.gg.base.Game;

public class SokobanGame extends Game {

    @Override
    public Agent getAgent(String id, String role) {
        if(getPlayer(id) != null) return getPlayer(id);
        if(role.equals("player")) {
            var agent = new SokobanPlayer(id, this);
            addAgent(agent);
            return agent;
        }
        throw new IllegalArgumentException("no agent with id " + id +
                " and role " + role + " allowed.");
    }

}
