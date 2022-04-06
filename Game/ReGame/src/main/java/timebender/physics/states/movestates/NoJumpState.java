package timebender.physics.states.movestates;

import timebender.physics.Body;
import timebender.physics.JumpComponent;
import timebender.physics.utils.PointVector;
import timebender.physics.enums.CollisionTypes;
import timebender.physics.states.movecommands.IMoveCommand;

import static timebender.physics.states.movecommands.MoveCommandType.*;

public class NoJumpState implements IMoveState {
    private Body bodyContext;
    private JumpComponent jumpComponent;

    public NoJumpState(){}

    public NoJumpState(Body bodyContext){
        this.bodyContext = bodyContext;
        this.jumpComponent = bodyContext.getJumpComponent();
    }

    @Override
    public void setContext(Body bodyContext) {
        this.bodyContext = bodyContext;
        this.jumpComponent = bodyContext.getJumpComponent();
    }

    @Override
    public IMoveState nextState(IMoveCommand moveCommand) {
        if(moveCommand.getCommandType() == JUMP_PRESSED) {

            // Get the bottom colliding state of the body
            boolean isCollidingBottom = bodyContext.getCollisionState()[CollisionTypes.BOTTOM.getValue()];

            // Check if body is colliding with the floor, or it has Jump permission
            boolean canMakeTransition = jumpComponent.canInitJumpAction(
                    isCollidingBottom
            );

            if(canMakeTransition)
                return new JumpState(bodyContext);
        }
        return this;
    }

    @Override
    public PointVector getMovementVector() {
        jumpComponent.decreaseJumpForce();
        return jumpComponent.getJumpForce();
    }
    @Override
    public String toString(){
        return "NoJumpState";
    }
}
