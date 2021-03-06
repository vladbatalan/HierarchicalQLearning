package timebender.physics.states.movestates;

import timebender.physics.Body;
import timebender.physics.utils.PointVector;
import timebender.physics.states.movecommands.IMoveCommand;

import static timebender.physics.states.movecommands.MoveCommandType.*;

public class MoveLeftState implements IMoveState {
    private Body bodyContext;

    public MoveLeftState(){}

    public MoveLeftState(Body bodyContext){
        this.bodyContext = bodyContext;
    }

    @Override
    public void setContext(Body bodyContext) {
        this.bodyContext = bodyContext;
    }

    @Override
    public IMoveState nextState(IMoveCommand moveCommand) {
        if(moveCommand.getCommandType() == LEFT_RELEASED)
            return new StandState(bodyContext);
        if(moveCommand.getCommandType() == RIGHT_PRESSED)
            return new MoveRightState(bodyContext);
        return this;
    }

    @Override
    public PointVector getMovementVector() {
        return new PointVector(-bodyContext.getMovementSpeed(),0);
    }

    @Override
    public String toString(){
        return "MoveLeftState";
    }
}
