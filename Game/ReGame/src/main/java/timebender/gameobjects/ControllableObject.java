package timebender.gameobjects;

import timebender.physics.Body;

import java.awt.*;

import static timebender.physics.enums.Actions.*;

public abstract class ControllableObject extends MobileObject{

    protected int lastAction = MOVE_RIGHT.getValue();

    public ControllableObject(Body body) {
        super(body);
    }

    @Override
    public void Draw(Graphics g) {
        boolean[] stateFlags = body.getStateFlags();

        // Player is currently jumping
        if(stateFlags[JUMP.getValue()]) {

            if(stateFlags[MOVE_LEFT.getValue()]) {
                lastAction = MOVE_LEFT.getValue();
            }

            if(stateFlags[MOVE_RIGHT.getValue()]) {
                lastAction = MOVE_RIGHT.getValue();
            }
            JumpAnimation(g);
            return;
        }

        // Object is not jumping, then check if it is moving left or right
        if(stateFlags[MOVE_LEFT.getValue()]){

            lastAction = MOVE_LEFT.getValue();
            MoveLeftAnimation(g);
            return;
        }

        if(stateFlags[MOVE_RIGHT.getValue()]){
            lastAction = MOVE_RIGHT.getValue();
            MoveRightAnimation(g);
            return;
        }

        // Player is standing
        StandAnimation(g);
    }

}
