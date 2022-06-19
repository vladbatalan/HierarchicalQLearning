package timebender.gameobjects.controllers;

import timebender.gameobjects.ObjectID;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.physics.Body;
import timebender.physics.states.movecommands.MoveCommand;
import timebender.physics.states.movecommands.MoveCommandType;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static timebender.levels.LevelFlagsSystem.*;

/**
 * Class responsible for transmitting the keyboard input to a Player object type.
 */
public class ExternalController implements ICommandObservable {

    private final ArrayList<ICommandObserver> commandObservers = new ArrayList<>();

    private Body body;

    private final ObjectID objectID;

    public ExternalController(Body body, ObjectID objectID) {
        this.body = body;
        this.objectID = objectID;
    }

    public void receiveCommend(MoveCommand command) {
        if(!isLevelRunning)
            return;

        if(command == null || command.getCommandType() == MoveCommandType.NO_ACTION)
            return;

        if(command.getCommandType() == MoveCommandType.SPACE_RELEASED){
            // Check if id of object is player, then set level flag
            if(objectID == ObjectID.Player){
                playerPressedSpaceEvent = true;
            }
        }

        command.setTimestamp(GameObjectHandler.GetFrameNumber());

        // Get the old body state
        String oldState = body.getMoveStateManager().getStateString();

        // Feed body with command
        body.getMoveStateManager().nextState(command);

        // Get the new state
        String newState = body.getMoveStateManager().getStateString();
        if (!oldState.equals(newState) || command.getCommandType() == MoveCommandType.SPACE_RELEASED) {

            unsuccessfulCommand = false;
            // If body state changed, alert observers the command
            for (ICommandObserver observer : commandObservers) {
                observer.receiveCommand(command);
            }
        }
        else{
            // The command does not change the state
            unsuccessfulCommand = true;
        }
    }

    @Override
    public void attachObserver(ICommandObserver observer) {
        commandObservers.add(observer);
    }

    @Override
    public void detachObserver(ICommandObserver observer) {
        commandObservers.remove(observer);
    }

    @Override
    public void clearObservers() {
        commandObservers.clear();
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

}
