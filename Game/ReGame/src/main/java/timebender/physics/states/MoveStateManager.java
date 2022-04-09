package timebender.physics.states;

import timebender.physics.Body;
import timebender.physics.utils.PointVector;
import timebender.physics.states.movecommands.IMoveCommand;
import timebender.physics.states.movestates.IMoveState;
import timebender.physics.states.movestates.NoJumpState;
import timebender.physics.states.movestates.StandState;

import static timebender.physics.enums.Actions.*;

/**
 * Class responsable with the movement of the body.
 *
 * It has a specific state of movement and af jump.
 *
 * It uses commands in order to evaluate the next state.
 */
public class MoveStateManager {
    private Body bodyContext;
    private IMoveState moveState;
    private IMoveState jumpState;

    public MoveStateManager(Body bodyContext){
        this.bodyContext = bodyContext;

        this.moveState = new StandState(bodyContext);
        this.jumpState = new NoJumpState(bodyContext);
    }

    public void nextState(IMoveCommand command){
        String lastMove = moveState.toString() + jumpState.toString();

        moveState = moveState.nextState(command);
        jumpState = jumpState.nextState(command);

//        if(!(moveState.toString() + jumpState.toString()).equals(lastMove)) {
////            Logger.PrintEndline();
////            Logger.Print("MoveState: " + moveState);
////            Logger.Print("JumpState: " + jumpState);
//        }
    }

    public void changeMoveState(IMoveState nextState){
        moveState = nextState;
    }

    public void changeJumpState(IMoveState nextState){
        jumpState = nextState;
    }

    public PointVector getVelocity(){
        return moveState.getMovementVector();
    }

    public PointVector getJumpForce(){
        return  jumpState.getMovementVector();
    }

    public boolean[] getStateFlags(){
        boolean[] stateFlags = new boolean[3];

        stateFlags[MOVE_LEFT.getValue()] = moveState.toString().equals("MoveLeftState");
        stateFlags[MOVE_RIGHT.getValue()] = moveState.toString().equals("MoveRightState");
        stateFlags[JUMP.getValue()] = moveState.toString().equals("JumpState");

        return stateFlags;
    }

    public String getStateString(){
        return moveState.toString() + jumpState.toString();
    }
}
