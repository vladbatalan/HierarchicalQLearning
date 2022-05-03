package timebender.gameobjects.controllers;

import timebender.gameobjects.ObjectID;
import timebender.gameobjects.handlers.GameObjectHandler;
import timebender.levels.LevelFlagsSystem;
import timebender.physics.Body;
import timebender.physics.states.movecommands.MoveCommandType;
import timebender.physics.states.movecommands.MoveCommand;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import static timebender.levels.LevelFlagsSystem.*;

/**
 * Class responsible for transmitting the keyboard input to a Player object type.
 */
public class KeyboardController implements ICommandObservable {

    /**
     * The keys for the body. These can be changed anytime.
     */
    private final int MOVE_LEFT = KeyEvent.VK_LEFT;
    private final int MOVE_RIGHT = KeyEvent.VK_RIGHT;
    private final int JUMP = KeyEvent.VK_UP;
    private final int SPACE = KeyEvent.VK_SPACE;
    private final int SPECIAL_KEY = KeyEvent.VK_C;

    private final ArrayList<ICommandObserver> commandObservers = new ArrayList<>();

    private Body body;

    private final ObjectID objectID;

    public KeyboardController(Body body, ObjectID objectID) {
        this.body = body;
        this.objectID = objectID;
    }

    public void receiveKeyEvent(int key, boolean isPressEvent) {
        if(!isLevelRunning)
            return;

        MoveCommand received = null;
        if (isPressEvent) {
            switch (key) {
                case MOVE_LEFT -> received = new MoveCommand(MoveCommandType.LEFT_PRESSED);
                case MOVE_RIGHT -> received = new MoveCommand(MoveCommandType.RIGHT_PRESSED);
                case JUMP -> received = new MoveCommand(MoveCommandType.JUMP_PRESSED);
            }
        } else {
            switch (key) {
                case MOVE_LEFT -> received = new MoveCommand(MoveCommandType.LEFT_RELEASED);
                case MOVE_RIGHT -> received = new MoveCommand(MoveCommandType.RIGHT_RELEASED);
                case JUMP -> received = new MoveCommand(MoveCommandType.JUMP_RELEASED);
                case SPACE -> {
                    received = new MoveCommand(MoveCommandType.SPACE_RELEASED);
                    // Check if id of object is player, then set level flag
                    if(objectID == ObjectID.Player){
                        playerPressedSpaceEvent = true;
                    }
                }
                // TODO: To be removed
                case SPECIAL_KEY -> {
                    if (commandObservers.size() > 0) {
                        System.out.println(((ControllerBuilder) (commandObservers.get(0))).buildController().toString());
                    }
                }
            }
        }

        if (received != null) {
            // Set the current frame of the command
            received.setTimestamp(GameObjectHandler.GetFrameNumber());

            // Get the old body state
            String oldState = body.getMoveStateManager().getStateString();

            // Feed body with command
            body.getMoveStateManager().nextState(received);

            // Get the new state
            String newState = body.getMoveStateManager().getStateString();
            if (!oldState.equals(newState) || received.getCommandType() == MoveCommandType.SPACE_RELEASED) {

                // If body state changed, alert observers the command
                for (ICommandObserver observer : commandObservers) {
                    observer.receiveCommand(received);
                }
            }
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
