package timebender.physics.states;

import timebender.physics.Body;
import timebender.physics.PointVector;

/**
 * Interface that encapsulate a moving state of a body.
 *
 * The context of the movement is the body.
 *
 * A state returns a movementVector that is used in body in order cu calculate the next
 * position.
 */

public interface IMoveState {
    void setContext(Body bodyContext);
    IMoveState nextState(IMoveCommand moveCommand);

    PointVector getMovementVector();
}
