package edu.missouriwestern.csmp.gg.sokoban;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.CommandEvent;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component("guide")
public class Guide extends Entity implements EventListener, Runnable {

    private static Logger logger = Logger.getLogger(Guide.class.getCanonicalName());
    private final String[] messages = {"Welcome to Sokoban!",
            "I'll guide you around!",
            "solve puzzles for a prize!",
            "each door above leads to a puzzle!"};
    @Autowired
    public Guide(@Qualifier("game") Game game) {
        super(game, Map.of("sprites", "sokoban-guide",
                           "character", "?",
                            "description", "a friendly guide"));
        game.registerListener(this);
    }

    public void accept(Event event) {
        if(event instanceof GameStartEvent) reset(); // move to spawn point at start of game
        else if(event instanceof CommandEvent) { // see if someone wants you to talk to them
            var command = (CommandEvent)event;
            if(command.getProperty("command").equals("INTERACT")) {
                var player = getGame().getPlayer(command.getProperty("player"));
                if(player instanceof ANSIClient) { // TODO: this cast sucks, shouldn't be tied to client, rethink approach
                    var avatar = ((ANSIClient)player).getAvatar();
                    var avatarLocation = getGame().getEntityLocation(avatar);
                    if(avatarLocation instanceof Tile) {
                        var tile = (Tile)avatarLocation;
                        var board = tile.getBoard();
                        var target = board.getAdjacentTile(tile, Direction.valueOf(command.getProperty("parameter")));
                        if(target == getGame().getEntityLocation(this)) { // someone is interacting with us
                            getGame().accept(new SpeechEvent(getGame(), this,
                                    messages[(int)(Math.random()*messages.length)]));
                        }
                    }
                }

            }
        }
    }

    public void reset() {
        var foyer = getGame().getBoard("foyer");
        var location = foyer.getTileStream()  // find location of guide spawn tile (should be exactly 1)
                .filter(tile -> tile.getType().equals("guide-spawn"))
                .findFirst().get();
        getGame().moveEntity(this, location);
    }

    @Override
    public void run() {
        while(true) { // walk randomly indefinitely
            try { Thread.sleep(4000); } catch(Exception e) {}
            logger.info("guide walking around");
            var direction = Direction.values()[(int)(Math.random()*4)];
            var location = (Tile)getGame().getEntityLocation(this);
            var board = location.getBoard();
            var destination = board.getAdjacentTile(location, direction);
            if(destination != null && !destination.hasProperty("impassable")) {
                destination.addEntity(this);
            }
        }
    }

    public String getType() {
        return "npc";
    }

    public String toString() {
        return super.toString();
    }
}
