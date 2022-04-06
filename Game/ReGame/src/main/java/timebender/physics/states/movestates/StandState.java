package timebender.physics.states.movestates;

import timebender.physics.Body;
import timebender.physics.utils.PointVector;
import timebender.physics.states.movecommands.IMoveCommand;

import static timebender.physics.states.movecommands.MoveCommandType.*;

public class StandState implements IMoveState {
    private Body bodyContext;

    public StandState(){}

    public StandState(Body bodyContext){
        this.bodyContext = bodyContext;
    }

    @Override
    public void setContext(Body bodyContext) {
        this.bodyContext = bodyContext;
    }

    @Override
    public IMoveState nextState(IMoveCommand moveCommand) {
        if(moveCommand.getCommandType() == LEFT_PRESSED)
            return new MoveLeftState(bodyContext);
        if(moveCommand.getCommandType() == RIGHT_PRESSED)
            return new MoveRightState(bodyContext);
        return this;
    }

    @Override
    public PointVector getMovementVector() {
        return new PointVector();
    }

    @Override
    public String toString(){
        return "StandState";
    }
}
