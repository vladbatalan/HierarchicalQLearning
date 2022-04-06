package timebender.physics.states.movestates;

import timebender.physics.Body;
import timebender.physics.JumpComponent;
import timebender.physics.utils.PointVector;
import timebender.physics.enums.CollisionTypes;
import timebender.physics.states.movecommands.IMoveCommand;

import static timebender.physics.states.movecommands.MoveCommandType.JUMP_RELEASED;

public class JumpState implements IMoveState {
    private Body bodyContext;
    private JumpComponent jumpComponent;

    public JumpState(){}

    public JumpState(Body bodyContext){
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
        if(moveCommand.getCommandType() == JUMP_RELEASED) {
            return new NoJumpState(bodyContext);
        }
        return this;
    }

    @Override
    public PointVector getMovementVector() {
        // Get the bottom colliding state of the body
        boolean isCollidingBottom = bodyContext.getCollisionState()[CollisionTypes.BOTTOM.getValue()];

        boolean canUpdateJump = jumpComponent.canUpdateJump(
                isCollidingBottom
        );

        // If the jump cannot be updated, transition back to NoJump
        if(!canUpdateJump) {
            bodyContext.changeJumpState(new NoJumpState(bodyContext));
        }

        return jumpComponent.getJumpForce();
    }

    @Override
    public String toString(){
        return "JumpState";
    }
}
