package timebender.physics;

import timebender.Logger;
import timebender.map.Map;
import timebender.physics.states.IMoveState;
import timebender.physics.states.MoveStateManager;

import java.awt.*;

import static timebender.physics.enums.CollisionTypes.*;
import static timebender.physics.utils.PhysicsConstants.GravityForce;

public class Body {

    // A body has a Width and a Height
    private final int bodyWidth;
    private final int bodyHeight;

    // Physics metrices
    private PointVector position;
    private final float mass;
    private float movementSpeed = 4f;

    // All collision sides
    private boolean[] collisionState = new boolean[5];

    // State of movement
    private final MoveStateManager moveStateManager;

    // The component that manage the jump state of the body.
    private final JumpComponent jumpComponent = new JumpComponent();


    private Color bodyColor = new Color(0, 0, 0, 0);

    public Body(PointVector position, int bodyWidth, int bodyHeight, float mass) {

        this.position = position;
        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
        this.mass = mass;
        this.moveStateManager = new MoveStateManager(this);

    }

    /**
     * Method responsible for updating the state of the object
     *
     * @param currentMap The map for providing environmental details.
     */
    public void update(Map currentMap) {

        // Create all the forces that contribute to nextPosition
        PointVector resultantForce = new PointVector();
        PointVector velocity = moveStateManager.getVelocity();
        PointVector jumpForce = moveStateManager.getJumpForce();

        // Logger.Print("Jump force: " + jumpForce.toString());

        // Calculate next position and check for collision
        resultantForce = resultantForce
                .add(velocity)
                .add(GravityForce(mass))
                .add(jumpForce);

        // Evaluate the next position to get the colliders
        PointVector nextPosition = new PointVector(position.add(resultantForce));

        // Check if is out of map bounds
        nextPosition = nextPosition.setInMapBounds(
                bodyWidth,
                bodyHeight,
                currentMap.getMaxBounds()
        );

        // Request the collision state from the interaction with the map
        collisionState = currentMap.checkCollision(
                new Rectangle(
                        (int) nextPosition.getX(),
                        (int) nextPosition.getY(),
                        bodyWidth,
                        bodyHeight));


        //apply corrections to resultantForce
        resultantForce = nextPosition.sub(position);

        // Ajust the resultant force based on collisionStates
        resultantForce = adjustResultingForceOnCollision(resultantForce);

        PointVector oldPosition = new PointVector(position);

        // Update the position
        position = position.add(resultantForce);//nextPosition;//.sub(resultantForce);

//        if(!oldPosition.equals(position)){
//            Logger.Print("Position changed to " + position.toString());
//        }
//
//        Logger.Print(resultantForce.toString());

    }

    /**
     * Method responsible for drawing the Body of the character.
     *
     * @param g The graphic element used for drawing.
     */
    public void draw(Graphics g) {
        g.setColor(bodyColor);
        g.fillRect((int) position.getX(), (int) position.getY(), bodyWidth, bodyHeight);
    }

    public PointVector adjustResultingForceOnCollision(PointVector resultantForce) {

        // This function takes into consideration the current resultant force
        // and the current Collision state which is modified by interaction with the map, or base on
        // a obj-obj interaction that stops the player from moving

        //top Collision
        if (collisionState[TOP.getValue()]) {
            if (resultantForce.getY() < 0) {
                resultantForce.setY(0);
            }
        }
        //bottom Collision
        if (collisionState[BOTTOM.getValue()]) {
            if (resultantForce.getY() > 0) {
                resultantForce.setY(0);
            }
        }
        //right Collision
        if (collisionState[RIGHT.getValue()]) {
            if (resultantForce.getX() > 0) {
                resultantForce.setX(0);
            }
        }
        //left Collision
        if (collisionState[LEFT.getValue()]) {
            if (resultantForce.getX() < 0) {
                resultantForce.setX(0);
            }
        }

        return resultantForce;
    }

    /**
     * Method responsible for changing the moving state of the body
     *
     * @param nextState the next state must be Stand, MoveLeft or MoveRight
     */
    public void changeMoveState(IMoveState nextState) {
        moveStateManager.changeMoveState(nextState);
    }

    /**
     * Method responsible for changing the jumping state of the body
     *
     * @param nextState the next state must be Jump Pressed or Jump Released
     */
    public void changeJumpState(IMoveState nextState) {
        moveStateManager.changeJumpState(nextState);
    }

    public void setCanJump(boolean value) {
        jumpComponent.setCanJump(value);
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setPosition(PointVector position) {
        this.position = position;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public PointVector getPosition() {
        return position;
    }

    public void setBodyColor(Color color) {
        this.bodyColor = color;
    }

    public int getBodyWidth() {
        return bodyWidth;
    }

    public int getBodyHeight() {
        return bodyHeight;
    }

    public boolean[] getCollisionState() {
        return collisionState;
    }

    public Rectangle getHitBox() {
        return new Rectangle((int) position.getX(), (int) position.getY(), bodyWidth, bodyHeight);
    }

    public PointVector getGravityForce() {
        return GravityForce(mass);
    }

    public JumpComponent getJumpComponent() {
        return jumpComponent;
    }

    public MoveStateManager getMoveStateManager() {
        return moveStateManager;
    }
}

