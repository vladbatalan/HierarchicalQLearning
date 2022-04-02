package timebender.gameobjects.controllers;

import timebender.physics.Body;
import timebender.physics.states.MoveCommandType;
import timebender.physics.states.movecommands.MoveCommand;

import java.awt.event.KeyEvent;

/**
 * Class responsible for transmitting the keyboard input to a Player object type.
 */
public class KeyboardController {

    /**
     * The keys for the body. These can be changed anytime.
     */
    private final int MOVE_LEFT = KeyEvent.VK_LEFT;
    private final int MOVE_RIGHT = KeyEvent.VK_RIGHT;
    private final int JUMP = KeyEvent.VK_UP;
    private final int SPACE = KeyEvent.VK_SPACE;

    private Body body;

    public KeyboardController(Body body){
        this.body = body;
    }

    public void receiveKeyEvent(int key, boolean isPressEvent){
        MoveCommand received = null;
        if(isPressEvent){
            switch (key){
                case MOVE_LEFT -> received = new MoveCommand(MoveCommandType.LEFT_PRESSED);
                case MOVE_RIGHT -> received = new MoveCommand(MoveCommandType.RIGHT_PRESSED);
                case JUMP -> received = new MoveCommand(MoveCommandType.JUMP_PRESSED);
            }
        }
        else{
            switch (key){
                case MOVE_LEFT -> received = new MoveCommand(MoveCommandType.LEFT_RELEASED);
                case MOVE_RIGHT -> received = new MoveCommand(MoveCommandType.RIGHT_RELEASED);
                case JUMP -> received = new MoveCommand(MoveCommandType.JUMP_RELEASED);
                case SPACE -> received = new MoveCommand(MoveCommandType.SPACE_RELEASED);
            }
        }
        
        if(received != null){
            // Feed body with command
            body.getMoveStateManager().nextState(received);

            //TODO: If body state changed, alert observers the command
        }
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

}
