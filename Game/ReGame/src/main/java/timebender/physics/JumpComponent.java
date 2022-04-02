package timebender.physics;

import timebender.physics.utils.PhysicsConstants;

import static timebender.physics.utils.PhysicsConstants.GRAVITY_ACCELERATION;
import static timebender.physics.utils.PhysicsConstants.JUMP_CONSTANT;

public class JumpComponent {
    // Jump variables
    private int jumpTimer = 0;
    private int jumpOnCollisionTime = 0;
    private boolean canJump = false;
    private PointVector jumpForce = new PointVector();


    /**
     * Method used to evaluate the transition from NoJumpState to JumpState.
     *
     * @param isBottomCollision a boolean that shows if the body collide at his bottom side.
     * @return a boolean that show if the transition can be performed.
     */
    public boolean canInitJumpAction(boolean isBottomCollision) {
        if (isBottomCollision || canJump) {
            canJump = false;
            jumpTimer = 0;
            jumpForce = new PointVector(GRAVITY_ACCELERATION.scalarMultiply(-JUMP_CONSTANT));
            return true;
        }
        return false;
    }

    /**
     * Method called by Jump state to evaluate when to change back to NoJumpState.
     *
     * @param isBottomCollision a boolean that shows if the body collide at his bottom side.
     * @return a boolean that show if the transition can be performed.
     */
    public boolean canUpdateJump(boolean isBottomCollision) {
        jumpTimer++;
        jumpForce = jumpForce.add(GRAVITY_ACCELERATION);

        if (jumpTimer > 10 && isBottomCollision) {
            jumpOnCollisionTime++;
        } else {
            jumpOnCollisionTime = 0;
        }

        if (jumpForce.getY() > 0 || (jumpTimer > 10 && isBottomCollision && jumpOnCollisionTime > 5)) {
            jumpForce = new PointVector();
            return false;
        }
        return true;
    }

    /**
     * Method used when the body is in NoJumpState.
     * <p>
     * It slowly decrease the jump force by a ratio.
     * <p>
     * If it is small enough, it will be aproximated to 0.
     */
    public void decreaseJumpForce() {
        // emulating the stop of a jump by the player
        jumpForce = jumpForce.scalarMultiply(PhysicsConstants.JUMP_DECREASE_RATIO);
        if (jumpForce.abs() < 0.1)
            jumpForce = new PointVector();
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    public PointVector getJumpForce() {
        return jumpForce;
    }
}
